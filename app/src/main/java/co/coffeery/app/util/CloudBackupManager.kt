package co.coffeery.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import co.coffeery.app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CloudBackupManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("cloud", Context.MODE_PRIVATE)

    fun isSignedIn(): Boolean = prefs.getBoolean("signed_in", false)
    fun getAccountEmail(): String? = prefs.getString("account_email", null)

    fun getProfilePhotoUrl(): android.net.Uri? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account?.photoUrl
    }

    fun isPlayServicesAvailable(): Boolean {
        val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        return result == ConnectionResult.SUCCESS
    }

    suspend fun silentSignIn(): Result<GoogleSignInAccount> = withContext(Dispatchers.IO) {
        try {
            val client = getSignInClient()
            val account = Tasks.await(client.silentSignIn())
            if (account != null) {
                prefs.edit()
                    .putBoolean("signed_in", true)
                    .putString("account_email", account.email)
                    .apply()
                Result.success(account)
            } else {
                Result.failure(Exception("Silent sign-in returned null"))
            }
        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(context.getString(R.string.google_server_client_id))
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(client: GoogleSignInClient): Intent = client.signInIntent

    fun handleSignInResult(data: Intent?, onResult: (Boolean, String) -> Unit) {
        try {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                prefs.edit()
                    .putBoolean("signed_in", true)
                    .putString("account_email", account.email)
                    .apply()
                Log.d("Coffeery", "Google Sign-In SUCCESS: ${account.email}")
                onResult(true, account.email ?: "")
            } else {
                onResult(false, "Account was null")
            }
        } catch (e: ApiException) {
            val code = e.statusCode
            val msg = when (code) {
                12500 -> "Error 12500: Check Support Email in GCC OAuth consent screen, and SHA-1 matches"
                12501 -> "Error 12501: Sign-in cancelled or Web Client ID mismatch"
                10 -> "Error 10: SHA-1 fingerprint mismatch. Run ./gradlew signingReport"
                8 -> "Error 8: Network error. Check internet connection."
                13 -> "Error 13: Play Services outdated. Update Google Play Services."
                15 -> "Error 15: Wrong package name or SHA-1 in GCC"
                else -> "Error $code: ${e.localizedMessage ?: "Unknown"}"
            }
            Log.e("Coffeery", "Google Sign-In FAILED: $msg", e)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            onResult(false, msg)
        } catch (e: Exception) {
            val msg = "Error: ${e.javaClass.simpleName} — ${e.localizedMessage ?: "Unknown"}"
            Log.e("Coffeery", "Google Sign-In CRASH: $msg", e)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            onResult(false, msg)
        }
    }

    suspend fun backupToDrive(activity: Activity, jsonData: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context) ?: return@withContext Result.failure(Exception("Not signed in"))
            val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_APPDATA))
            credential.selectedAccount = account.account

            val httpTransport = com.google.api.client.http.javanet.NetHttpTransport()
            val jsonFactory = com.google.api.client.json.gson.GsonFactory.getDefaultInstance()
            val drive = com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Coffeery")
                .build()

            val content = com.google.api.client.http.ByteArrayContent.fromString("application/json", jsonData)
            val existing = drive.files().list()
                .setSpaces("appDataFolder")
                .setQ("name='coffeery_backup.json' and trashed=false")
                .setFields("files(id,name)")
                .setPageSize(1)
                .execute()
                .files
            val file = if (!existing.isNullOrEmpty()) {
                val fileId = existing.first().id
                drive.files().update(fileId, com.google.api.services.drive.model.File(), content)
                    .setFields("id,name,modifiedTime")
                    .execute()
            } else {
                val metadata = com.google.api.services.drive.model.File()
                    .setName("coffeery_backup.json")
                    .setParents(listOf("appDataFolder"))
                    .setMimeType("application/json")
                drive.files().create(metadata, content)
                    .setFields("id,name,modifiedTime")
                    .execute()
            }
            Result.success(file.name ?: "coffeery_backup.json")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFromDrive(context: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context) ?: return@withContext Result.failure(Exception("Not signed in"))
            val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_APPDATA))
            credential.selectedAccount = account.account

            val httpTransport = com.google.api.client.http.javanet.NetHttpTransport()
            val jsonFactory = com.google.api.client.json.gson.GsonFactory.getDefaultInstance()
            val drive = com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Coffeery")
                .build()

            val files = drive.files().list()
                .setSpaces("appDataFolder")
                .setQ("name='coffeery_backup.json' and trashed=false")
                .setFields("files(id,name,modifiedTime)")
                .setOrderBy("modifiedTime desc")
                .setPageSize(1)
                .execute()
                .files
            if (files.isNullOrEmpty()) return@withContext Result.failure(Exception("No backup found"))

            val latestFile = files.first()
            val outputStream = java.io.ByteArrayOutputStream()
            drive.files().get(latestFile.id).executeMediaAndDownloadTo(outputStream)
            Result.success(outputStream.toString("UTF-8"))
        } catch (e: GoogleJsonResponseException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut(client: GoogleSignInClient) {
        prefs.edit().putBoolean("signed_in", false).remove("account_email").apply()
        client.signOut().addOnCompleteListener {
            client.revokeAccess().addOnCompleteListener {
                Log.d("Coffeery", "Google Sign-Out complete")
            }
        }
    }
}
