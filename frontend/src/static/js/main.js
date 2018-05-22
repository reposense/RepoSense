var REPORT_DIR = "";
var REPOS = {};

var RAMP_SCALE = 0.1;

var app = new Vue({
    el: "#app",
    data: {
        reportDirInput: "",
        repos: {},
        users: []
    },
    computed:{
        sliceCount: function(){ return this.filtered[0].commits.length; },
        sliceWidth: function(){ return 100/this.sliceCount; },
        filtered: function(){
            var res = [];
            for(user of this.users){
                user["commits"] = user["dailyCommits"];
                res.push(user);
            }
            return res;
        },
        avgCommitSize: function(){
            var totalCommits=0, totalCount=0;
            for(user of this.filtered){
                for(slice of user.commits){
                    if(slice.insertions==0){ continue; }
                    totalCount += 1;
                    totalCommits += slice.insertions;
                }
            }

            return totalCommits/totalCount;
        } 
    },
    methods:{
        // model funcs
        updateReportDir: function(evt){
            REPORT_DIR = this.reportDirInput;
            this.users = [];

            api.loadSummary(() => this.repos=REPOS);
        },
        addUsers: function(users){ 
            for(var i in users){ this.users.push(users[i]); }
        },

        // view funcs
        getWidth(slice){
            if(slice.insertions==0){ return 0; }

            var size = this.sliceWidth;
            size *= slice.insertions/this.avgCommitSize;
            return Math.max(size*RAMP_SCALE, 0.5);
        },
        getSliceTitle(slice){
            return "contribution on " + slice.fromDate + 
                ": " + slice.insertions + " lines";
        },
        getSliceLink(user, slice){
            console.log(user);
            return 'http://github.com/' +
              this.repos[user.repoId].organization + '/' +
              this.repos[user.repoId].repoName + '/commits/' +
              this.repos[user.repoId].branch + '?' +
              'author=' + user.name + '&' +
              'since=' + slice.fromDate + '&' +
              'until=' + slice.toDate;
        }
    }
});
