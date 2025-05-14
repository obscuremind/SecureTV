# Android Studio Setup Instructions

## Java Version Requirements

This project requires Java 11 to build properly. Please ensure your Android Studio is configured to use Java 11 for this project.

### Steps to configure Java version in Android Studio:

1. Open Android Studio
2. Go to File > Project Structure
3. Under SDK Location, set the JDK location to a Java 11 installation
4. Click Apply and OK

## Gradle Configuration

If you encounter JVM target compatibility issues, try the following:

1. In Android Studio, go to File > Settings > Build, Execution, Deployment > Build Tools > Gradle
2. Make sure Gradle JDK is set to Java 11
3. Click Apply and OK

## Important Note About Android Gradle Plugin

This project uses Android Gradle Plugin 7.4.2 and Gradle 7.5, which are compatible with Java 11. 
Do not upgrade to Android Gradle Plugin 8.x as it requires Java 17.

## Building the Project

After configuring Java 11, you should be able to build the project without any JVM target compatibility issues.



## Troubleshooting

If you still encounter issues with JVM target compatibility, try:

1. Invalidate caches and restart Android Studio (File > Invalidate Caches / Restart)
2. Delete the .gradle folder in the project directory and rebuild
3. Ensure your JAVA_HOME environment variable points to a Java 11 installation
