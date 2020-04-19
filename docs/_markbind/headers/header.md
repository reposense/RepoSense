<header><navbar type="dark" placement="top" type="inverse">
  <a slot="brand" href="{{baseUrl}}/index.html" title="Home" class="navbar-brand"><img width="30px" src="{{baseUrl}}/favicon.ico"></img></a>
  <li><a href="{{baseUrl}}/index.html" class="nav-link">HOME</a></li>
  <li><a href="{{baseUrl}}/UserGuide.html" class="nav-link">USER GUIDE</a></li>
  <li><a href="{{baseUrl}}/DeveloperGuide.html" class="nav-link">DEVELOPER GUIDE</a></li>
  <li><a href="{{baseUrl}}/about.html" class="nav-link">ABOUT</a></li>
  <li>
    <a href="https://github.com/RepoSense/reposense" target="_blank" class="nav-link"><md>:fab-github:</md></a>
  </li>
  <li slot="right">
    <form class="navbar-form">
      <searchbar :data="searchData" placeholder="Search" :on-hit="searchCallback" menu-align-right></searchbar>
    </form>
  </li>
</navbar></header>
