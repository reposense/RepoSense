const fs = require("fs");

var data = require("./summary.json");
var objs = {};

var attrs = "repo organization branch displayName fromDate".split(" ");
for(var dir in data){
    var obj = {};
    var tar = data[dir];

    if(!fs.existsSync(dir)){ fs.mkdirSync(dir); }
    
    for(var a in attrs){
        var attr = attrs[a];
        obj[attr] = tar[attr];
        delete tar[attr];
    }

    fs.writeFileSync(dir+"/summary.json", JSON.stringify(tar));
    objs[dir] = obj;
}

fs.writeFileSync("repo.js", JSON.stringify(objs));
