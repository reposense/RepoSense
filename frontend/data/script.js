const fs = require("fs");

var data = require("./raw/summary.json");
var objs = {};

var attrs = "repo organization branch displayName fromDate".split(" ");
for(var dir in data){
    console.log(dir);

    var obj = {};
    var tar = data[dir];

    var ndir = "new/"+dir;

    if(!fs.existsSync(ndir)){ fs.mkdirSync(ndir); }

    for(var a in attrs){
        var attr = attrs[a];
        obj[attr] = tar[attr];
        delete tar[attr];
    }

    fs.writeFileSync(ndir+"/summary.json", JSON.stringify(tar));
    objs[dir] = obj;
}

fs.writeFileSync("new/repo.json", JSON.stringify(objs));
