> gradle jmods sdk -PCONF=Release -PMAVEN_PUBLISH=true -PMAVEN_VERSION=custom publishToMavenLocal

Changes that will not be merged:
- Remove java.desktop (Breaking change, saves us 13MB)

> Unfortunately, those are things that will unlikely ever be implemented in JavaFX.

Changes that MAY be merged:
- Remove Win DLLs, load them from the system (Is that needed? Should there be a flag to disable?)
  - Saves unpacked: 3.17 MB
  - Saves packed: 1.27MB
- Remove SWT jar from graphics (Why is it even there)
  - Saves unpacked: 0.36MB
  - Saves packed: 0.34MB
- Maven Publishing
- Maven Publishing Sources

> There are some underlying build changes that may make those obsolete or easier.

Changes that will be merged:
- John's Layout improvements
