$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

$buildDir = Join-Path $root 'build'
$classDir = Join-Path $buildDir 'classes'
$jarPath = Join-Path $root 'Battleship.jar'

if (Test-Path $buildDir) {
    Remove-Item $buildDir -Recurse -Force
}
New-Item -ItemType Directory -Path $classDir | Out-Null

javac -d $classDir *.java

Copy-Item -Recurse -Force (Join-Path $root 'fonts') (Join-Path $classDir 'fonts')
Get-ChildItem -Path $root -Filter '*.txt' | Copy-Item -Destination $classDir -Force

if (Test-Path $jarPath) {
    Remove-Item $jarPath -Force
}

jar cfe $jarPath GameMain -C $classDir .
Write-Host "Created $jarPath"
