<header>
  <navbar type="dark">
    <a slot="brand" href="{{baseUrl}}/index.html" title="Home" class="navbar-brand">Your Logo</a>
    <li><a href="{{baseUrl}}/contents/topic1.html" class="nav-link">Topic 1</a></li>
    <li><a href="{{baseUrl}}/contents/topic2.html" class="nav-link">Topic 2</a></li>
    <dropdown header="Topic 3" class="nav-link">
      <li><a href="{{baseUrl}}/contents/topic3a.html" class="dropdown-item">Topic 3a</a></li>
      <li><a href="{{baseUrl}}/contents/topic3b.html" class="dropdown-item">Topic 3b</a></li>
    </dropdown>
    <li slot="right">
      <form class="navbar-form">
        <searchbar :data="searchData" placeholder="Search" :on-hit="searchCallback" menu-align-right></searchbar>
      </form>
    </li>
  </navbar>
</header>
