var REPORT_DIR = "";
var REPOS = {};

var app = new Vue({
    el: "#app",
    data: {
        reportDirInput: "",
        repos: {},
        users: []
    },
    methods:{
        updateReportDir: function(evt){
            REPORT_DIR = this.reportDirInput;
            this.users = [];

            api.loadSummary(() => this.repos=REPOS);
        },

        addUsers: function(users){ 
            for(var i in users){ this.users.push(users[i]); }
        }
    }
});
