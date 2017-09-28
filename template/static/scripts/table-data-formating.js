var prepareChartData = function(author) {
    var commitsLength = resultJson['commits'].length;
    var labels = []
    var countributionData = [];
    var issueData = [];

    for (var i = 0; i < commitsLength; i++) {
        var currentCommit = resultJson['commits'][i];
        labels.push(currentCommit['time']);
        countributionData.push(currentCommit['authorContributionMap'][author]);
        issueData.push(currentCommit['authorIssueMap'][author]);
    }

    var datasets = [];

    var lineChartData = {
        labels: labels,
        datasets: [{
            label: "Contribution",
            borderColor: "rgb(0,0,255)",
            backgroundColor: "rgb(0,0,255)",
            fill: false,
            data: countributionData,
            yAxisID: "contribution",
        }]

    };
    return lineChartData;
}

window.onload = function() {
    var currentAuthor = getQueryVariable('author');
    for (author in resultJson['commits'][0].authorContributionMap) {
        if (currentAuthor == null || currentAuthor == author) {
            var ctx = document.getElementById(getLegalClassName(author) + "-progress-canvas").getContext("2d");
            Chart.Line(ctx, {
                data: prepareChartData(author),
                options: {
                    responsive: true,
                    hoverMode: 'index',
                    stacked: false,
                    title: {
                        display: true,
                        text: author + "'s contribution and issue progress"
                    },
                    scales: {
                        yAxes: [{
                            type: "linear", // only linear but allow scale type registration. This allows extensions to exist solely for log scale for instance
                            display: true,
                            position: "left",
                            id: "contribution",
                            scaleLabel: {
                                display: true,
                                labelString: 'contribution'
                            }
                        }],
                    }
                }
            });

        }
    }

};