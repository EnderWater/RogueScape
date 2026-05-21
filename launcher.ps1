$ErrorActionPreference = "Stop"

Write-Host "Checking RuneLite version..."

# Read local gradle.properties
$gradlePropsPath = Join-Path $PSScriptRoot "gradle.properties"
$props = Get-Content $gradlePropsPath

$currentVersionLine = $props | Where-Object { $_ -match '^runeliteVersion=' }
$currentVersion = $currentVersionLine.Split('=')[1]

Write-Host "Current RuneLite Version: $currentVersion"

# Query Maven metadata
$metadataUrl = "https://repo.runelite.net/net/runelite/client/maven-metadata.xml"

[xml]$metadata = Invoke-WebRequest $metadataUrl | Select-Object -ExpandProperty Content

$latestVersion = $metadata.metadata.versioning.latest

Write-Host "Latest RuneLite Version: $latestVersion"

$shouldUpdate = $false

if ($latestVersion -ne $currentVersion)
{
    Write-Host ""
    $response = Read-Host "A newer RuneLite version is available. Update? (y/n)"

    if ($response -eq "y")
    {
        $shouldUpdate = $true
    }
}

if ($shouldUpdate)
{
    Write-Host "Updating RuneLite version..."

    $updatedProps = $props | ForEach-Object {
        if ($_ -match '^runeliteVersion=') {
            "runeliteVersion=$latestVersion"
        }
        else {
            $_
        }
    }

    Set-Content $gradlePropsPath $updatedProps

    Write-Host "Rebuilding ShadowJar..."

    cmd /c gradlew.bat shadowJar --refresh-dependencies

    if ($LASTEXITCODE -ne 0)
    {
        Write-Host "Build failed."
        pause
        exit 1
    }
}
else
{
    Write-Host "Using existing build."
}

Write-Host "Launching Roguescape..."

$props = Get-Content $gradlePropsPath
$jarNameLine = $props | Where-Object { $_ -match '^pluginJarName=' }
$jarName = $jarNameLine.Split('=')[1]

java -ea -jar "$jarName.jar"