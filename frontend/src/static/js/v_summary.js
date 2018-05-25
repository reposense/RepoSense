function comparator(fn){ return function(a, b){ 
    var a1 = fn(a), b1=fn(b);
    if(a1 == b1){ return 0; }
    else if(a1 < b1){ return -1; }
    return 1;
};}

/* dates funcs */
const DAY = (1000*60*60*24);
function getIntervalDay(a, b){
    var diff = Date.parse(a) - Date.parse(b); 
    return diff/DAY;
}
function getIntervalWeek(a, b){
    return getIntervalDay(a, b)/7;
}
function getDateStr(date){
    return (new Date(date)).toISOString().split("T")[0];
}
function dateRounding(datestr, roundDown){
    // rounding up to nearest sunday
    var date = new Date(datestr);
    var day = date.getUTCDay();
    var datems = date.getTime();
    if(roundDown){
        datems -= day*DAY;
    }else{
        datems += (7-day)*DAY;
    }

    return getDateStr(datems); 
}

var vSummary = {
    props: ["repos"],
    template: $("v_summary").innerHTML,
    data: function(){
        return { 
            filtered: [],
            rampScale: 0.1,
            filterSearch: "",
            filterSort: "totalCommits",
            filterSortReverse: false,
            filterGroupRepos: true,
            filterGroupWeek: false,
            filterSinceDate: "",
            filterUntilDate: "",
            filterHash: ""
        };
    },
    watch:{ 
        repos: function(){ this.getFiltered(); },
        filterSort: function(){ this.getFiltered(); },
        filterSortReverse: function(){ this.getFiltered(); },
        filterGroupRepos: function(){ this.getFiltered(); },
        filterGroupWeek: function(){ this.getFiltered(); },
        filterSinceDate: function(){ this.getFiltered(); },
        filterUntilDate: function(){ this.getFiltered(); }
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
        // view funcs
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
        },
        // model funcs
        getFilterHash: function(){ 
            this.filterSearch = this.filterSearch.toLowerCase();
            this.filterHash = [
                enquery("search", this.filterSearch),
                enquery("sort", this.filterSort),
                enquery("reverse", this.filterSortReverse),
                enquery("repoSort", this.filterGroupRepos),
                enquery("since", this.filterSinceDate),
                enquery("until", this.filterUntilDate)
            ].join('&');

            window.location.hash = this.filterHash;
        },
        getDates: function(){
            if(this.filterSinceDate && this.filterUntilDate){
                return;
            }

            var minDate="", maxDate="";
            for(repo of this.filtered){
                for(user of repo){
                    var commits = user.commits;
                    var date1 = commits[0].fromDate;
                    var date2 = commits[commits.length-1].fromDate;
                    if(!minDate || minDate>date1){ minDate=date1; }
                    if(!maxDate || maxDate<date2){ maxDate=date2; }
                }
            }

            if(!this.filterSinceDate){ this.filterSinceDate=minDate; }
            if(!this.filterUntilDate){ this.filterUntilDate=maxDate; }
        },
        getFiltered: function(){ 
            this.getFilterHash();

            // array of array, sorted by repo
            var full = []; 

            for(repo of this.repos){
                var res = [];
                
                // filtering
                for(user of repo.users){ 
                    if(user.searchPath.search(this.filterSearch)>-1){
                        this.getUserCommits(user);
                        if(this.filterGroupWeek){ this.splitCommitsWeek(user); }
                        res.push(user);
                    } 
                }

                if(res.length){ full.push(res); }
            } 
            this.filtered = full;

            this.sortFiltered();
            this.getDates();
        },
        splitCommitsWeek: function(user){
            var commits = user.commits;
            var leng = commits.length;

            var res = []; 
            for(var i=0; i<(leng-1)/7; i++){
                var week = {
                    insertions:0,
                    deletions:0,
                    fromDate:commits[i*7].fromDate,
                    toDate:""
                };

                for(var j=0; j<7; j++){
                    var commit = commits[i*7 + j];
                    week.insertions += commit.insertions;
                    week.deletions += commit.deletions;     
                    week.toDate = commit.toDate;
                }
                
                res.push(week);
            }

            user.commits = res;
        },
        getUserCommits: function(user){
            user["commits"] = []; 
            var userFirst = user.dailyCommits[0];
            var userLast = user.dailyCommits[user.dailyCommits.length-1];

            var sinceDate = this.filterSinceDate;
            if(!sinceDate){ sinceDate = userFirst.fromDate; }

            if(this.filterGroupWeek){ sinceDate = dateRounding(sinceDate, 1); }
            var diff = getIntervalDay(userFirst.fromDate, sinceDate); 

            var startMs = (new Date(sinceDate)).getTime();
            for(var i=0; i<diff; i++){ 
                user.commits.push({ 
                    insertions:0, 
                    deletions:0,
                    fromDate:getDateStr(startMs + i*DAY),
                    toDate:getDateStr(startMs + (i+1)*DAY)
                }); 
            }

            for(commit of user.dailyCommits){
                if(commit.fromDate<sinceDate){ continue; } 
                if(commit.fromDate>untilDate){ break; }
                user.commits.push(commit);
            }

            var untilDate = this.filterUntilDate;
            if(!untilDate){ untilDate = userLast.fromDate; }

            if(this.filterGroupWeek){ untilDate = dateRounding(untilDate); }
            diff = getIntervalDay(untilDate, userLast.fromDate);

            var endMs = (new Date(userLast.fromDate)).getTime();
            for(var i=0; i<diff; i++){ 
                user.commits.push({ 
                    insertions:0, 
                    deletions:0,
                    fromDate:getDateStr(endMs + i*DAY),
                    endDate:getDateStr(endMs + (i+1)*DAY)
                }); 
            }
        },
        sortFiltered: function(){ 
            var full = [];
            if(this.filterGroupRepos){
                for(users of this.filtered){ 
                    users.sort(comparator(ele => ele[this.filterSort]));
                    full.push(users);
                }
            }else{
                full.push([]);
                for(users of this.filtered){
                    for(user of users){
                        full[0].push(user);
                    }
                }

                full[0].sort(comparator(ele => ele[this.filterSort]));
            }

            if(this.filterSortReverse){
                for(users of full){ users.reverse(); }
            }

            this.filtered = full;
        }
    },
    created: function(){ this.getFiltered(); }
};
