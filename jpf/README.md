# JPF verification

JPF Core currently requires JDK 11. Build `jpf-core` with `gradlew buildJars`,
then run from the project root:

```powershell
./jpf/run.ps1 -JpfHome C:/path/to/jpf-core -JavaHome C:/path/to/jdk-11
```
