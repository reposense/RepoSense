for f in $(ls --color=no | grep CS); do 
    cp template/repo_report/index.html $f/index.html
    cp template/repo_report/result.js $f/result.js 
done

