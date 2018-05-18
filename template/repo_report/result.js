function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        fn(JSON.parse(xhr.responseText));
    };
    xhr.send(null);
}

var resultJson={};
loadJSON("authorship.json", obj => {
    resultJson = obj;
    initialize();
});
