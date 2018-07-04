function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
            fn(JSON.parse(xhr.responseText));
        }
    };
    xhr.open("GET", file);
    xhr.send(null);
}

function loadSubFile(dir, docType){
    loadJSON(dir+"/commits_"+docType+".json", obj2 => {
        for(var key in obj2){
            summaryJson[docType][dir][key] = clone(obj2[key]);
        }
        cnt -= 1;
        if(!cnt){ initialize();}
    });
}

function clone(obj) {
    if (obj === null || typeof(obj) !== "object" || "isActiveClone" in obj) {
        return obj;
    }
    var temp;
    if (obj instanceof Date) {
        temp = new obj.constructor(); //or new Date(obj);
    } else {
        temp = obj.constructor();
    }
    for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
            obj["isActiveClone"] = null;
            temp[key] = clone(obj[key]);
            delete obj["isActiveClone"];
        }
    }
    return temp;
}

var cnt=0, summaryJson={}, docTypesArr = [];
var tempJson={};
var cntDocType = 0;

loadJSON("doctype.json", res => {
    cntDocType = res.length;
    for(var idx in res) {
        docTypesArr.push(res[idx]);
        cntDocType -= 1;
        if (!cntDocType) {
            loadJSON("summary.json", res => {
                summaryJson = {};
                cnt = res.length * docTypesArr.length;

                for (var idx in docTypesArr) {
                    summaryJson[docTypesArr[idx]] = {};
                    for(var i in res){
                        var repo = res[i];
                        var name = repo.organization+"_"+repo.repoName;
                        summaryJson[docTypesArr[idx]][name] = clone(repo);
                    }
                    for(var dir in summaryJson[docTypesArr[idx]]){
                        loadSubFile(dir, docTypesArr[idx]);
                    }
                }
            });
        }
    }
});
