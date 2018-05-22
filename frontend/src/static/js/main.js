var REPORT_DIR = "";
var REPOS = {};

function $(id){ return document.getElementById(id); }

const vSummary = {
    props: ["users"],
    template: $("v_summary").innerHTML,
    data: function(){
        return { rampScale: 0.1 };
    },
    computed: {
        filtered: function(){
            var res = [];
            for(user of this.users){
                user["commits"] = user["dailyCommits"];
                res.push(user);
            }
            return res;
        },
        sliceCount: function(){ return this.filtered[0].commits.length; },
        sliceWidth: function(){ return 100/this.sliceCount; },
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
        },
        avgContributionSize: function(){
            var totalLines=0, totalCount=0;
            for(user of this.filtered){
                if(user.totalCommits==0){ continue; }
                totalCount += 1;
                totalLines += user.totalCommits;
            }
            return totalLines/totalCount;
        }
    },
    methods: {
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
    }
};

var app = new Vue({
    el: "#app",
    data: {
        reportDirInput: "",
        repos: {},
        users: [],
        userUpdated: false
    },
    methods:{
        // model funcs
        updateReportDir: function(evt){
            REPORT_DIR = this.reportDirInput;
            this.users = [];

            api.loadSummary(() => this.repos=REPOS);
        },
        addUsers: function(users){
            this.userUpdated = false;
            for(var i in users){ this.users.push(users[i]); }
            this.userUpdated = true;
        }
    },
    components:{
        "v_summary": vSummary
    }
});
