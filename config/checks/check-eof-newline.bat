@echo
rem Checks that all text files end with a newline.

set temp=check-eof-newline~%RANDOM%.tmp

git grep --cached -I -l -e "*" -- "../../" > %temp%

setlocal enabledelayedexpansion
set ret=0


rem Count the number of lines that contain zero or more characters at the end;
rem this is true for every line except for the last when it is not terminated
rem by a line-break, because the `$` is anchored to such:

for /f "delims=" %%F in (%temp%) do @(
       set file=%%F
       set "file=!file:/=\!"
       for /F %%D in ('findstr ".*$" "!file!" ^| find /C /V ""') do (
           rem Count the total number of lines in the file and compare
           for /F %%C in ('^< "!file!" find /C /V ""') do (
               (
                   if %%D lss %%C (
                    echo ERROR:!file!:%%C: no newline at EOF.
                    set ret=1
                   )
               )
           )
       )
)

exit /b !ret!

