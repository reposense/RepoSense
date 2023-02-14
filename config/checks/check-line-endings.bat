@echo off
setlocal enabledelayedexpansion

set ret=0

git grep --cached -I -n --no-color -P "\r$" -- "../../" | (
    for /f "tokens=1,2 delims=:" %%A in ('more') do @(
        echo ERROR:%%A:%%B: prohibited \r\n line ending, use \n instead.
        set ret=1
    )
)

exit /b !ret!
