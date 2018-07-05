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
    textArea.style.position = "fixed";
    textArea.style.top = 0;
    textArea.style.left = 0;
    textArea.style.width = "2em";
    textArea.style.height = "2em";
    textArea.style.padding = 0;
    textArea.style.border = "none";
    textArea.style.outline = 'none';
    textArea.style.boxShadow = "none";
    textArea.style.background = "transparent";
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand("copy");
    document.body.removeChild(textArea);
}

var getLegalClassName = function(original) {
    return original.replace(/ /g, "-");
}

var getContribution = function(repo) {
    var count = 0;
    for (var author in repo["authorFinalContributionMap"]) {
        count += repo["authorFinalContributionMap"][author];
    }
    return count;
}

function getScaleLimit(intervalType, docType, summaryJson) {
    var totalContribution = 0;
    var count = 0;
    for (var idx in docType) {
        if (!{}.hasOwnProperty.call(docType, idx)) {
            continue;
        }
        for (var repo in summaryJson[docType[idx]]) {
            if (!{}.hasOwnProperty.call(summaryJson[docType[idx]], repo)) {
                continue;
            }
            for (var author in summaryJson[docType[idx]][repo][intervalType]) {
                if (!{}.hasOwnProperty.call(summaryJson[docType[idx]][repo][intervalType], author)) {
                    continue;
                }
                for (var i in summaryJson[docType[idx]][repo][intervalType][author]) {
                    if (!{}.hasOwnProperty.call(summaryJson[docType[idx]][repo][intervalType][author], i)) {
                        continue;
                    }
                    var currentPeriod = summaryJson[docType[idx]][repo][intervalType][author][i];
                    if (totalContribution["insertions"] != 0) {
                        totalContribution += currentPeriod["insertions"];
                        count += 1;
                    }

                }
            }
        }
    }
    return totalContribution / count * 20;
};

