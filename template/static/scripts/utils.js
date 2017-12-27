var getQueryVariable = function(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return decodeURI(pair[1]);
        }
    }
    return null;

}
var getLegalClassName = function(original) {
    return original.replace(/ /g, "-");
}

var getContribution = function(repo) {
    var count = 0;
    for (author in repo['authorFinalContributionMap']) {
        count += repo['authorFinalContributionMap'][author];
    }
    return count;
}

function openInNewTab(url) {
    var win = window.open(url, '_blank');
    win.focus();
}

function flatten(authorRepos) {
    result = [];
    for (repo in authorRepos){
        for (author in authorRepos[repo]){
            result.push(authorRepos[repo][author]);
        }
    }
    return result;
}

function sortSegment(segment, sortElement, sortOrder) {
    
    if (sortOrder == "high2low") {
        segment.sort(function(a, b) {
            return b[sortElement] - a[sortElement];
        })
    } else {
        segment.sort(function(a, b) {
            return a[sortElement] - b[sortElement];
        })
    }
    return segment;
}