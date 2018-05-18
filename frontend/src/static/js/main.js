var REPORT_DIR = "";
var REPOS = {};

var app = new Vue({
    el: "#app",
    data: {
        reportDirInput: ""
    },
    methods:{
        updateReportDir: function(evt){
            REPORT_DIR = this.reportDirInput;
            api.loadSummary();
        },
    }
});