function getScaleLimitMap(docType, summaryJson) {
    var result = {};
    result["authorWeeklyIntervalContributions"] = getScaleLimit("authorWeeklyIntervalContributions", docType, summaryJson);
    result["authorDailyIntervalContributions"] = getScaleLimit("authorDailyIntervalContributions", docType, summaryJson);
    return result;
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

function getTotalContributionLimit(docType, summaryJson) {
    var totalContribution = 0;
    var count = 0;
    for (var idx in docType) {
        if (!{}.hasOwnProperty.call(docType, idx)) {
            continue;
        }
        for (var repo in summaryJson[docType[idx]]) {
            for (var author in summaryJson[docType[idx]][repo]["authorFinalContributionMap"]) {
                if (!{}.hasOwnProperty.call(summaryJson[docType[idx]][repo]["authorFinalContributionMap"], author)) {
                    continue;
                }
                totalContribution += (summaryJson[docType[idx]][repo]["authorFinalContributionMap"][author]);
                count += 1
            }
        }
    }
    return totalContribution / count * 10;
};

function openInNewTab(url) {
    var win = window.open(url, '_blank');
    win.focus();
}

function flatten(authorRepos) {
    var result = [];
    for (var repo in authorRepos) {
        if (!{}.hasOwnProperty.call(authorRepos, repo)) {
            continue;
        }
        for (var author in authorRepos[repo]) {
            if (!{}.hasOwnProperty.call(authorRepos[repo], author)) {
                continue;
            }
            result.push(authorRepos[repo][author]);
        }
    }
    return result;
}

function sortSegment(segment, sortElement, sortOrder) {
    if (sortOrder === "high2low") {
        segment.sort(function(a, b) {
            if (b[sortElement] > a[sortElement]) {
                return 1;
            } else if (b[sortElement] < a[sortElement]) {
                return -1;
            } else {
                return 0;
            }
        });
    } else {
        segment.sort(function(a, b) {
            if (a[sortElement] > b[sortElement]) {
                return 1;
            } else if (a[sortElement] < b[sortElement]) {
                return -1;
            } else {
                return 0;
            }
        });
    }
    return segment;
}

function sortByLineContributed(files, currentAuthor) {
    files.sort(function(lhs, rhs) {
        var lhsValue = lhs.authorContributionMap[currentAuthor] ? lhs.authorContributionMap[currentAuthor] : 0;
        var rhsValue = rhs.authorContributionMap[currentAuthor] ? rhs.authorContributionMap[currentAuthor] : 0;
        return rhsValue - lhsValue;
    });
    return files;
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

function obtainRelevantFilesBasedOnDocTypes(resultJson, docTypes) {
    var result = [];
    for (var idx in docTypes) {
        if (!{}.hasOwnProperty.call(docTypes, idx)) {
            continue;
        }
        var files = resultJson[docTypes[idx]];
        for (var id in files) {
            if (!{}.hasOwnProperty.call(docTypes, idx)) {
                continue;
            }
            result.push(files[id]);
        }
    }
    return result;
}

function isMatch(searchTerm, currentPhrase) {
    return currentPhrase.toLowerCase().indexOf(searchTerm.toLowerCase()) != -1;
}

function isSearchMatch(searchTerm, authorRepo) {
    if (searchTerm === "") {
        return true;
    }
    var terms = searchTerm.split(" ");
    for (var i = 0; i < terms.length; i++) {
        //neither author name or repo name is a match for the search term
        if (isMatch(terms[i], authorRepo["author"]) || isMatch(terms[i], authorRepo["displayName"]) ||
            isMatch(terms[i], authorRepo["authorDisplayName"])) {
            return true;
        }
    }
    return false;

}

function getMinDate(docType) {
    docType = docType[0];
    var rawDate = summaryJson[docType][Object.keys(summaryJson[docType])[0]]["sinceDate"];
    if (rawDate) {
        //the sinceDate has been set
        return Date.parse(rawDate).toString("M/d/yy");
    } else {
        //find the min Date among all intervals
        var result;
        for (var i in summaryJson[docType]) {
            var authorContribution = summaryJson[docType][i]["authorDailyIntervalContributions"];
            var currentRawDate = authorContribution[Object.keys(authorContribution)[0]][0]["sinceDate"];
            var currentDate = Date.parse(currentRawDate);

            if (result) {
                if (result.compareTo(currentDate) > 0) {
                    result = currentDate;
                }
            } else {
                result = currentDate;
            }
        }
        if (result == null) {
            result = Date.today();
        }

        return result.toString("M/d/yy");
    }
}

function getMaxDate(docType) {
    docType = docType[0];
    var rawDate = summaryJson[docType][Object.keys(summaryJson[docType])[0]]["untilDate"];
    if (rawDate) {
        //the untilDate has been set
        return Date.parse(rawDate).toString("M/d/yy");
    } else {
        //find the max Date among all intervals
        var result;

        for (var i in summaryJson[docType]) {
            var authorContributions = summaryJson[docType][i]["authorDailyIntervalContributions"];
            if (Object.keys(authorContributions).length === 0) {
                continue;
            }

            var authorIntervals = authorContributions[Object.keys(authorContributions)[0]];
            if (authorIntervals.length === 0) {
                continue;
            }

            var currentRawDate = authorIntervals[authorIntervals.length - 1]["untilDate"];
            var currentDate = Date.parse(currentRawDate);

            if (result) {
                if (result.compareTo(currentDate) < 0) {
                    result = currentDate;
                }
            } else {
                result = currentDate;
            }
        }
        if (result == null) {
            result = Date.today();
        }
        return result.toString("M/d/yy");
    }
}

function isNotAuthored(currentAuthor, line) {
    return currentAuthor == null || line.author == null || line.author.gitID != currentAuthor;
}

function obtainSummariesForCombinedDocTypes(summary, docType) {
    if (docType.length === 0) { return; }
    var summariesForCombinedDocType = clone(summary[docType[0]]);
    if (docType.length === 1) { return summariesForCombinedDocType; }
    var dailyIntervalContributionSize = 0,
        totalDailyContribution = 0;
    for (var idx in docType) {
        var type = docType[idx];
        var summary_temp = clone(summary[type]);
        for (var organization in summary_temp) {
            for (var item in summary_temp[organization]) {
                if (item === "authorDailyIntervalContributions") {
                    var calculatedStatistics = obtainSummariesForAuthorDailyIntervalContributions(summariesForCombinedDocType, summary_temp,
                        organization, item, idx);
                    totalDailyContribution = calculatedStatistics[0];
                    dailyIntervalContributionSize = calculatedStatistics[1];
                }
                if (item === "authorWeeklyIntervalContributions") {
                    obtainSummariesForAuthorWeeklyIntervalContributions(summariesForCombinedDocType, summary_temp,
                        organization, item, idx);
                }
                if (item === "authorFinalContributionMap") {
                    obtainSummariesForAuthorFinalContributionMap(summariesForCombinedDocType, summary_temp,
                        organization, item, idx);
                }
            }
        }
    }
    obtainSummariesForAuthorContributionVariance(summariesForCombinedDocType, totalDailyContribution,
        dailyIntervalContributionSize)
    return summariesForCombinedDocType;
}

function obtainSummariesForAuthorDailyIntervalContributions(summariesForCombinedDocType, summary_temp, organization,
    item, idx) {
    var totalDailyContribution = 0, dailyIntervalContributionSize = 0;
    for (var person in summary_temp[organization][item]) {
        for (var commmit_idx = 0; commmit_idx < Math.min(summary_temp[organization][item][person].length,
            summariesForCombinedDocType[organization][item][person].length); commmit_idx++) {
            dailyIntervalContributionSize = dailyIntervalContributionSize + 1;
            totalDailyContribution = totalDailyContribution +
                parseInt(summary_temp[organization][item][person][commmit_idx]["insertions"]);
            if (idx != 0) {
                var insertionsCnt =
                    parseInt(summariesForCombinedDocType[organization][item][person][commmit_idx]["insertions"])
                    + parseInt(summary_temp[organization][item][person][commmit_idx]["insertions"]);
                var deletionsCnt =
                    parseInt(summariesForCombinedDocType[organization][item][person][commmit_idx]["deletions"])
                    + parseInt(summary_temp[organization][item][person][commmit_idx]["deletions"]);
                summariesForCombinedDocType[organization][item][person][commmit_idx]["insertions"] = insertionsCnt;
                summariesForCombinedDocType[organization][item][person][commmit_idx]["deletions"] = deletionsCnt;
            }
        }
    }
    return [totalDailyContribution, dailyIntervalContributionSize];
}

function obtainSummariesForAuthorWeeklyIntervalContributions(summariesForCombinedDocType, summary_temp, organization,
    item, idx) {
    if (idx != 0) {
        for (var person in summary_temp[organization][item]) {
            for (var commmit_idx = 0; commmit_idx < Math.min(summary_temp[organization][item][person].length,
                summariesForCombinedDocType[organization][item][person].length); commmit_idx++) {
                var insertionsCnt = parseInt(summariesForCombinedDocType[organization][item][person][commmit_idx]["insertions"])
                + parseInt(summary_temp[organization][item][person][commmit_idx]["insertions"]);
                var deletionsCnt = parseInt(summariesForCombinedDocType[organization][item][person][commmit_idx]["deletions"])
                + parseInt(summary_temp[organization][item][person][commmit_idx]["deletions"]);
                summariesForCombinedDocType[organization][item][person][commmit_idx]["insertions"] = insertionsCnt;
                summariesForCombinedDocType[organization][item][person][commmit_idx]["deletions"] = deletionsCnt;
            }
        }
    }
}

function obtainSummariesForAuthorFinalContributionMap(summariesForCombinedDocType, summary_temp, organization,
    item, idx) {
    if (idx != 0) {
        for (var person in summary_temp[organization][item]) {
            var total = parseInt(summariesForCombinedDocType[organization][item][person]) +
                parseInt(summary_temp[organization][item][person]);
            summariesForCombinedDocType[organization][item][person] = total;
        }
    }
}

function obtainSummariesForAuthorContributionVariance(summariesForCombinedDocType, totalDailyContribution,
    dailyIntervalContributionSize, organization) {
    var mean = totalDailyContribution / dailyIntervalContributionSize;
    for (var organization in summariesForCombinedDocType) {
        for (var person in summariesForCombinedDocType[organization]["authorDailyIntervalContributions"]) {
            var variance = 0;
            for (var commit in summariesForCombinedDocType[organization]["authorDailyIntervalContributions"][person]) {
                var insertions =
                    parseInt(summariesForCombinedDocType[organization]["authorDailyIntervalContributions"][person][commit]["insertions"]);
                variance = variance + Math.pow((mean - insertions), 2);
            }
            variance /= dailyIntervalContributionSize;
            summariesForCombinedDocType[organization]["authorContributionVariance"][person] = variance;
        }
    }
}