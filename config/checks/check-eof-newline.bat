@echo
setlocal enabledelayedexpansion

set ret=0

git grep --cached -I -l -e "*" -- "../../" > temp.txt

for /f "delims=" %%F in (temp.txt) do @(
       rem /* Count the number of lines that contain zero or more characters at the end;
       rem    this is true for every line except for the last when it is not terminated
       rem    by a line-break, because the `$` is anchored to such: */

       set file=%%F
       set "file=!file:/=\!"

       for /F %%D in ('findstr ".*$" "!file!" ^| find /C /V ""') do (
           rem // Count the total number of lines in the file:
           for /F %%C in ('^< "!file!" find /C /V ""') do (
               (
                   rem // Compare the line counts and conditionally append a line-break:
                   if %%D lss %%C (
                    echo ERROR:!file!:%%C: no newline at EOF.
                    set ret=1
                   )
               )
           )
       )
)

exit /b !ret!

