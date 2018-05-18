function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        if(xhr.status == 200){
            fn(JSON.parse(xhr.responseText));
        }else{
            alert("unable to get file");
        }
    };
    xhr.send(null);
}
