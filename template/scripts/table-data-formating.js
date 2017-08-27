//console.log(resultJson['commits'][0]['authorContributionMap']);



var prepareChartData = function(datasetName) {
    var commitsLength = resultJson['commits'].length;
    var resultMap = {};
    var labels = []

    for (var i = 0; i < commitsLength; i++) {
        var currentCommit = resultJson['commits'][i];
        labels.push(currentCommit['time']);
        var rawDataMap = currentCommit[datasetName];
        for (var author in rawDataMap) {
            if (!(author in resultMap)) {
                resultMap[author] = zeros(i);
            }
            resultMap[author].push(rawDataMap[author]);
        }

    }

    var datasets = [];

    for (author in resultMap) {
        var authorData = {
            label: author,
            backgroundColor: dynamicColors(),
            borderWidth: 1,
            data: resultMap[author]
        }
        datasets.push(authorData);
    }

    var barChartData = {
        labels: labels,
        datasets: datasets

    };
    return barChartData;
}

window.onload = function() {
    var ctx1 = document.getElementById("contribution-progress-canvas").getContext("2d");
    window.bar1 = new Chart(ctx1, {
        type: 'bar',
        data: prepareChartData('authorContributionMap'),
        options: {
            responsive: true,
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'contribution progress'
            }
        }
    });
    var ctx2 = document.getElementById("issue-progress-canvas").getContext("2d");
    window.bar2 = new Chart(ctx2, {
        type: 'bar',
        data: prepareChartData('authorIssueMap'),
        options: {
            responsive: true,
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'contribution progress'
            }
        }
    });

};