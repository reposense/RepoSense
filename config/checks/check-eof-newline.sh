#!/bin/sh
# Checks that all text files end with a newline.

ret=0

# Preserve filename with spaces by only splitting on newlines.
IFS='
'

for filename in $(git grep --cached -I -l -e '' -- ':/'); do
    if [ "$(tail -c 1 "./$filename")" != '' ]; then
        line="$(wc -l "./$filename" | cut -d' ' -f1)"
        echo "ERROR:$filename:$line: no newline at EOF."
        ret=1
    fi
done

exit $ret
