package co.coffeery.app.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import co.coffeery.app.BuildConfig
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
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@Suppress("DEPRECATION")
class CloudBackupManager(private val context: Context) {

    private val appContext: Context = context.applicationContext

    companion object {
        private val httpTransport by lazy { NetHttpTransport() }
        private val jsonFactory by lazy { GsonFactory.getDefaultInstance() }
    }

    private val prefs = appContext.getSharedPreferences("cloud", Context.MODE_PRIVATE)

    private var pendingRecoverableIntent: Intent? = null

    fun getRecoverableIntent(): Intent? = pendingRecoverableIntent

    fun consumeRecoverableIntent(): Intent? = pendingRecoverableIntent.also { pendingRecoverableIntent = null }

    fun clearRecoverableIntent() {
        pendingRecoverableIntent = null
    }

    private fun clearSignInPrefs() {
        prefs.edit().putBoolean("signed_in", false).remove("account_email").apply()
    }

    class RecoverableAuthException(val intent: Intent, cause: Throwable) : IOException("Authorization required — please grant Drive permission", cause)

    fun isSignedIn(): Boolean {
        val flagged = prefs.getBoolean("signed_in", false)
        val account = GoogleSignIn.getLastSignedInAccount(appContext)
        if (flagged && account == null) {
            clearSignInPrefs()
            return false
        }
        return flagged && account != null
    }

    fun getAccountEmail(): String? = prefs.getString("account_email", null)

    fun getProfilePhotoUrl(): android.net.Uri? {
        val account = GoogleSignIn.getLastSignedInAccount(appContext)
        return account?.photoUrl
    }

