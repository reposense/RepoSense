#!/bin/sh
# Runs all check-*.sh scripts, and returns a non-zero exit code if any of them fail.

dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd) &&
ret=0 &&
for checkscript in "$dir"/check-*.sh; do
    chmod +x $checkscript
    if ! "$checkscript"; then
        ret=1
    fi
done
exit $ret
