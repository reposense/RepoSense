var dynamicColors = function() {
    var r = Math.floor(Math.random() * 255);
    var g = Math.floor(Math.random() * 255);
    var b = Math.floor(Math.random() * 255);
    return "rgb(" + r + "," + g + "," + b + ")";
}

var authorColors = {};
var repoInfoIndexMap = {};

var prepareProgressData = function(data) {
    datasets = [];
    labels = [];
    //init labels
    firstKey = Object.keys(data)[0];
    for (var i in data[firstKey]) {
        labels.push(data[firstKey][i]['date']);
    }

    for (var author in data) {
        currentDeletions = [];
        currentInsertions = [];
        for (i in data[author]) {
            temp = data[author][i];
            currentDeletions.push(-temp['deletions'])
            currentInsertions.push(temp['insertions'])
        }
        datasets.push({
            data: currentDeletions,
            label: author + " deletions",
            backgroundColor: authorColors[author],
            stack: author,

        })
        datasets.push({
            data: currentInsertions,
            label: author + " insertions",
            backgroundColor: authorColors[author],
            stack: author,

        })
    }


    var config = {
        type: 'bar',
        data: {
            datasets: datasets,
            labels: labels,

        },
        options: {
            responsive: true,
            onClick: githubCommitsLink,
            legend: {
                display: false
            },
            title: {
                display: true,
                position: 'left',
                text: "Contribution Progress"
            },
            scales: {
                yAxes: [{
                    ticks: {
                        display: false
                    }
                }],
                xAxes: [{
                    ticks: {
                        display: false
                    }
                }]
            }

        }

    };
    return config;

}

var prepareDistributionData = function(data) {
    var dataset = [];
    var labels = [];
    var colors = [];
    for (var author in data['authorFinalContributionMap']) {
        labels.push(author);
        dataset.push(data['authorFinalContributionMap'][author]);
        colors.push(authorColors[author]);
    }
    var config = {
        type: 'pie',
        data: {
            datasets: [{
                data: dataset,
                label: 'Final Contribution Distribution',
                backgroundColor: colors,

            }],
            labels: labels
        },
        options: {
            responsive: true,
            title: {
                display: true,
                position: 'left',
                text: "Contribution Distribution"
            },
            legend: {
                display: false
            },
        }

    };
    return config;
}

function githubCommitsLink(event, array) {
    var element = this.getElementAtEvent(event);
    var canvasId = element[0]._chart.canvas.id;
    var repoInfo = summaryJson[repoInfoIndexMap[canvasId]];
    var authorRawTag = element[0]._model.datasetLabel;
    var author = authorRawTag.substring(0, authorRawTag.lastIndexOf(" "))
    var date = element[0]._model.label;
    var nextDate = getNextDate(date, repoInfo);
    var url = "https://github.com/" +
        repoInfo["organization"] + "/" + repoInfo["repo"] +
        "/commits/" + repoInfo["branch"] +
        "?author=" + author + "&since=" +
        date;
    if (nextDate != null) {
        url += ("&until=" + nextDate);
    }
    var win = window.open(url, '_blank');
    win.focus();
}

function getNextDate(date, repoInfo) {
    var intervalMap = repoInfo["authorIntervalContributions"]
    var elements = intervalMap[Object.keys(intervalMap)[0]];
    for (var i = 0; i < elements.length; i++) {
        if (elements[i]['date'] == date) {
            if (i != elements.length - 1) {
                return elements[i + 1]['date']
            } else {
                return null;
            }
        }
    }
    return null;

}

window.onload = function() {
    piesMap = {};
    for (var i in summaryJson) {
        for (author in summaryJson[i]['authorIntervalContributions']) {
            authorColors[author] = dynamicColors();
        }
    }
    for (var i in summaryJson) {
        var id = summaryJson[i].displayName + "-distribution-canvas";
        var canvas = document.getElementById(id);
        var ctx = canvas.getContext("2d");

        var currentChart = new Chart(ctx, prepareDistributionData(summaryJson[i]));
        piesMap[summaryJson[i].displayName] = currentChart;
        canvas.onclick = function(evt) {
            currentRepoName = (evt.srcElement.id).replace("-distribution-canvas", "");

            var activePoints = piesMap[currentRepoName].getElementsAtEvent(evt);
            if (activePoints[0]) {
                var chartData = activePoints[0]['_chart'].config.data;
                var idx = activePoints[0]['_index'];

                var label = chartData.labels[idx];

                var url = currentRepoName + "/index.html?author=" + label;
                window.location.href = url;

            }
        };
    }

    for (var i in summaryJson) {
        var id = summaryJson[i].displayName + "-progress-canvas";
        var canvas = document.getElementById(id);
        var ctx = canvas.getContext("2d");
        var currentChart = new Chart(ctx, prepareProgressData(summaryJson[i]['authorIntervalContributions']));
        repoInfoIndexMap[id] = i;
    }
};