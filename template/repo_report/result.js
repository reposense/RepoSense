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
            summaryJson[docType][dir][key] = clone(obj2[key]);
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

function loadFiles() {
    var idx = 0;
    function next() {
        if (idx < docTypes.length) {
            loadJSON("authorship_" + docTypes[idx] + ".json", res => {
                resultJson[docTypes[idx]] = clone(res);
                idx += 1;
                next();
            });
        } else {
            initialize();
        }
    }
    next();
}

var docTypes = ["java", "adoc"], cnt = docTypes.length, resultJson = {};

loadFiles();
