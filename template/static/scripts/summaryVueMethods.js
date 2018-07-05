vueMethods = {
    updateMinDate: function(date) {
        this.minDate = date;
    },
    updateMaxDate: function(date) {
        this.maxDate = date;
    },
    rangeFilter: function(contributions, intervalType, minDate, maxDate) {
        var resultContribution = [];
        var minDateParsed = Date.parse(minDate);
        var maxDateParsed = Date.parse(maxDate);
        var startingDate = Date.parse(contributions[0]["sinceDate"]);
        var paddingCount = getIntervalCount(intervalType, minDate, startingDate);
        if(minDateParsed < startingDate){
            for(var i=0; i<paddingCount; i++){
                resultContribution.push({
                    insertions:0
                });
            }
        }
        for (contribution of contributions) {
            var currentSinceDate = Date.parse(contribution["sinceDate"]);
            var currentUntilDate = Date.parse(contribution["untilDate"]);
            if (minDateParsed.compareTo(currentSinceDate) <= 0 && maxDateParsed.compareTo(currentUntilDate) >= 0) {
                resultContribution.push(contribution);
            }
        }

        return resultContribution;
    },
    getSliceStyle(index, value, intervalType, minDate, maxDate, sliceScaleLimitMap) {
        var sliceScaleLimit = sliceScaleLimitMap[intervalType];
        var spacing = 100 / getIntervalCount(intervalType, minDate, maxDate);
        var contribution = value['insertions'];
        var width;
        if (contribution == 0) {
            width = 0;
        }  else {
            width = contribution / sliceScaleLimit * spacing * 1.5;
            if (width < 0.5) {
                width = 0.5;
            }
        }
        var color = rgbacolors[index % (rgbacolors.length)];
        return "margin-left:" + (index * spacing - width + spacing) + "%;" + "width:" + width + "%;"
            + "background: linear-gradient(to left top, " + color + " 50%, transparent 50%);" + ";";
    },
    getContributionBarWidths(value, totalContributionLimit) {
        var widths = [];
        for (var i = 0; i < parseInt(value / totalContributionLimit); i++) {
            widths.push("100%");
        }
        widths.push((value % totalContributionLimit) / totalContributionLimit * 100 + "%");
        return widths;
    },
    getSliceTitle: function(value, intervalType) {
        if (intervalType == "authorDailyIntervalContributions"){
            return "contribution on " + value["sinceDate"] + ": " + value['insertions'] + " lines";
        } else{
            return "contribution from " + value["sinceDate"] + " to " + value["untilDate"] + ": "
                + value["insertions"] + " lines";
        }
    },
    getSliceGithubLink: function(timeSlice, authorRepo) {
        var url = "https://github.com/" +
            authorRepo.organization + "/" + authorRepo.repo +
            "/commits/" + authorRepo["branch"] +
            "?author=" + authorRepo["author"] + "&since=" +
            timeSlice["sinceDate"] + "&until=" + timeSlice["untilDate"];
        return "openInNewTab('" + url + "')";
    },
    generateBookmark : function(searchTerm,sortElement,sortOrder,isGroupByRepo,intervalType) {
        var url = window.location.href.split('?')[0] + "?";
        url += "searchTerm="+encodeURI(searchTerm);
        url += "&sortElement="+encodeURI(sortElement);
        url += "&sortOrder="+encodeURI(sortOrder);
        url += "&isGroupByRepo="+encodeURI(isGroupByRepo);
        url += "&intervalType="+encodeURI(intervalType);
        return "copyTextToClipboard('"+url+"');alert('copied bookmark to your clipboard!');";
    },
    getContributionBarTitle: function(value) {
        return "total contribution : " + value;
    },
    sortAndFilter(summary, searchTerm, sortElement, sortOrder, isGroupByRepo, docType) {
        summary = obtainSummariesForCombinedDocTypes(summary, docType);
        var authorRepos = [];
        for (var repo in summary) {
            if (!{}.hasOwnProperty.call(summary, repo)) {
                continue;
            }
            var newRepo = [];
            for (var author in summary[repo]["authorFinalContributionMap"]) {
                if ({}.hasOwnProperty.call(summary[repo]["authorFinalContributionMap"], author)) {
                    var authorRepo = {};
                    authorRepo["author"] = author;
                    authorRepo["authorDisplayName"] = summary[repo]["authorDisplayNameMap"][author];
                    authorRepo["displayName"] = summary[repo]["displayName"];
                    authorRepo["repo"] = summary[repo]["repoName"];
                    authorRepo["branch"] = summary[repo]["branch"];
                    authorRepo["organization"] = summary[repo]["organization"];
                    authorRepo["authorDailyIntervalContributions"] =
                    summary[repo]["authorDailyIntervalContributions"][author];
                    authorRepo["authorWeeklyIntervalContributions"] =
                    summary[repo]["authorWeeklyIntervalContributions"][author];
                    authorRepo["finalContribution"] = summary[repo]["authorFinalContributionMap"][author];
                    authorRepo["variance"] = summary[repo]["authorContributionVariance"][author];
                    if (isSearchMatch(searchTerm, authorRepo)) {
                        newRepo.push(authorRepo);
                     }
                }
            }
            authorRepos.push(newRepo);
        }
        if (isGroupByRepo === true) {
            for (var repoIndex in authorRepos) {
                if (!{}.hasOwnProperty.call(authorRepos, repoIndex)) {
                    continue;
                }
                authorRepos[repoIndex] = sortSegment(authorRepos[repoIndex], sortElement, sortOrder);
            }
            authorRepos = flatten(authorRepos);
        } else {
            authorRepos = flatten(authorRepos);
            sortSegment(authorRepos, sortElement, sortOrder);
        }
        return authorRepos;
    }
};
