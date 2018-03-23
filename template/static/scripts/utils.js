var getQueryVariable = function(variable, defaultValue) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return decodeURI(pair[1]);
        }
    }
    return defaultValue;
}

function copyTextToClipboard(text) {
    var textArea = document.createElement("textarea");
    textArea.style.position = 'fixed';
    textArea.style.top = 0;
    textArea.style.left = 0;
    textArea.style.width = '2em';
    textArea.style.height = '2em';
    textArea.style.padding = 0;
    textArea.style.border = 'none';
    textArea.style.outline = 'none';
    textArea.style.boxShadow = 'none';
    textArea.style.background = 'transparent';
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');
    document.body.removeChild(textArea);
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

function getScaleLimitMap() {
    var result = {};
    result["authorWeeklyIntervalContributions"] = getScaleLimit("authorWeeklyIntervalContributions");
    result["authorDailyIntervalContributions"] = getScaleLimit("authorDailyIntervalContributions");
    return result;
}

function getScaleLimit(intervalType) {
    var totalContribution = 0;
    var count = 0;
    for (repo in summaryJson) {
        for (author in summaryJson[repo][intervalType]) {
            for (i in summaryJson[repo][intervalType][author]) {
                currentPeriod = summaryJson[repo][intervalType][author][i];
                if (totalContribution['insertions'] != 0) {
                    totalContribution += currentPeriod['insertions'];
                    count += 1
                }

            }
        }
    }
    return totalContribution / count * 20;
};

function getIntervalCount(intervalType, minDate, maxDate) {
    var minDateParsed = Date.parse(minDate);
    var maxDateParsed = Date.parse(maxDate);
    var oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds

    var diffDays = Math.round(Math.abs((minDateParsed.getTime() - maxDateParsed.getTime()) / (oneDay)));
    if (intervalType == "authorWeeklyIntervalContributions") {
        var divisor = 7;
    } else {
        var divisor = 1;
    }
    return diffDays / divisor;
}

function getTotalContributionLimit() {
    var totalContribution = 0;
    var count = 0;
    for (repo in summaryJson) {
        for (author in summaryJson[repo]['authorFinalContributionMap']) {
            totalContribution += (summaryJson[repo]['authorFinalContributionMap'][author]);
            count += 1
        }
    }
    return totalContribution / count * 10;
};

function openInNewTab(url) {
    var win = window.open(url, '_blank');
    win.focus();
}

function flatten(authorRepos) {
    result = [];
    for (repo in authorRepos) {
        for (author in authorRepos[repo]) {
            result.push(authorRepos[repo][author]);
        }
    }
    return result;
}

function sortSegment(segment, sortElement, sortOrder) {
    if (sortOrder == "high2low") {
        segment.sort(function(a, b) {
            if (b[sortElement] > a[sortElement]){
                return 1;
            } else if (b[sortElement] < a[sortElement]){
                return -1
            } else{
                return 0;
            }
        })
    } else {
        segment.sort(function(a, b) {
            if (a[sortElement] > b[sortElement]){
                return 1;
            } else if (a[sortElement] < b[sortElement]){
                return -1
            } else{
                return 0;
            }
        })
    }
    return segment;
}

function sortByLineContributed(files, currentAuthor) {
    files.sort(function(lhs, rhs) {
        var lhsValue = lhs.authorContributionMap[currentAuthor] ? lhs.authorContributionMap[currentAuthor] : 0;
        var rhsValue = rhs.authorContributionMap[currentAuthor] ? rhs.authorContributionMap[currentAuthor] : 0;
        return rhsValue - lhsValue;
    })
    return files;
}

function isSearchMatch(searchTerm, authorRepo) {
    if (searchTerm == "") {
        return true;
    }
    var terms = searchTerm.split(" ");
    for (var i = 0; i < terms.length; i++) {
        //neither author name or repo name is a match for the search term
        if (isMatch(terms[i], authorRepo['author']) || isMatch(terms[i], authorRepo['displayName']) || isMatch(terms[i], authorRepo['authorDisplayName'])) {
            return true;
        }
    }
    return false;

}

function isMatch(searchTerm, currentPhrase) {
    return currentPhrase.toLowerCase().indexOf(searchTerm.toLowerCase()) != -1;
}

function getMinDate() {
    rawDate = summaryJson[Object.keys(summaryJson)[0]]["fromDate"];
    if (rawDate) {
        //the fromDate has been set
        return Date.parse(rawDate).toString("M/d/yy");
    } else {
        //find the min Date among all intervals
        var result;
        for (var i in summaryJson) {
            var authorContribution = summaryJson[i]["authorDailyIntervalContributions"];
            var currentRawDate = authorContribution[Object.keys(authorContribution)[0]][0]["fromDate"];
            var currentDate = Date.parse(currentRawDate);
            if (result) {
                if (result.compareTo(currentDate) > 0) {
                    result = currentDate;
                }
            } else {
                result = currentDate;
            }
        }
        return result.toString("M/d/yy");
    }
}

function getMaxDate() {
    rawDate = summaryJson[Object.keys(summaryJson)[0]]["toDate"];
    if (rawDate) {
        //the fromDate has been set
        return Date.parse(rawDate).toString("M/d/yy");
    } else {
        //find the min Date among all intervals
        var result;
        for (var i in summaryJson) {
            var authorContributions = summaryJson[i]["authorDailyIntervalContributions"];
            if (Object.keys(authorContributions).length == 0) continue;
            var authorIntervals = authorContributions[Object.keys(authorContributions)[0]];
            var currentRawDate = authorIntervals[authorIntervals.length - 1]["toDate"];
            var currentDate = Date.parse(currentRawDate);
            if (result) {
                if (result.compareTo(currentDate) < 0) {
                    result = currentDate;
                }
            } else {
                result = currentDate;
            }
        }
        return result.toString("M/d/yy");
    }
}

function isNotAuthored(currentAuthor,line){
    return currentAuthor==null || line.author == null || line.author.gitID != currentAuthor;
}