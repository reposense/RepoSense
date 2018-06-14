var REPORT_DIR = "";
var REPOS = {};

var app = new window.Vue({
    el: "#app",
    data: {
        reportDirInput: "",
        repos: {},
        repoLength: 0,
        loadedRepo: 0,
        userUpdated: false
    },
    methods: {
        // model funcs
        updateReportDir(evt) {
            REPORT_DIR = this.reportDirInput;
            this.users = [];

            window.api.loadSummary(() => {
                this.repos = REPOS;
                this.repoLength = Object.keys(REPOS).length;
                this.loadedRepo = 0;
            });
        },
        addUsers(users) {
            this.userUpdated = false;
            this.loadedRepo += 1;
            this.userUpdated = true;
        },
        getUsers() {
            var full = [];
            for(var repo in this.repos){
                if(this.repos[repo].users){
                    full.push(this.repos[repo]);
                }
            }
            return full;
        }
    },
    components: {
        "v_summary": window.vSummary
    },
});
