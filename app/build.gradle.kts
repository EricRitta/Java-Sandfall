plugins {
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(libs.jaylib)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(26)
  }
}

application {
  // Define the main class for the application.
  mainClass = "Main"
}
