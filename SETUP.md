# Android Studio Setup Instructions

## Java Version Requirements

This project now uses Java 8 compatibility settings to ensure maximum compatibility across different development environments.

### Steps to configure Java version in Android Studio:

1. Open Android Studio
2. Go to File > Project Structure
3. Under SDK Location, set the JDK location to a Java 8 or Java 11 installation
4. Click Apply and OK

## Gradle Configuration

If you encounter JVM target compatibility issues, try the following:

1. In Android Studio, go to File > Settings > Build, Execution, Deployment > Build Tools > Gradle
2. Make sure Gradle JDK is set to Java 8 or Java 11
3. Click Apply and OK

## Important Note About Android Gradle Plugin

This project uses Android Gradle Plugin 7.4.2 and Gradle 7.5, which are compatible with Java 8 and Java 11. 
Do not upgrade to Android Gradle Plugin 8.x as it requires Java 17.

## Building the Project

After configuring your Java environment, you should be able to build the project without any JVM target compatibility issues.



## Troubleshooting

If you still encounter issues with JVM target compatibility, try:

1. Invalidate caches and restart Android Studio (File > Invalidate Caches / Restart)
2. Delete the .gradle folder in the project directory and rebuild
3. Ensure your JAVA_HOME environment variable points to a Java 8 or Java 11 installation

### Specific Error: No enum constant org.jetbrains.kotlin.gradle.plugin.PropertiesProvider.JvmTargetValidationMode

If you encounter this error, it means the Kotlin Gradle plugin doesn't recognize the validation mode. We've updated the project to remove this setting completely and set JVM targets directly. If you still see this error, try:

1. Make sure you've pulled the latest changes from the repository
2. Check that the `kotlin.jvm.target.validation.mode` line in gradle.properties is commented out or removed
3. Verify that app/build.gradle has `jvmTarget = '1.8'` set directly in kotlinOptions
4. Restart Android Studio after making these changes

Note: If you're using a Turkish keyboard, be careful with the "i" character in configuration files, as Turkish "İ" is different from English "I" and can cause parsing errors.
