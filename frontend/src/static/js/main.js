var REPORT_DIR = "";
var REPOS = {};

function $(id){ return document.getElementById(id); }

const vSummary = {
    props: ["repos"],
    template: $("v_summary").innerHTML,
    data: function(){
        return { 
            filtered: [],
            rampScale: 0.1,
            filterSearch: "",
            filterSort: "totalCommits",
            filterHash: ""
        };
    },
    watch:{ 
        filterHash: function(){ this.getFiltered(); },
        repos: function(){ this.getFiltered(); }
    },
    computed: {
        sliceCount: function(){ return this.filtered[0][0].commits.length; },
        sliceWidth: function(){ return 100/this.sliceCount; },
        avgCommitSize: function(){
            var totalCommits=0, totalCount=0;
            for(repo of this.filtered){
                for(user of repo){
                    for(slice of user.commits){
                        if(slice.insertions==0){ continue; }
                        totalCount += 1;
                        totalCommits += slice.insertions;
                    }
                }
            }
            return totalCommits/totalCount;
        },
        avgContributionSize: function(){
            var totalLines=0, totalCount=0; 
            for(repo of this.filtered){
                for(user of repo){
                    if(user.totalCommits==0){ continue; }
                    totalCount += 1;
                    totalLines += user.totalCommits;
                }
            }
            return totalLines/totalCount;
        }
    },
    methods: {
        getFilterHash: function(){ 
            this.filterSearch = this.filterSearch.toLowerCase();
            this.filterHash = "search=" + encodeURIComponent(this.filterSearch) + "&";
            this.filterHash = "sort=" + encodeURIComponent(this.filterSort) + "&";

            window.location.hash = this.filterHash;
        },
        getFiltered: function(){ 
            this.getFilterHash();

            // array of array, sorted by repo
            var full = []; 

            for(repo of this.repos){
                var res = [];
                
                // filtering
                for(user of repo.users){ 
                    if(user.searchPath.search(this.filterSearch) == -1){
                        continue; 
                    } 
                    res.push(user);
                }

                // sorting
                res = res.sort((a, b) => {
                    return a[this.filterSort] - b[this.filterSort];
                });

                // getting the ramp slices
                for(user of res){
                    // TODO: group by week
                    user["commits"] = user["dailyCommits"];
                }

                full.push(res);
            }

            this.filtered = full;
        },
        getWidth: function(slice){
            if(slice.insertions==0){ return 0; }

            var size = this.sliceWidth;
            size *= slice.insertions/this.avgCommitSize;
            return Math.max(size*this.rampScale, 0.5);
        },
        getSliceTitle: function(slice){
            return "contribution on " + slice.fromDate +
                ": " + slice.insertions + " lines";
        },
        getSliceLink: function(user, slice){
            return 'http://github.com/' +
              REPOS[user.repoId].organization + '/' +
              REPOS[user.repoId].repoName + '/commits/' +
              REPOS[user.repoId].branch + '?' +
              'author=' + user.name + '&' +
              'since=' + slice.fromDate + '&' +
              'until=' + slice.toDate;
        },
        getContributionBars: function(totalContribution){
            var res = []; 
            var contributionLimit = (this.avgContributionSize*2);

            var cnt = parseInt(totalContribution/contributionLimit);
            for(i=0; i<cnt; i++){ res.push(100); }
            
            var last = (totalContribution%contributionLimit)/contributionLimit;
            if(last!=0){ res.push(last*100); }

            return res;
        }
    },
    created: function(){ this.getFiltered(); }
};

var app = new Vue({
    el: "#app",
    data: {
        reportDirInput: "",
        repos: {},
        repoLength: 0,
        loadedRepo: 0,
        userUpdated: false
    },
    methods:{
        // model funcs
        updateReportDir: function(evt){ 
            REPORT_DIR = this.reportDirInput;
            this.users = [];

            api.loadSummary(() => {
                this.repos = REPOS;
                this.repoLength = Object.keys(REPOS).length;
                this.loadedRepo = 0;
            });
        },
        addUsers: function(users){
            this.userUpdated = false;
            this.loadedRepo += 1;
            this.userUpdated = true;
        },
        getUsers: function(){
            var full = [];
            for(var repo in this.repos){
                if(!this.repos[repo].users){ continue; }
                full.push(this.repos[repo]); 
            }
            return full;
        }
    },
    components:{
        "v_summary": vSummary
    },
});
