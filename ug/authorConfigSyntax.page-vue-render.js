
    const renderFn = new Function("const { createElementVNode: _createElementVNode, createTextVNode: _createTextVNode, resolveComponent: _resolveComponent, createVNode: _createVNode, withCtx: _withCtx, createStaticVNode: _createStaticVNode, Fragment: _Fragment, openBlock: _openBlock, createElementBlock: _createElementBlock } = Vue\n\nconst _hoisted_1 = { fixed: \"\" }\nconst _hoisted_2 = /*#__PURE__*/_createElementVNode(\"a\", {\n  href: \"/index.html\",\n  title: \"Home\",\n  class: \"navbar-brand\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"img\", {\n    width: \"30px\",\n    src: \"/favicon.ico\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_3 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/index.html\",\n    class: \"nav-link\"\n  }, \"HOME\")\n], -1 /* HOISTED */)\nconst _hoisted_4 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/showcase.html\",\n    class: \"nav-link\"\n  }, \"SHOWCASE\")\n], -1 /* HOISTED */)\nconst _hoisted_5 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/ug/index.html\",\n    class: \"nav-link\"\n  }, \"USER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_6 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/dg/index.html\",\n    class: \"nav-link\"\n  }, \"DEVELOPER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_7 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/about.html\",\n    class: \"nav-link\"\n  }, \"ABOUT\")\n], -1 /* HOISTED */)\nconst _hoisted_8 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/contact.html\",\n    class: \"nav-link\"\n  }, \"CONTACT\")\n], -1 /* HOISTED */)\nconst _hoisted_9 = /*#__PURE__*/_createElementVNode(\"li\", { tags: \"dev\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://reposense.org\",\n    class: \"nav-link\",\n    target: \"_blank\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fas fa-external-link-alt\"\n      }),\n      /*#__PURE__*/_createTextVNode(\" PRODUCTION SITE\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_10 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://github.com/RepoSense/reposense\",\n    target: \"_blank\",\n    class: \"nav-link\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fab fa-github\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_11 = { class: \"navbar-form\" }\nconst _hoisted_12 = /*#__PURE__*/_createElementVNode(\"div\", {\n  tags: \"dev\",\n  class: \"text-center bg-warning p-2\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"You are looking at the user documentation for the most recent \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n    /*#__PURE__*/_createTextVNode(\" branch of RepoSense (not released to the public yet). \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, [\n      /*#__PURE__*/_createTextVNode(\"The documentation for the latest public release is \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://reposense.org/ug\" }, \"here\"),\n      /*#__PURE__*/_createTextVNode(\".\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_13 = { id: \"flex-body\" }\nconst _hoisted_14 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-top\" }, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"font-weight-bold mb-2\",\n    style: {\"font-size\":\"1.25rem\"}\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"USER GUIDE\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_15 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_16 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/index.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Overview\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_17 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/generatingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Generating reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_18 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/usingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Using reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_19 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/customizingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Customizing reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_20 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/sharingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Sharing reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_21 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Appendices\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-dropdown-btn-container\" }, [\n      /*#__PURE__*/_createElementVNode(\"i\", {\n        class: \"site-nav-dropdown-btn-icon site-nav-rotate-icon\",\n        onclick: \"handleSiteNavClick(this.parentNode.parentNode, false); event.stopPropagation();\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"span\", {\n          class: \"glyphicon glyphicon-menu-down\",\n          \"aria-hidden\": \"true\"\n        })\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createElementVNode(\"ul\", { class: \"site-nav-dropdown-container site-nav-dropdown-container-open site-nav-list\" }, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/cli.html\" }, \"CLI syntax reference\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/reportConfig.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Getting started with \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"report-config.yaml\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/configFiles.html\" }, \"Config files format\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/authorConfigSyntax.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Advanced syntax: \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"author-config.csv\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/usingAuthorTags.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Using \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"@@author\"),\n          /*#__PURE__*/_createTextVNode(\" tags\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/withNetlify.html\" }, \"RepoSense with Netlify\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/withGithubActions.html\" }, \"RepoSense with GitHub Actions\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/runSh.html\" }, [\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"run.sh\"),\n          /*#__PURE__*/_createTextVNode(\" format\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/faq.html\" }, \"FAQ\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/troubleshooting.html\" }, \"Troubleshooting\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_22 = {\n  id: \"content-wrapper\",\n  class: \"fixed-header-padding\"\n}\nconst _hoisted_23 = /*#__PURE__*/_createElementVNode(\"h1\", {\n  class: \"display-4\",\n  id: \"appendix-author-config-csv-advanced-syntax\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"span\", null, [\n    /*#__PURE__*/_createTextVNode(\"Appendix: \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"author-config.csv\"),\n    /*#__PURE__*/_createTextVNode(\" advanced syntax\")\n  ]),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#appendix-author-config-csv-advanced-syntax\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_24 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"lead\" }, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Given below are the advanced syntax available for \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"author-config.csv\"),\n    /*#__PURE__*/_createTextVNode(\".\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_25 = /*#__PURE__*/_createElementVNode(\"h2\", { id: \"multiple-repository-s-location-per-author\" }, [\n  /*#__PURE__*/_createTextVNode(\"Multiple \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Repository's Location\"),\n  /*#__PURE__*/_createTextVNode(\" per author\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#multiple-repository-s-location-per-author\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_26 = /*#__PURE__*/_createElementVNode(\"h4\", { id: \"specifying-multiple-repository-s-location\" }, [\n  /*#__PURE__*/_createTextVNode(\"Specifying multiple \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Repository's Location\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#specifying-multiple-repository-s-location\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_27 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createTextVNode(\"To specify multiple \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Repository's Location\"),\n  /*#__PURE__*/_createTextVNode(\" in a single row, we can use a semicolon \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \";\"),\n  /*#__PURE__*/_createTextVNode(\" as a separator.\")\n], -1 /* HOISTED */)\nconst _hoisted_28 = /*#__PURE__*/_createElementVNode(\"p\", null, \"Below is an example:\", -1 /* HOISTED */)\nconst _hoisted_29 = /*#__PURE__*/_createStaticVNode(\"<div class=\\\"table-responsive\\\"><table class=\\\"markbind-table table table-bordered table-striped\\\"><thead><tr><th>Repository&#39;s Location</th> <th>Branch</th> <th>Author&#39;s Git Host ID</th> <th>... Hidden columns</th></tr></thead> <tbody><tr><td><code class=\\\"hljs inline no-lang\\\">https://github.com/reposense/RepoSense.git;https://github.com/MarkBind/markbind.git</code></td> <td>master</td> <td>sikai00</td> <td>--</td></tr></tbody></table></div><p>We have now furnished author details for both repositories in a single row, instead of having two individual rows per <code class=\\\"hljs inline no-lang\\\">Repository&#39;s Location</code>.</p>\", 2)\nconst _hoisted_31 = /*#__PURE__*/_createElementVNode(\"br\", null, null, -1 /* HOISTED */)\nconst _hoisted_32 = /*#__PURE__*/_createElementVNode(\"h4\", { id: \"specifying-repository-s-location-with-one-or-more-specific-branches\" }, [\n  /*#__PURE__*/_createTextVNode(\"Specifying \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Repository's Location\"),\n  /*#__PURE__*/_createTextVNode(\" with one or more specific branches\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#specifying-repository-s-location-with-one-or-more-specific-branches\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_33 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createTextVNode(\"To list out one or more branches for a particular \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Repository's Location\"),\n  /*#__PURE__*/_createTextVNode(\", we can use a pipe \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"|\"),\n  /*#__PURE__*/_createTextVNode(\" separator to demarcate the different branches.\")\n], -1 /* HOISTED */)\nconst _hoisted_34 = /*#__PURE__*/_createElementVNode(\"p\", null, \"Below is an example:\", -1 /* HOISTED */)\nconst _hoisted_35 = /*#__PURE__*/_createStaticVNode(\"<div class=\\\"table-responsive\\\"><table class=\\\"markbind-table table table-bordered table-striped\\\"><thead><tr><th>Repository&#39;s Location</th> <th>Branch</th> <th>Author&#39;s Git Host ID</th> <th>... Hidden columns</th></tr></thead> <tbody><tr><td><code class=\\\"hljs inline no-lang\\\">https://github.com/reposense/RepoSense.git~master|release|cypress</code></td> <td></td> <td>sikai00</td> <td>--</td></tr></tbody></table></div><p>We have now provided author details for three different branches in a single repo, using a single row.</p>\", 2)\nconst _hoisted_37 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createTextVNode(\"The branch specified through the delimiter syntax will take precedence over the \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Branch\"),\n  /*#__PURE__*/_createTextVNode(\" column.\")\n], -1 /* HOISTED */)\nconst _hoisted_38 = /*#__PURE__*/_createElementVNode(\"h2\", { id: \"using-github-branch-url-as-repository-s-location\" }, [\n  /*#__PURE__*/_createTextVNode(\"Using GitHub branch URL as \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Repository's Location\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#using-github-branch-url-as-repository-s-location\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_39 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createTextVNode(\"We can also use GitHub branch URL as copied from the address bar directly, to be able to quickly use the URL to specify the \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Repository's Location\"),\n  /*#__PURE__*/_createTextVNode(\" and a specific \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Branch\"),\n  /*#__PURE__*/_createTextVNode(\" at the same time.\")\n], -1 /* HOISTED */)\nconst _hoisted_40 = /*#__PURE__*/_createElementVNode(\"p\", null, \"Below is an example:\", -1 /* HOISTED */)\nconst _hoisted_41 = /*#__PURE__*/_createStaticVNode(\"<div class=\\\"table-responsive\\\"><table class=\\\"markbind-table table table-bordered table-striped\\\"><thead><tr><th>Repository&#39;s Location</th> <th>Branch</th> <th>Author&#39;s Git Host ID</th> <th>... Hidden columns</th></tr></thead> <tbody><tr><td><code class=\\\"hljs inline no-lang\\\">https://github.com/reposense/RepoSense/tree/release</code></td> <td></td> <td>sikai00</td> <td>--</td></tr></tbody></table></div><p>There is no need to specify the <code class=\\\"hljs inline no-lang\\\">Branch</code> column now, as we have specified it through the GitHub branch URL.</p>\", 2)\nconst _hoisted_43 = /*#__PURE__*/_createElementVNode(\"p\", null, \"GitLab and BitBucket branch URL are not supported at the moment.\", -1 /* HOISTED */)\nconst _hoisted_44 = /*#__PURE__*/_createElementVNode(\"h2\", { id: \"combining-the-advanced-syntax\" }, [\n  /*#__PURE__*/_createTextVNode(\"Combining the advanced syntax\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#combining-the-advanced-syntax\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_45 = /*#__PURE__*/_createElementVNode(\"p\", null, \"It is possible to combine the above-mentioned advanced syntax. By doing so, we can go from:\", -1 /* HOISTED */)\nconst _hoisted_46 = /*#__PURE__*/_createStaticVNode(\"<div class=\\\"table-responsive\\\"><table class=\\\"markbind-table table table-bordered table-striped\\\"><thead><tr><th>Repository&#39;s Location</th> <th>Branch</th> <th>Author&#39;s Git Host ID</th> <th>... Hidden columns</th></tr></thead> <tbody><tr><td><code class=\\\"hljs inline no-lang\\\">https://github.com/MarkBind/markbind/tree/vue3-migration</code></td> <td></td> <td>sikai00</td> <td>--</td></tr> <tr><td><code class=\\\"hljs inline no-lang\\\">https://github.com/reposense/RepoSense.git</code></td> <td>master</td> <td>sikai00</td> <td>--</td></tr> <tr><td><code class=\\\"hljs inline no-lang\\\">https://github.com/reposense/RepoSense.git</code></td> <td>cypress</td> <td>sikai00</td> <td>--</td></tr></tbody></table></div><p>to:</p>\", 2)\nconst _hoisted_48 = /*#__PURE__*/_createStaticVNode(\"<div class=\\\"table-responsive\\\"><table class=\\\"markbind-table table table-bordered table-striped\\\"><thead><tr><th>Repository&#39;s Location</th> <th>Branch</th> <th>Author&#39;s Git Host ID</th> <th>... Hidden columns</th></tr></thead> <tbody><tr><td><code class=\\\"hljs inline no-lang\\\">https://github.com/MarkBind/markbind/tree/vue3-migration;https://github.com/reposense/RepoSense.git~master|cypress</code></td> <td></td> <td>sikai00</td> <td>--</td></tr></tbody></table></div><br>\", 2)\nconst _hoisted_50 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"nav-component slim-scroll\" }, null, -1 /* HOISTED */)\nconst _hoisted_51 = /*#__PURE__*/_createElementVNode(\"footer\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"text-center\" }, [\n    /*#__PURE__*/_createElementVNode(\"small\", null, [\n      /*#__PURE__*/_createTextVNode(\"[\"),\n      /*#__PURE__*/_createElementVNode(\"span\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"Powered by\")\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"https://markbind.org/favicon.ico\",\n        width: \"30\"\n      }),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://markbind.org/\" }, \"MarkBind 6.0.0\"),\n      /*#__PURE__*/_createTextVNode(\", generated on Mon, 14 Apr 2025, 5:39:32 UTC]\")\n    ])\n  ])\n], -1 /* HOISTED */)\n\nreturn function render(_ctx, _cache) {\n  const _component_searchbar = _resolveComponent(\"searchbar\")\n  const _component_navbar = _resolveComponent(\"navbar\")\n  const _component_overlay_source = _resolveComponent(\"overlay-source\")\n  const _component_site_nav = _resolveComponent(\"site-nav\")\n  const _component_box = _resolveComponent(\"box\")\n\n  return (_openBlock(), _createElementBlock(_Fragment, null, [\n    _createElementVNode(\"header\", _hoisted_1, [\n      _createVNode(_component_navbar, { type: \"dark\" }, {\n        brand: _withCtx(() => [\n          _hoisted_2\n        ]),\n        right: _withCtx(() => [\n          _createElementVNode(\"li\", null, [\n            _createElementVNode(\"form\", _hoisted_11, [\n              _createVNode(_component_searchbar, {\n                data: _ctx.searchData,\n                placeholder: \"Search\",\n                \"on-hit\": _ctx.searchCallback,\n                \"menu-align-right\": \"\"\n              }, null, 8 /* PROPS */, [\"data\", \"on-hit\"])\n            ])\n          ])\n        ]),\n        default: _withCtx(() => [\n          _createTextVNode(),\n          _hoisted_3,\n          _createTextVNode(),\n          _hoisted_4,\n          _createTextVNode(),\n          _hoisted_5,\n          _createTextVNode(),\n          _hoisted_6,\n          _createTextVNode(),\n          _hoisted_7,\n          _createTextVNode(),\n          _hoisted_8,\n          _createTextVNode(),\n          _hoisted_9,\n          _createTextVNode(),\n          _hoisted_10,\n          _createTextVNode()\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _hoisted_12\n    ]),\n    _createTextVNode(),\n    _createElementVNode(\"div\", _hoisted_13, [\n      _createVNode(_component_overlay_source, {\n        id: \"site-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"site-nav\"\n      }, {\n        default: _withCtx(() => [\n          _hoisted_14,\n          _createTextVNode(),\n          _createElementVNode(\"div\", _hoisted_15, [\n            _createElementVNode(\"div\", null, [\n              _createVNode(_component_site_nav, null, {\n                default: _withCtx(() => [\n                  _createVNode(_component_overlay_source, {\n                    class: \"site-nav-list site-nav-list-root\",\n                    \"tag-name\": \"ul\",\n                    to: \"mb-site-nav\"\n                  }, {\n                    default: _withCtx(() => [\n                      _hoisted_16,\n                      _createTextVNode(),\n                      _hoisted_17,\n                      _createTextVNode(),\n                      _hoisted_18,\n                      _createTextVNode(),\n                      _hoisted_19,\n                      _createTextVNode(),\n                      _hoisted_20,\n                      _createTextVNode(),\n                      _hoisted_21\n                    ]),\n                    _: 1 /* STABLE */\n                  })\n                ]),\n                _: 1 /* STABLE */\n              })\n            ])\n          ])\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _createElementVNode(\"div\", _hoisted_22, [\n        _hoisted_23,\n        _createTextVNode(),\n        _hoisted_24,\n        _createTextVNode(),\n        _hoisted_25,\n        _createTextVNode(),\n        _hoisted_26,\n        _createTextVNode(),\n        _hoisted_27,\n        _createTextVNode(),\n        _hoisted_28,\n        _createTextVNode(),\n        _hoisted_29,\n        _createTextVNode(),\n        _hoisted_31,\n        _createTextVNode(),\n        _hoisted_32,\n        _createTextVNode(),\n        _hoisted_33,\n        _createTextVNode(),\n        _hoisted_34,\n        _createTextVNode(),\n        _hoisted_35,\n        _createTextVNode(),\n        _createVNode(_component_box, {\n          type: \"info\",\n          seamless: \"\"\n        }, {\n          default: _withCtx(() => [\n            _hoisted_37\n          ]),\n          _: 1 /* STABLE */\n        }),\n        _createTextVNode(),\n        _hoisted_38,\n        _createTextVNode(),\n        _hoisted_39,\n        _createTextVNode(),\n        _hoisted_40,\n        _createTextVNode(),\n        _hoisted_41,\n        _createTextVNode(),\n        _createVNode(_component_box, {\n          type: \"warning\",\n          seamless: \"\"\n        }, {\n          default: _withCtx(() => [\n            _hoisted_43\n          ]),\n          _: 1 /* STABLE */\n        }),\n        _createTextVNode(),\n        _hoisted_44,\n        _createTextVNode(),\n        _hoisted_45,\n        _createTextVNode(),\n        _hoisted_46,\n        _createTextVNode(),\n        _hoisted_48\n      ]),\n      _createTextVNode(),\n      _createVNode(_component_overlay_source, {\n        id: \"page-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"page-nav\"\n      }, {\n        default: _withCtx(() => [\n          _hoisted_50\n        ]),\n        _: 1 /* STABLE */\n      })\n    ]),\n    _createTextVNode(),\n    _hoisted_51\n  ], 64 /* STABLE_FRAGMENT */))\n}");
    var render = renderFn();
  