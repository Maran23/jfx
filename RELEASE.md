> .\gradlew clean jmods sdk -PSTATIC_BUILD=true -PCONF=Release -PMAVEN_PUBLISH=true -PMAVEN_VERSION=custom-static publishToMavenLocal

> .\gradlew clean jmods sdk -PCONF=Release -PMAVEN_PUBLISH=true -PMAVEN_VERSION=custom publishToMavenLocal

Locations:
- JMods: `..\build\jmods`
- Static-Libs: `..\sdk\lib`

Changes:
- Remove `java.desktop` (Breaking change, saves us 13MB)
  - Remove all `java.desktop` classes for GraalVM scanning
- Remove Win DLLs, load them from the system (Is that needed? Should there be a flag to disable?)
  - Saves unpacked: 3.17 MB
  - Saves packed: 1.27MB
- Remove SWT jar from graphics (Why is it even there)
  - Saves unpacked: 0.36MB
  - Saves packed: 0.34MB
- Simplified `NativeLibLoader`
- Bump everything to `JNI_VERSION_1_8`

Contributions:
- [OPENED] Maven Publishing
- [OPENED] Maven Publishing Sources
- [MAYBE] Bump JNI_VERSION to Java 8
- [MAYBE] Simplified NativeLibLoader
- [MAYBE] Flag to build without DLLs
- [MAYBE] Exclude SWT
- [NEVER] Removing java.desktop - will not be accepted

Other:
- Layout improvements by John are included

> All Windows DLLs can be loaded perfectly fine with System.loadLibrary(..)
> All JavaFX DLLs need to be extracted to the TEMP cache for non JMods, 
> or otherwise are loaded from app/runtime/bin/javafx/*.dll
