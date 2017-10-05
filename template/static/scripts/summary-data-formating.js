var dynamicColors = function() {
    var r = Math.floor(Math.random() * 255);
    var g = Math.floor(Math.random() * 255);
    var b = Math.floor(Math.random() * 255);
    return "rgb(" + r + "," + g + "," + b + ")";
}

var prepareDistributionData = function(data) {
    dataset = [];
    labels = [];
    colors = [];
    for (author in data['authorFinalContributionMap']) {
        labels.push(author);
        dataset.push(data['authorFinalContributionMap'][author]);
        colors.push(dynamicColors());
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
            responsive: true
        }

    };
    return config;
}

window.onload = function() {
    piesMap ={};
    for (i in resultJson) {
        var id = resultJson[i].displayName + "-distribution-canvas";
        var canvas = document.getElementById(id);
        var ctx = canvas.getContext("2d");

        var currentChart = new Chart(ctx, prepareDistributionData(resultJson[i]));
        piesMap[resultJson[i].displayName] = currentChart;
        canvas.onclick = function(evt) {
            currentRepoName = (evt.srcElement.id).replace("-distribution-canvas","");

            var activePoints = piesMap[currentRepoName].getElementsAtEvent(evt);
            if (activePoints[0]) {
                var chartData = activePoints[0]['_chart'].config.data;
                var idx = activePoints[0]['_index'];

                var label = chartData.labels[idx];

                var url = currentRepoName +"/index.html?author="+label;
                window.location.href = url;

            }
        };
    }

};