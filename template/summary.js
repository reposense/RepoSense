function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        fn(JSON.parse(xhr.responseText));
    };
    xhr.send(null);
}

function loadSubFile(dir){
    loadJSON(dir+"/commits.json", obj2 => {
        for(var key in obj2){
            summaryJson[dir][key] = obj2[key];
        }

        cnt -= 1;
        if(!cnt){ initialize(); }
    });
}

var cnt=0, summaryJson={};
loadJSON("summary.json", res => {
    summaryJson = {};
    for(var i in res){
        var repo = res[i];
        var name = repo.organization+"_"+repo.repoName;
        summaryJson[name] = repo;
    }

    cnt = res.length;
    for(var dir in summaryJson){ loadSubFile(dir); }
});
