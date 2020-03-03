<header><navbar placement="top" type="dark">
<a slot="brand" href="{{baseUrl}}/index.html" title="Home" class="navbar-brand"><img src = "frontend/src/favicon.ico"></i></a>
  <li><a href="{{baseUrl}}/index.html" class="nav-link">HOME</a></li>
  <li><a href="{{baseUrl}}/about.html" class="nav-link">ABOUT US</a></li>
  <li slot="right">
    <form class="navbar-form">
      <searchbar :data="searchData" placeholder="Search" :on-hit="searchCallback" menu-align-right></searchbar>
    </form>
  </li>
</navbar></header>
