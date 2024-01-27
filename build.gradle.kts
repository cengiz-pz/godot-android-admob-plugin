// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.library") version "7.2.2" apply false
}

val cleanBuildDir by tasks.registering(Delete::class) {
    delete(rootProject.buildDir)
}

allprojects {
    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
}