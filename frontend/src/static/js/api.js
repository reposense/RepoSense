function loadJSON(file, fn){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", file);
    xhr.onload = function(){
        if(xhr.status == 200){
            fn(JSON.parse(xhr.responseText));
        }else{
            alert("unable to get file");
        }
    };
    xhr.send(null);
}

var api = {
    loadSummary: function(){ 
        loadJSON(REPORT_DIR+"/summary.json", repos => {
            REPOS = {};
            
            for(var i in repos){
                var repo = repos[i];
                var name = repo.organization+"_"+repo.repoName;
                REPOS[name] = repo;
                api.loadCommits(name);
            }
        });
    },

    loadCommits: function(repo){
        loadJSON(REPORT_DIR+"/"+repo+"/commits.json", commits => {
            REPOS[repo].commits = commits;
        });
    }
};
