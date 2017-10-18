var dynamicColors = function() {
    var r = Math.floor(Math.random() * 255);
    var g = Math.floor(Math.random() * 255);
    var b = Math.floor(Math.random() * 255);
    return "rgb(" + r + "," + g + "," + b + ")";
}

var authorColors = {};
var repoInfoIndexMap = {};
var currentRepo = getQueryVariable('repo');


var prepareProgressData = function(author) {
    data = summaryJson[currentRepo]['authorIntervalContributions'][author],
        datasets = [];
    labels = [];
    //init labels
    for (var i in data) {
        labels.push(data[i]['date']);
    }

    currentDeletions = [];
    currentInsertions = [];
    for (i in data) {
        temp = data[i];
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
    var repoInfo = summaryJson[currentRepo];
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
    for (author in summaryJson[currentRepo]['authorIntervalContributions']) {
        authorColors[author] = dynamicColors();
    }
    var id = "distribution-canvas";
    var canvas = document.getElementById(id);
    var ctx = canvas.getContext("2d");

    var currentChart = new Chart(ctx, prepareDistributionData(summaryJson[currentRepo]));
    canvas.onclick = function(evt) {
        var activePoints = currentChart.getElementsAtEvent(evt);
        console.log(currentChart)
        if (activePoints[0]) {
            var chartData = activePoints[0]['_chart'].config.data;
            var idx = activePoints[0]['_index'];

            var label = chartData.labels[idx];

            var url = currentRepo + "/index.html?author=" + label;
            window.location.href = url;

        }
    };

    for (var author in summaryJson[currentRepo]['authorIntervalContributions']) {
        var id = author + "-progress-canvas";
        var canvas = document.getElementById(id);
        var ctx = canvas.getContext("2d");
        new Chart(ctx, prepareProgressData(author));
    }
};