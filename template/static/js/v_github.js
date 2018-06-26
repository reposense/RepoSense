window.vGithubCommits = {
  props: ["githubPath"],
  template: window.$("v_github_commits").innerHTML,
  data(){
    return {
      commits: [],
      commitMsgLeng: 80,
    };
  },
  methods:{
    shorten(rawTxt){
      const txt = rawTxt.split("\n")[0];
      if(txt.length > this.commitMsgLeng){
        return `${txt.slice(0, this.commitMsgLeng)}...`;
      }
      else{
        return txt;
      }
    }
  },
  created(){
    const xhr = new XMLHttpRequest();
    xhr.open("GET", `https://api.github.com${this.githubPath}`);
    xhr.onload = () => {
      if (xhr.status === 200) {
        this.commits = JSON.parse(xhr.responseText);
        console.log(this.commits);
      }
    };
    xhr.send(null);
  },
};
