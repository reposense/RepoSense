var REPORT_DIR = "";
var REPOS = {};

var app = new Vue({
    el: "#app",
    data: {
        reportDirInput: ""
    },
    methods:{
        updateReportDir: function(evt){
            // TODO: reload the whole object 
            REPORT_DIR = this.reportDirInput;
            loadJSON(REPORT_DIR+"/repo.json", obj => {
                REPOS = obj;
            });
        }
    }
});
