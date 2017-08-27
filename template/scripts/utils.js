var dynamicColors = function() {
    var r = Math.floor(Math.random() * 255);
    var g = Math.floor(Math.random() * 255);
    var b = Math.floor(Math.random() * 255);
    return "rgb(" + r + "," + g + "," + b + ")";
}
 
var zeros = function(num) {
    var result = [];
    for (var i = 0; i < num; i++) {
        result.push(0);
    }
    return result;
}

var normalizeResult = function(map, num) {
    for (key in map) {
        if (map[key].length < num) {
            for (var i = 0; i < num - map[key].length; i++) {
                map[key].push(0);
            }
        }
    }
}

var getQueryVariable = function (variable)
{
       var query = window.location.search.substring(1);
       var vars = query.split("&");
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return decodeURI(pair[1]);}
       }
       return null;
}