    fun isPlayServicesAvailable(): Boolean {
        val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appContext)
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
                clearSignInPrefs()
                Result.failure(Exception(appContext.getString(R.string.settings_cloud_error)))
            }
        } catch (e: ApiException) {
            clearSignInPrefs()
            Result.failure(e)
        } catch (e: Exception) {
            clearSignInPrefs()
            Result.failure(e)
        }
    }

    fun getSignInClient(): GoogleSignInClient {
        val resToken = try {
            appContext.getString(R.string.google_server_client_id)
        } catch (_: Exception) {
            ""
        }
        val cfgToken = try {
            BuildConfig.GOOGLE_SERVER_CLIENT_ID
        } catch (_: Exception) {
            ""
        }
        val token = when {
            cfgToken.isNotBlank() && cfgToken != resToken -> cfgToken
            resToken.isNotBlank() -> resToken
            else -> cfgToken
        }
        val gsoBuilder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
        if (token.isNotBlank()) {
            gsoBuilder.requestIdToken(token)
        }
        val gso = gsoBuilder.build()
        return GoogleSignIn.getClient(appContext, gso)
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
                clearSignInPrefs()
                onResult(false, appContext.getString(R.string.settings_cloud_error))
            }
        } catch (e: ApiException) {
            clearSignInPrefs()
            val code = e.statusCode
            val msg = when (code) {
                12500 -> appContext.getString(R.string.cloud_error_12500)
                12501 -> appContext.getString(R.string.cloud_error_12501)
                10 -> appContext.getString(R.string.cloud_error_10)
                8 -> appContext.getString(R.string.cloud_error_8)
                13 -> appContext.getString(R.string.cloud_error_13)
                15 -> appContext.getString(R.string.cloud_error_15)
                else -> appContext.getString(R.string.cloud_error_generic, code, e.localizedMessage ?: appContext.getString(R.string.cloud_error_unknown))
            }
            Log.e("Coffeery", "Google Sign-In FAILED: $msg", e)
            Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show()
            onResult(false, msg)
        } catch (e: Exception) {
            clearSignInPrefs()
            val msg = appContext.getString(R.string.cloud_error_exception, e.javaClass.simpleName, e.localizedMessage ?: appContext.getString(R.string.cloud_error_unknown))
            Log.e("Coffeery", "Google Sign-In CRASH: $msg", e)
            Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show()
            onResult(false, msg)
        }
    }

    private fun buildDrive(account: GoogleSignInAccount, ctx: Context): Drive {
        @Suppress("DEPRECATION") // GoogleAccountCredential is deprecated but still required for DriveScopes.DRIVE_APPDATA
        val credential = GoogleAccountCredential.usingOAuth2(appContext, listOf(DriveScopes.DRIVE_APPDATA))
        credential.selectedAccount = account.account
        val wrapped: HttpRequestInitializer = HttpRequestInitializer { request ->
            @Suppress("DEPRECATION") // credential.initialize is deprecated
            credential.initialize(request)
            request.connectTimeout = 15_000
            request.readTimeout = 15_000
        }
        return Drive.Builder(httpTransport, jsonFactory, wrapped)
            .setApplicationName("Coffeery")
            .build()
    }

    private fun extractRecoverableIntent(e: Exception): Intent? {
        return try {
            val m = e.javaClass.getMethod("getIntent")
            m.invoke(e) as? Intent
        } catch (_: Exception) {
            null
        }
    }

    private fun isUserRecoverable(e: Exception): Boolean {
        val name = e.javaClass.name
        return name.contains("UserRecoverableAuthIOException") || name.contains("UserRecoverableAuthException")
    }

    suspend fun backupToDrive(jsonData: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(appContext)
            if (account == null) {
                Log.e("Coffeery", "backupToDrive failed: Not signed in")
                return@withContext Result.failure(Exception(appContext.getString(R.string.cloud_error_not_signed_in)))
            }
            val drive = buildDrive(account, appContext)
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
            Log.d("Coffeery", "backupToDrive success: ${file.name}")
            Result.success(file.name ?: "coffeery_backup.json")
        } catch (e: UserRecoverableAuthIOException) {
            pendingRecoverableIntent = e.intent
            Log.w("Coffeery", "backupToDrive recoverable auth required", e)
            Result.failure(RecoverableAuthException(e.intent, e))
        } catch (e: GoogleJsonResponseException) {
            Log.e("Coffeery", "backupToDrive Drive API error ${e.statusCode}: ${e.details?.message ?: e.message}", e)
            val msg = when (e.statusCode) {
                401, 403 -> appContext.getString(R.string.cloud_error_auth_failed, e.statusCode)
                404 -> appContext.getString(R.string.cloud_error_404)
                429 -> appContext.getString(R.string.cloud_error_429)
                else -> appContext.getString(R.string.cloud_error_drive, e.statusCode, e.details?.message ?: e.message ?: "")
            }
            Result.failure(Exception(msg, e))
        } catch (e: IOException) {
            if (isUserRecoverable(e as Exception)) {
                val intent = extractRecoverableIntent(e as Exception)
                if (intent != null) {
                    pendingRecoverableIntent = intent
                    Log.w("Coffeery", "backupToDrive recoverable auth (generic) required", e)
                    return@withContext Result.failure(RecoverableAuthException(intent, e))
                }
            }
            Log.e("Coffeery", "backupToDrive network/IO error", e)
            Result.failure(Exception(appContext.getString(R.string.cloud_error_network, e.message ?: ""), e))
        } catch (e: Exception) {
            if (isUserRecoverable(e)) {
                val intent = extractRecoverableIntent(e)
                if (intent != null) {
                    pendingRecoverableIntent = intent
                    Log.w("Coffeery", "backupToDrive recoverable auth (generic) required", e)
                    return@withContext Result.failure(RecoverableAuthException(intent, e))
                }
            }
            Log.e("Coffeery", "backupToDrive failed", e)
            Result.failure(e)
        }
    }

    @Suppress("DEPRECATION") // keep compatibility for callers passing Activity; uses applicationContext internally
    suspend fun backupToDrive(activity: android.app.Activity, jsonData: String): Result<String> {
        return backupToDrive(jsonData)
    }

    suspend fun restoreFromDrive(): Result<String> = withContext(Dispatchers.IO) {
        restoreInternal(appContext)
    }

    suspend fun restoreFromDrive(context: Context): Result<String> = withContext(Dispatchers.IO) {
        restoreInternal(context.applicationContext)
    }

    private suspend fun restoreInternal(ctx: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(appContext)
            if (account == null) {
                Log.e("Coffeery", "restoreFromDrive failed: Not signed in")
                return@withContext Result.failure(Exception(appContext.getString(R.string.cloud_error_not_signed_in)))
            }
            val drive = buildDrive(account, ctx)
            val files = drive.files().list()
                .setSpaces("appDataFolder")
                .setQ("name='coffeery_backup.json' and trashed=false")
                .setFields("files(id,name,modifiedTime)")
                .setOrderBy("modifiedTime desc")
                .setPageSize(1)
                .execute()
                .files
            if (files.isNullOrEmpty()) {
                Log.w("Coffeery", "restoreFromDrive: No backup found")
                return@withContext Result.failure(Exception(appContext.getString(R.string.cloud_error_no_backup)))
            }
            val latestFile = files.first()
            val outputStream = java.io.ByteArrayOutputStream()
            drive.files().get(latestFile.id).executeMediaAndDownloadTo(outputStream)
            Log.d("Coffeery", "restoreFromDrive success: ${latestFile.name}")
            Result.success(outputStream.toString("UTF-8"))
        } catch (e: UserRecoverableAuthIOException) {
            pendingRecoverableIntent = e.intent
            Log.w("Coffeery", "restoreFromDrive recoverable auth required", e)
            Result.failure(RecoverableAuthException(e.intent, e))
        } catch (e: GoogleJsonResponseException) {
            Log.e("Coffeery", "restoreFromDrive Drive API error ${e.statusCode}: ${e.details?.message ?: e.message}", e)
            val msg = when (e.statusCode) {
                401, 403 -> appContext.getString(R.string.cloud_error_auth_failed, e.statusCode)
                404 -> appContext.getString(R.string.cloud_error_404)
                429 -> appContext.getString(R.string.cloud_error_429)
                else -> appContext.getString(R.string.cloud_error_drive, e.statusCode, e.details?.message ?: e.message ?: "")
            }
            Result.failure(Exception(msg, e))
        } catch (e: IOException) {
            if (isUserRecoverable(e as Exception)) {
                val intent = extractRecoverableIntent(e as Exception)
                if (intent != null) {
                    pendingRecoverableIntent = intent
                    Log.w("Coffeery", "restoreFromDrive recoverable auth (generic) required", e)
                    return@withContext Result.failure(RecoverableAuthException(intent, e))
                }
            }
            Log.e("Coffeery", "restoreFromDrive network/IO error", e)
            Result.failure(Exception(appContext.getString(R.string.cloud_error_network, e.message ?: ""), e))
        } catch (e: Exception) {
            if (isUserRecoverable(e)) {
                val intent = extractRecoverableIntent(e)
                if (intent != null) {
                    pendingRecoverableIntent = intent
                    Log.w("Coffeery", "restoreFromDrive recoverable auth (generic) required", e)
                    return@withContext Result.failure(RecoverableAuthException(intent, e))
                }
            }
            Log.e("Coffeery", "restoreFromDrive failed", e)
            Result.failure(e)
        }
    }

    fun signOut(client: GoogleSignInClient) {
        pendingRecoverableIntent = null
        clearSignInPrefs()
        client.signOut().addOnCompleteListener {
            client.revokeAccess().addOnCompleteListener {
                Log.d("Coffeery", "Google Sign-Out complete")
            }
        }
    }
}
