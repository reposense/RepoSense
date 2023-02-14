@echo off

set temp=check-trailing-whitespace~%RANDOM%.tmp

git grep --cached -I -n --no-color -P "[ \t]+$" -- ":/" ":/!*/require_trailing_whitespaces/*" > %temp%

setlocal enabledelayedexpansion
set ret=0

for /f "tokens=1,2 delims=:" %%A in (%temp%) do @(
    set severity=ERROR
    echo.%%A | findstr /c:".md" > nul && (
        set severity=WARN
    ) || (
        set ret=1
    )
    echo !severity!:%%A:%%B: trailing whitespace.
)

del %temp%
exit /b !ret!

