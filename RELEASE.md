Build static libs:

```shell
bash gradlew sdk -PSTATIC_BUILD=true -PCONF=Release -PMAVEN_PUBLISH=true -PMAVEN_VERSION=custom-static publishToMavenLocal
```

Build into `~\.m2`

```shell
bash gradlew jmods sdk -PCONF=Release -PMAVEN_PUBLISH=true -PMAVEN_VERSION=custom publishToMavenLocal
```

Locations:

- JMods: `.\build\jmods`
- Static-Libs: `.\build\sdk\lib`
- Maven: `~\.m2\repository\org\openjfx`

Changes:

- Remove `java.desktop` (Breaking change, saves us 13MB)
    - Remove all `java.desktop` classes for GraalVM scanning
- Remove Win DLLs, load them from the system (Is that needed? Should there be a flag to disable?)
    - Saves unpacked: 3.17 MB
    - Saves packed: 1.27MB
- Remove `SWT` jar from graphics (Why is it even there)
    - Saves unpacked: 0.36MB
    - Saves packed: 0.34MB
- Simplified `NativeLibLoader`

Contributions:

- [OPENED] Maven Publishing Sources
- [MAYBE] Simplified NativeLibLoader
- [MAYBE] Flag to build without DLLs
- [MAYBE] Exclude SWT
- [NEVER] Removing java.desktop - will not be accepted

Other:

- Layout improvements by John are included

Research:

- All Windows DLLs can be loaded perfectly fine with System.loadLibrary(..)
- JavaFX DLLs:
    - Non JMods: Extracted to the TEMP cache and then loaded
    - JMods: Loaded from app/runtime/bin/javafx/*.dll
    - Static Build: DLL included / compiled in
