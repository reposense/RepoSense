var resultJson = {}, docTypes = [], cnt = 0;

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

function loadFiles() {
    var idx = 0;
    function next() {
        if (idx < docTypes.length) {
            loadJSON("authorship_" + docTypes[idx] + ".json", (res) => {
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

loadJSON("../doctype.json", (res) => {
   cnt = res.length;
   for (var idx in res) {
       if ({}.hasOwnProperty.call(res, idx)) {
           docTypes.push(res[idx]);
           cnt -= 1;
           if (!cnt) {
               loadFiles();
           }
       }
    }
});
