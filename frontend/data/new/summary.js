function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        fn(JSON.parse(xhr.responseText));
    };
    xhr.send(null);
}

var summaryJson = {};
loadJSON("repo.json", obj => {
    summaryJson = obj;
    for(var dir in obj){
        loadJSON(dir+"/summary.json", obj2 => {
            var g_dir = dir;
            for(var key in obj2){
                summaryJson[g_dir][key] = obj2[key];
                console.log(key);
                if(dir=="CS2103JAN2018-F09-B1_main"){
                    console.log(summaryJson[g_dir]);
                }
            }
        });
    } 
});
