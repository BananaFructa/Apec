plugins {
    kotlin("jvm") version "1.9.10" apply false
    id("org.polyfrost.multi-version.root")
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

preprocess {
    "1.12.2-forge"(11202, "srg") {
        "1.8.9-forge"(10809, "srg")
    }
}