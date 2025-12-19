# CRITICAL: Keep all attributes for proper serialization
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable

# ========================================
# CRITICAL: Keep ConfigItem (fixes the TypeToken crash)
# ========================================
-keep class com.badr1.ardraw.data.ConfigItem { *; }
-keepclassmembers class com.badr1.ardraw.data.ConfigItem {
    <fields>;
    <init>(...);
    <methods>;
}

# Keep SerializedName annotations on ConfigItem
-keepclassmembers class com.badr1.ardraw.data.ConfigItem {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ========================================
# CRITICAL: Keep InitRepository and TypeToken classes
# ========================================
-keep class com.badr1.ardraw.data.repository.InitRepository { *; }
-keep class com.badr1.ardraw.data.repository.InitRepository$** { *; }
-keepclassmembers class com.badr1.ardraw.data.repository.InitRepository {
    *;
}
-keepclassmembers class com.badr1.ardraw.data.repository.InitRepository$** {
    *;
}

# ========================================
# Keep ALL your app classes
# ========================================
-keep class com.badr1.ardraw.** { *; }
-keepclassmembers class com.badr1.ardraw.** { *; }

# Keep ALL repositories
-keep class com.badr1.ardraw.data.repository.** { *; }
-keepclassmembers class com.badr1.ardraw.data.repository.** { *; }

# Keep all data models
-keep class com.badr1.ardraw.data.Category { *; }
-keep class com.badr1.ardraw.data.Subcategory { *; }
-keep class com.badr1.ardraw.data.ImageMetadata { *; }

# Keep data classes with ALL their properties
-keepclassmembers class com.badr1.ardraw.data.** {
    <fields>;
    <init>(...);
    <methods>;
}

# Keep data class copy methods and component functions
-keepclassmembers class com.badr1.ardraw.data.* {
    public ** copy(...);
    public ** component*();
}

# ========================================
# Gson (CRITICAL - fixes TypeToken crash)
# ========================================
-keep class com.google.gson.** { *; }
-keepclassmembers class com.google.gson.** { *; }

# Keep TypeToken and its generic type information
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# CRITICAL: Keep all anonymous TypeToken classes
-keepclassmembers class * extends com.google.gson.reflect.TypeToken {
    *;
}

# Keep generic signatures
-keepattributes Signature

# Keep SerializedName annotations
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class * implements com.google.gson.TypeAdapter { *; }
-keep class * implements com.google.gson.TypeAdapterFactory { *; }
-keep class * implements com.google.gson.JsonSerializer { *; }
-keep class * implements com.google.gson.JsonDeserializer { *; }

# ========================================
# Room Database
# ========================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }

-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** getDatabase(...);
    public abstract ** *Dao();
}

# Keep all Room-generated classes
-keep class * implements androidx.room.EntityInsertionAdapter { *; }
-keep class * implements androidx.room.EntityDeletionOrUpdateAdapter { *; }
-keep class * implements androidx.room.SharedSQLiteStatement { *; }

-dontwarn androidx.room.**

# ========================================
# Kotlin & Coroutines
# ========================================
-keepclassmembers class **.*$WhenMappings {
    <fields>;
}

-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep suspend functions
-keepclassmembers class * {
    *** *Async(...);
    *** *Suspend(...);
}

# ========================================
# ViewModels & Lifecycle
# ========================================
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
    <init>(...);
}

-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
    <init>(...);
}

-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ========================================
# Hilt/Dagger
# ========================================
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

-keepclassmembers,allowobfuscation class * {
    @dagger.* *;
    @javax.inject.* *;
    @dagger.hilt.* *;
}

-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

-keep class * extends dagger.internal.InjectedFieldSignature { *; }

# Keep Hilt generated classes
-keep class **_HiltModules { *; }
-keep class **_HiltModules$** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-keep class **_ComponentTreeDeps { *; }

-dontwarn com.google.errorprone.annotations.**

# ========================================
# Firebase
# ========================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keepclassmembers class com.google.firebase.** { *; }

-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ========================================
# Jetpack Compose
# ========================================
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
-dontwarn androidx.compose.**

-keep class **$Companion { *; }

# ========================================
# DataStore
# ========================================
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends androidx.datastore.core.Serializer {
    *;
}
-dontwarn androidx.datastore.**

# ========================================
# CameraX
# ========================================
-keep class androidx.camera.** { *; }
-keepclassmembers class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ========================================
# Coil Image Loading
# ========================================
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ========================================
# Navigation Component
# ========================================
-keep class androidx.navigation.** { *; }
-keepclassmembers class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# ========================================
# Parcelable
# ========================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
    public <fields>;
    public <methods>;
}

# ========================================
# Enums
# ========================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    **[] $VALUES;
    public *;
}

# ========================================
# Native Methods
# ========================================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ========================================
# Keep constructors for reflection
# ========================================
-keepclassmembers class * {
    public <init>(...);
}

# ========================================
# Debugging
# ========================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile