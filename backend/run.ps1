# run.ps1 - Decoupled Self-contained Maven Downloader and Spring Boot Server Launcher
# This script makes running the project extremely easy if Maven is not installed globally.

$mavenVersion = "3.9.6"
$mavenDirName = "apache-maven-$mavenVersion"
$zipName = "apache-maven-$mavenVersion-bin.zip"
$downloadUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/$zipName"
$localMavenDir = Join-Path $PSScriptRoot ".maven"
$zipPath = Join-Path $PSScriptRoot $zipName
$extractedBinPath = Join-Path $localMavenDir "$mavenDirName\bin"

Write-Host "==========================================================" -ForegroundColor Green
Write-Host "   PATTA TRANSFER PORTAL - SPRING BOOT LAUNCHER AUTOMATION" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green

# 1. Inspect and install Maven sandbox if missing
if (-not (Test-Path $extractedBinPath)) {
    Write-Host "`n>>> Global Maven ('mvn') not detected. Downloading portable Apache Maven $mavenVersion..." -ForegroundColor Yellow
    
    if (-not (Test-Path $localMavenDir)) {
        New-Item -ItemType Directory -Path $localMavenDir -Force | Out-Null
    }
    
    Write-Host "Downloading: $downloadUrl" -ForegroundColor Gray
    try {
        # Configure TLS for modern downloads
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $downloadUrl -OutFile $zipPath
        Write-Host "Download complete!" -ForegroundColor Green
    } catch {
        Write-Host "ERROR: Failed to download Maven. Ensure you are connected to the internet: $_" -ForegroundColor Red
        Exit
    }
    
    Write-Host ">>> Extracting sandbox binaries..." -ForegroundColor Yellow
    try {
        Expand-Archive -Path $zipPath -DestinationPath $localMavenDir -Force
        Write-Host "Extraction complete!" -ForegroundColor Green
    } catch {
        Write-Host "ERROR: Extraction failed: $_" -ForegroundColor Red
        Exit
    } finally {
        if (Test-Path $zipPath) {
            Remove-Item -Path $zipPath -Force
        }
    }
    
    Write-Host ">>> Portable Maven installed in project space successfully!" -ForegroundColor Green
} else {
    Write-Host "`n>>> Portable Maven installation found." -ForegroundColor Green
}

# 2. Bind Maven to script process session
$env:PATH = "$extractedBinPath;" + $env:PATH
Write-Host ">>> Process session PATH updated." -ForegroundColor Cyan

# 3. Start Spring Boot app
Write-Host "`n>>> Commencing Maven compile and boot initialization..." -ForegroundColor Green
Write-Host ">>> Note: Hibernate JPA will auto-generate users/applications tables in MySQL." -ForegroundColor Gray
Write-Host "==========================================================" -ForegroundColor Green

# Run Maven Boot execution
mvn spring-boot:run
