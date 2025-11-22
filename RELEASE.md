> gradle jmods sdk -PCONF=Release -PMAVEN_PUBLISH=true -PMAVEN_VERSION=custom publishToMavenLocal

Changes that will not be merged:
- Remove Win DLLs, load them from the system (Would be nice if this can be enabled at dependency level)
- Remove SWT jar from graphics (Why is it even there, Saves 0.32MB)
- Remove java.desktop (Breaking change, saves us 13MB)

> Unfortunately, those are things that will unlikely ever be implemented in JavaFX.

Changes that will be merged:
- John's Layout improvements
- Maven Publishing
- Maven Publishing Sources

More:
- Disable SWT App
