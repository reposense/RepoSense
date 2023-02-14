@echo off
rem Runs all check-*.bat scripts, and returns a non-zero exit code if any of them fail.

setlocal enabledelayedexpansion
set ret=0

for /R %%X in (check-*.bat) do (
 call %%X
 if ERRORLEVEL 1 set ret=1
)

exit /b !ret!
