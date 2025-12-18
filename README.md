# Amaal - Android CI/CD Pipeline

This project includes a GitHub Actions workflow to automatically build a debug APK on every push or pull request to the `main` or `master` branches.

## How to use

1. **Push to GitHub**: Ensure the `.github/workflows/android_build.yml` and Gradle configuration files are in your repository.
2. **Actions Tab**: Navigate to the "Actions" tab in your GitHub repository to see the build progress.
3. **Download APK**: Once the workflow completes, the `Amaal-Debug-APK` will be available as an artifact in the build summary.

## Requirements
- Java 17
- Gradle Wrapper (`gradlew`)
- Standard Android project structure