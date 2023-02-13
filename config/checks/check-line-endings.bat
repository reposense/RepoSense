@echo off

git grep --cached -I -n --no-color -P "\r$" -- "../../" | (
    for /f "tokens=1,2 delims=:" %%A in ('more') do @(
        echo ERROR:%%A:%%B: prohibited \r\n line ending, use \n instead.
    )
)
