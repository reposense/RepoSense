function loadFile(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        console.log(xhr.responseText);
    }
    xhr.send(null);
}

var summaryJson = {};
loadFile("summary.json");
