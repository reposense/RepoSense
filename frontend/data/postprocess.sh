# cleanup
rm -rf raw/static
rm -rf raw/repo_report
for f in $(find raw | grep json); do rm $f; done

# convert to JSON
for f in $(find raw | grep js); do
    echo cleaning up $f
    sed "s/var summaryJson = //; s/var resultJson = //" $f > $f"on"
done

# splitting the summary
node script.js

# copying the gitblame over
for f in $(find raw/CS*/*json); do
    cp $f new/${f:4}
done
