@echo off

set temp=check-line-endings~%RANDOM%.tmp

git grep --cached -I -n --no-color -P "\r$" -- ":/" > %temp%

setlocal enabledelayedexpansion
set ret=0

for /f "tokens=1,2 delims=:" %%A in (%temp%) do @(
    echo ERROR:%%A:%%B: prohibited \r\n line ending, use \n instead.
    set ret=1
)

del %temp%
exit /b !ret!
