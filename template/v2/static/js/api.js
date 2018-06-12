/* util funcs */
function $(id){
    return document.getElementById(id);
}

function enquery(key, val){
    return key + "=" + encodeURIComponent(val);
}

/* api funcs */
function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        if(xhr.status === 200){
            fn(JSON.parse(xhr.responseText));
        }else{
            alert("unable to get file");
        }
    };
    xhr.send(null);
}

var api = {
    loadSummary(callback) {
        var REPORT_DIR = window.REPORT_DIR;
        window.REPOS = {};

        loadJSON(REPORT_DIR+"/summary.json", (repos) => {
            var names = [];
            for(var repo of repos){
                var repoName = repo.organization+"_"+repo.repoName;
                window.REPOS[repoName] = repo;
                names.push(repoName);
            }

            if(callback){
                callback();
            }

            for(var name of names){
                api.loadCommits(name);
            }
        });
    },

    loadCommits(repoName) {
        var REPORT_DIR = window.REPORT_DIR;

        loadJSON(REPORT_DIR+"/"+repoName+"/commits.json", (commits) => {
            var res = [];
            var repo = window.REPOS[repoName];

            for(var author in commits.authorDisplayNameMap){
                if(!author){ continue; }

                var obj = {
                    name: author,
                    repoId: repoName,
                    variance: commits.authorContributionVariance[author],
                    displayName: commits.authorDisplayNameMap[author],
                    weeklyCommits: commits.authorWeeklyIntervalContributions[author],
                    dailyCommits: commits.authorDailyIntervalContributions[author],
                    totalCommits: commits.authorFinalContributionMap[author],
                };

                var searchParams = [
                    repo.organization, repo.repoName,
                    obj.displayName, author
                ];

                obj.searchPath = searchParams.join("/").toLowerCase();
                obj.repoPath = repo.organization + "/" + repo.repoName;

                res.push(obj);
            }

            repo.commits = commits;
            repo.users = res;

            var app = window.app;
            app.addUsers();
        });
    }
};
