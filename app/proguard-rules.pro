# Coffeery — keep rules.
# Room generates code at build time; no runtime reflection rules needed for the
# entities used here. Keep model classes to be safe if minify is enabled later.
-keep class co.coffeery.app.data.model.** { *; }

# Room entities (data/local)
-keep class co.coffeery.app.data.local.** { *; }

# JSON parsing (org.json reflection)
-keep class org.json.** { *; }
-dontwarn org.json.**

# Kotlin data classes used for StateFlow
-keep class co.coffeery.app.ui.screens.root.AppUiState { *; }
-keep class co.coffeery.app.util.BrewResult { *; }

# BuildConfig — GOOGLE_SERVER_CLIENT_ID field accessed via reflection by some tools
-keep class co.coffeery.app.BuildConfig { *; }

# Compose — fine-grained (remove broad keep that disabled R8)
-keep class androidx.compose.runtime.** { *; }

# General
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# Google Sign-In
-keep class com.google.android.gms.auth.** { *; }
-dontwarn com.google.android.gms.auth.**

# Google Drive API — keep model and scopes for Gson reflection
-keep class com.google.api.services.drive.** { *; }
-keep class com.google.api.services.drive.model.** { *; }
-dontwarn com.google.api.services.drive.**
-keep class com.google.api.client.http.** { *; }
-dontwarn com.google.api.client.http.**
-keep class com.google.api.client.googleapis.extensions.android.gms.auth.** { *; }
-dontwarn com.google.api.client.googleapis.extensions.android.gms.auth.**
-keep class com.google.api.client.googleapis.extensions.android.http.** { *; }
-dontwarn com.google.api.client.googleapis.extensions.android.http.**
-keep class com.google.api.client.json.gson.** { *; }
-dontwarn com.google.api.client.json.gson.**
-keep class com.google.api.client.googleapis.json.** { *; }
-dontwarn com.google.api.client.googleapis.json.**
-keep class com.google.api.client.googleapis.media.** { *; }
-dontwarn com.google.api.client.googleapis.media.**
-keep class com.google.http.client.** { *; }
-dontwarn com.google.http.client.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn javax.naming.**
-dontwarn com.google.api.client.http.**
-dontwarn com.google.android.gms.common.**
