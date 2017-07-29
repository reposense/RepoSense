//console.log(resultJson['commits'][0]['authorContributionMap']);

var commitsLength = resultJson['commits'].length;
var resultMap = {};
var labels = []

for (var i = 0; i < commitsLength; i++) {
    var currentCommit = resultJson['commits'][i];
    labels.push(currentCommit['time']);
    var authorContributionMap = currentCommit['authorContributionMap'];
    for (var author in authorContributionMap) {
      if (!(author in resultMap)){
          resultMap[author] = zeros(i);
      }
      resultMap[author].push(authorContributionMap[author]);
    } 
    normalizeResult(resultMap, i+1);

}

var datasets = [];

for (author in resultMap){
  var authorData = {
    label:author,
    backgroundColor: dynamicColors(),
    borderWidth: 1,
    data:resultMap[author]
  }
  datasets.push(authorData);
}

var barChartData = {
    labels: labels,
    datasets: datasets

};

window.onload = function() {
    var ctx = document.getElementById("contribution-progress-canvas").getContext("2d");
    window.myBar = new Chart(ctx, {
        type: 'bar',
        data: barChartData,
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