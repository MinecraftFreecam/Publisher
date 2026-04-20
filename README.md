<p align="center" width="100%"><img src=".idea/icon.svg" width="128" height="128" alt="Freecam Publisher icon"></p>

[![GitHub Release](https://img.shields.io/github/v/release/MinecraftFreecam/Publisher)](https://github.com/MinecraftFreecam/Publisher/releases/latest)
[![](https://jitpack.io/v/MinecraftFreecam/Publisher.svg)][JitPack]

# Freecam Publisher

An internal tool for publishing [Freecam] releases.

Uses [CurseUpload4J] to upload releases to CurseForge and [Modrinth4J] to upload releases to Modrinth.

### Schema

DTOs representing the release-metadata schema can be found in [`schema`](./schema).
You can depend on them using JitPack:

```kotlin
repositories {
    maven("https://jitpack.io")
}
dependencies {
    implementation("com.github.MinecraftFreecam.Publisher:schema:-SNAPSHOT")
}
```

You can pin a specific version by replacing `-SNAPSHOT` with a git tag or commit, see [JitPack].

[Freecam]: https://github.com/MinecraftFreecam/Freecam
[CurseUpload4J]: https://github.com/firstdarkdev/CurseUpload4J
[Modrinth4J]: https://github.com/masecla22/Modrinth4J
[JitPack]: https://jitpack.io/#MinecraftFreecam/Publisher
