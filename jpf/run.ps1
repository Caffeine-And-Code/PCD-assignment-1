param(
    [string]$JpfHome = $env:JPF_HOME,
    [string]$JavaHome = $env:JAVA_HOME
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($JpfHome)) {
    throw "Set JPF_HOME or pass -JpfHome with the jpf-core directory."
}
if ([string]::IsNullOrWhiteSpace($JavaHome)) {
    throw "Set JAVA_HOME or pass -JavaHome with a JDK 11 directory."
}

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$runJpf = Join-Path $JpfHome "build/RunJPF.jar"
$java = Join-Path $JavaHome "bin/java.exe"
$javac = Join-Path $JavaHome "bin/javac.exe"
$classes = Join-Path $projectRoot "out/jpf"

if (-not (Test-Path -LiteralPath $runJpf)) {
    throw "RunJPF.jar not found. Build jpf-core first with gradlew buildJars."
}
if (-not (Test-Path -LiteralPath $java) -or -not (Test-Path -LiteralPath $javac)) {
    throw "A complete JDK 11 was not found in JavaHome."
}

if (Test-Path -LiteralPath $classes) {
    Remove-Item -LiteralPath $classes -Recurse -Force
}
New-Item -ItemType Directory -Path $classes | Out-Null
$sources = @(
    (Join-Path $projectRoot "src/pcd/poool/vnativethreads/util/BoundedBuffer.java"),
    (Join-Path $projectRoot "src/pcd/poool/vnativethreads/util/BoundedBufferImpl.java"),
    (Join-Path $projectRoot "src/pcd/poool/vjavaexecutorframework/util/BoundedBuffer.java"),
    (Join-Path $projectRoot "src/pcd/poool/vjavaexecutorframework/util/BoundedBufferImpl.java"),
    (Join-Path $projectRoot "src/pcd/test/jpf/NativeBufferJpfTest.java"),
    (Join-Path $projectRoot "src/pcd/test/jpf/ExecutorBufferJpfTest.java")
)

& $javac -d $classes $sources
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$configurations = @("native-buffer.jpf", "executor-buffer.jpf")
foreach ($configuration in $configurations) {
    Write-Host "`nRunning $configuration"
    & $java -Xmx1024m -jar $runJpf "+classpath=$classes" `
        "+sourcepath=$(Join-Path $projectRoot 'src')" (Join-Path $PSScriptRoot $configuration)
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
exit 0
