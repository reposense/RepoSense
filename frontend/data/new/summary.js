function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        fn(JSON.parse(xhr.responseText));
    };
    xhr.send(null);
}

function loadSubFile(dir){
    loadJSON(dir+"/summary.json", obj2 => {
        for(var key in obj2){
            summaryJson[dir][key] = obj2[key];
        }

        cnt -= 1;
        if(!cnt){ initialize(); }
    });
}

var cnt=0, summaryJson={};
loadJSON("repo.json", obj => {
    summaryJson = obj;
    for(var dir in obj){ cnt+=1; }
    for(var dir in obj){ loadSubFile(dir); }
});
