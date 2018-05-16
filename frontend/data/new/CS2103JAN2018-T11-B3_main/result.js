function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        fn(JSON.parse(xhr.responseText));
    };
    xhr.send(null);
}

var cnt=0, resultJson={};
loadJSON("result.json", obj => {
    resultJson = obj;
    initialize();
});
