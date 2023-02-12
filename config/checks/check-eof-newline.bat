@echo off
setlocal enabledelayedexpansion

set "expected="

git grep --cached -I -l -e "*" -- "../../" | (
    set lastline="test"
    for /f "delims=" %%F in ('more') do @(
       for /f "delims==" %%A in (%%F) do @(
        set lastline=%%A
        echo # %%A
       )
    )

    if not "%expected%" == "%lastline%" ( echo "ERROR:%%F:line: no newline at EOF." )

)
