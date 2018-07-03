function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            fn(JSON.parse(xhr.responseText));
        }
    }
    xhr.open("GET", file);
    xhr.send(null);
}

function loadSubFile(dir, docType){
    loadJSON(dir+"/commits_"+docType+".json", obj2 => {
        for(var key in obj2){
//            console.log("loading from "+docType);
//            console.log("before "+dir+"/"+key);
//            console.log(summaryJson["*.adoc"][dir][key]);
            summaryJson[docType][dir][key] = clone(obj2[key]);
//            console.log("after "+dir+"/"+key);
//            console.log(summaryJson["*.adoc"][dir][key]);
//            console.log(summaryJson["*.adoc"][dir][key] === summaryJson["*.java"][dir][key]);
        }
        cnt -= 1;
        if(!cnt){ initialize();}
    });
}

function clone(obj) {
      if (obj === null || typeof(obj) !== 'object' || 'isActiveClone' in obj)
        return obj;

      if (obj instanceof Date)
        var temp = new obj.constructor(); //or new Date(obj);
      else
        var temp = obj.constructor();

      for (var key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          obj['isActiveClone'] = null;
          temp[key] = clone(obj[key]);
          delete obj['isActiveClone'];
        }
      }

      return temp;
    }

var cnt=0, summaryJson={}, docTypes = ["adoc", "java"];
var tempJson={};

loadJSON("summary.json", res => {
    summaryJson = {};
    cnt = res.length * docTypes.length;

    for (var idx in docTypes) {
        summaryJson[docTypes[idx]] = {};
        for(var i in res){
            var repo = res[i];
            var name = repo.organization+"_"+repo.repoName;
            summaryJson[docTypes[idx]][name] = clone(repo);
        }
        for(var dir in summaryJson[docTypes[idx]]){
            loadSubFile(dir, docTypes[idx]);
        }
    }
});
