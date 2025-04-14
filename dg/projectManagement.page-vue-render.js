
    const renderFn = new Function("const { createElementVNode: _createElementVNode, createTextVNode: _createTextVNode, resolveComponent: _resolveComponent, createVNode: _createVNode, withCtx: _withCtx, Fragment: _Fragment, openBlock: _openBlock, createElementBlock: _createElementBlock } = Vue\n\nconst _hoisted_1 = { fixed: \"\" }\nconst _hoisted_2 = /*#__PURE__*/_createElementVNode(\"a\", {\n  href: \"/index.html\",\n  title: \"Home\",\n  class: \"navbar-brand\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"img\", {\n    width: \"30px\",\n    src: \"/favicon.ico\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_3 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/index.html\",\n    class: \"nav-link\"\n  }, \"HOME\")\n], -1 /* HOISTED */)\nconst _hoisted_4 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/showcase.html\",\n    class: \"nav-link\"\n  }, \"SHOWCASE\")\n], -1 /* HOISTED */)\nconst _hoisted_5 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/ug/index.html\",\n    class: \"nav-link\"\n  }, \"USER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_6 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/dg/index.html\",\n    class: \"nav-link\"\n  }, \"DEVELOPER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_7 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/about.html\",\n    class: \"nav-link\"\n  }, \"ABOUT\")\n], -1 /* HOISTED */)\nconst _hoisted_8 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/contact.html\",\n    class: \"nav-link\"\n  }, \"CONTACT\")\n], -1 /* HOISTED */)\nconst _hoisted_9 = /*#__PURE__*/_createElementVNode(\"li\", { tags: \"dev\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://reposense.org\",\n    class: \"nav-link\",\n    target: \"_blank\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fas fa-external-link-alt\"\n      }),\n      /*#__PURE__*/_createTextVNode(\" PRODUCTION SITE\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_10 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://github.com/RepoSense/reposense\",\n    target: \"_blank\",\n    class: \"nav-link\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fab fa-github\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_11 = { class: \"navbar-form\" }\nconst _hoisted_12 = { id: \"flex-body\" }\nconst _hoisted_13 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-top\" }, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"font-weight-bold mb-2\",\n    style: {\"font-size\":\"1.25rem\"}\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"DEVELOPER GUIDE\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_14 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_15 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/index.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Contributing\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_16 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/settingUp.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Setting up\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_17 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/learningBasics.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Learning the basics\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_18 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/workflow.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Workflow\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_19 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Design and implementation\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-dropdown-btn-container\" }, [\n      /*#__PURE__*/_createElementVNode(\"i\", {\n        class: \"site-nav-dropdown-btn-icon site-nav-rotate-icon\",\n        onclick: \"handleSiteNavClick(this.parentNode.parentNode, false); event.stopPropagation();\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"span\", {\n          class: \"glyphicon glyphicon-menu-down\",\n          \"aria-hidden\": \"true\"\n        })\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createElementVNode(\"ul\", { class: \"site-nav-dropdown-container site-nav-dropdown-container-open site-nav-list\" }, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/architecture.html\" }, \"Architecture\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/report.html\" }, \"HTML report\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_20 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/projectManagement.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Project management\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_21 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/devOpsGuide.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"DevOps guide\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_22 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Appendices\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-dropdown-btn-container\" }, [\n      /*#__PURE__*/_createElementVNode(\"i\", {\n        class: \"site-nav-dropdown-btn-icon site-nav-rotate-icon\",\n        onclick: \"handleSiteNavClick(this.parentNode.parentNode, false); event.stopPropagation();\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"span\", {\n          class: \"glyphicon glyphicon-menu-down\",\n          \"aria-hidden\": \"true\"\n        })\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createElementVNode(\"ul\", { class: \"site-nav-dropdown-container site-nav-dropdown-container-open site-nav-list\" }, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/cli.html\" }, \"CLI syntax reference\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/styleGuides.html\" }, \"Style guides\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_23 = /*#__PURE__*/_createElementVNode(\"div\", {\n  id: \"content-wrapper\",\n  class: \"fixed-header-padding\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"h1\", {\n    class: \"display-4\",\n    id: \"project-management\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, \"Project management\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#project-management\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"lead\" }, [\n    /*#__PURE__*/_createElementVNode(\"p\", null, \"This page contains information about project management tasks. The target audience is senior developers (and above).\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h2\", { id: \"merging-prs\" }, [\n    /*#__PURE__*/_createTextVNode(\"Merging PRs\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#merging-prs\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"ul\", null, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Use the 'squash and merge' option\"),\n      /*#__PURE__*/_createTextVNode(\" unless the situation warrants a different option.\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"For the merge commit\"),\n      /*#__PURE__*/_createTextVNode(\", follow conventions at \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://se-education.org/guides/conventions/github.html\" }, [\n        /*#__PURE__*/_createElementVNode(\"em\", null, \"GitHub conventions\"),\n        /*#__PURE__*/_createTextVNode(\" @SE-EDU\")\n      ]),\n      /*#__PURE__*/_createTextVNode(\".\")\n    ])\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h2\", { id: \"making-a-release-on-github\" }, [\n    /*#__PURE__*/_createTextVNode(\"Making a release on GitHub\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#making-a-release-on-github\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, \"Before making a release, please check the following prerequisites:\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"ul\", null, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Ensure that you have \"),\n      /*#__PURE__*/_createElementVNode(\"strong\", null, [\n        /*#__PURE__*/_createTextVNode(\"JDK \"),\n        /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"11\")\n      ]),\n      /*#__PURE__*/_createTextVNode(\" installed (\"),\n      /*#__PURE__*/_createElementVNode(\"mark\", null, [\n        /*#__PURE__*/_createTextVNode(\"Not other major release versions such as \"),\n        /*#__PURE__*/_createElementVNode(\"strong\", null, [\n          /*#__PURE__*/_createTextVNode(\"JDK \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"12\")\n        ]),\n        /*#__PURE__*/_createTextVNode(\" or \"),\n        /*#__PURE__*/_createElementVNode(\"strong\", null, [\n          /*#__PURE__*/_createTextVNode(\"JDK \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"13\")\n        ])\n      ]),\n      /*#__PURE__*/_createTextVNode(\").\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Ensure that the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"JAVA_HOME\"),\n      /*#__PURE__*/_createTextVNode(\" environment variable is correctly set to your JDK installation directory. You can refer to the \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://docs.oracle.com/cd/E19182-01/821-0917/inst_jdk_javahome_t/index.html\" }, \"JDK Installation Guide\"),\n      /*#__PURE__*/_createTextVNode(\".\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Ensure that you have merged the \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/RepoSense/reposense\" }, \"upstream\"),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n      /*#__PURE__*/_createTextVNode(\" branch into both the local and upstream \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n      /*#__PURE__*/_createTextVNode(\" branch according to the following steps:\\n\"),\n      /*#__PURE__*/_createElementVNode(\"ol\", null, [\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"In your local repository, reset your \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n          /*#__PURE__*/_createTextVNode(\" branch to be exactly the same as the upstream \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n          /*#__PURE__*/_createTextVNode(\" branch.\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Switch to the local \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n          /*#__PURE__*/_createTextVNode(\" branch, and merge the \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n          /*#__PURE__*/_createTextVNode(\" branch into it with \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"git merge master --no-ff\"),\n          /*#__PURE__*/_createTextVNode(\" (no fast forward to keep the commit history for releases).\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Push the local \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n          /*#__PURE__*/_createTextVNode(\" branch directly to the \"),\n          /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/reposense/RepoSense/tree/release\" }, [\n            /*#__PURE__*/_createTextVNode(\"upstream \"),\n            /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n            /*#__PURE__*/_createTextVNode(\" branch\")\n          ]),\n          /*#__PURE__*/_createTextVNode(\" (make sure you have the push access).\")\n        ])\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"To make a release for RepoSense on GitHub, please follow the \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Creating a release\"),\n    /*#__PURE__*/_createTextVNode(\" section in the \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://docs.github.com/en/github/administering-a-repository/managing-releases-in-a-repository\" }, \"GitHub Docs\"),\n    /*#__PURE__*/_createTextVNode(\".\"),\n    /*#__PURE__*/_createElementVNode(\"br\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, \"Take note of the following when making the release according to the above guide:\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"ul\", null, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"When entering a release version number, use semantic versioning with some small tweaks:\\n\"),\n      /*#__PURE__*/_createElementVNode(\"ul\", null, [\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Use \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"MAJOR.MINOR\"),\n          /*#__PURE__*/_createTextVNode(\" as the version number when the release includes new features and/or major changes.\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Use \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"MAJOR.MINOR.PATCH\"),\n          /*#__PURE__*/_createTextVNode(\" as the version number when the release only includes bug fixes and/or minor changes.\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Append \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"rc\"),\n          /*#__PURE__*/_createTextVNode(\" to the version number to indicate that the release is a pre-release that is not ready to be used in production.\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Enter the release title as \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"RepoSense vxxx\"),\n      /*#__PURE__*/_createTextVNode(\" where \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"xxx\"),\n      /*#__PURE__*/_createTextVNode(\" is the version number. Enter the release description by referring to the previous \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/reposense/RepoSense/releases\" }, \"RepoSense releases\"),\n      /*#__PURE__*/_createTextVNode(\".\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Before launching the release, generate the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"RepoSense.jar\"),\n      /*#__PURE__*/_createTextVNode(\" file and attach it to the release.\\n\"),\n      /*#__PURE__*/_createElementVNode(\"ol\", null, [\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Switch to the \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n          /*#__PURE__*/_createTextVNode(\" branch.\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, \"In the terminal, change the directory to the project root directory.\"),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Run \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"gradlew --version\"),\n          /*#__PURE__*/_createTextVNode(\" to check that the JDK version is 11.\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Run \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"gradlew shadowJar\"),\n          /*#__PURE__*/_createTextVNode(\", and the Jar file will be generated at \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"{buildDir}/jar/\"),\n          /*#__PURE__*/_createTextVNode(\".\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"Check that the Jar file is working. You may need to check that the report can be generated from the Jar file both locally and remotely by following the \"),\n          /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/generatingReports.html\" }, \"Generating Reports Guide\"),\n          /*#__PURE__*/_createTextVNode(\".\")\n        ])\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"After making the release, please also remember to deploy the production website using the \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"#deploying-the-production-website\" }, \"deploy guide\"),\n    /*#__PURE__*/_createTextVNode(\".\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h2\", { id: \"deploying-the-production-website\" }, [\n    /*#__PURE__*/_createTextVNode(\"Deploying the production website\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#deploying-the-production-website\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, \"We have two versions of the website:\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"ol\", null, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Production website\"),\n      /*#__PURE__*/_createTextVNode(\" at \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://reposense.org\" }, \"https://reposense.org\"),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"ul\", null, [\n        /*#__PURE__*/_createElementVNode(\"li\", null, \"matches the latest released version\"),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, \"deployed manually after each new release\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Dev website\"),\n      /*#__PURE__*/_createTextVNode(\" at \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://reposense.org/RepoSense\" }, \"https://reposense.org/RepoSense\"),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"ul\", null, [\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"matches the latest \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n          /*#__PURE__*/_createTextVNode(\" branch\")\n        ]),\n        /*#__PURE__*/_createTextVNode(),\n        /*#__PURE__*/_createElementVNode(\"li\", null, [\n          /*#__PURE__*/_createTextVNode(\"deployed automatically by Github Actions whenever the \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n          /*#__PURE__*/_createTextVNode(\" branch is updated\")\n        ])\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, \"The production website differs from the dev website in some ways, e.g.,\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"ul\", null, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"It has a \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"CNAME\"),\n      /*#__PURE__*/_createTextVNode(\" file (to indicate that it is the target destination for the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"reposense.org\"),\n      /*#__PURE__*/_createTextVNode(\" domain name)\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, \"Its DG pages show a warning that it is not the latest version of the DG.\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"MarkBind mainly manages these variations via the \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"site.config\"),\n    /*#__PURE__*/_createTextVNode(\" file. That is why the \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"site.config\"),\n    /*#__PURE__*/_createTextVNode(\" file in the \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n    /*#__PURE__*/_createTextVNode(\" branch is slightly different from the one in the \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n    /*#__PURE__*/_createTextVNode(\" branch.\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, \"After each release, do the following steps to deploy the production website:\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"ol\", null, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Switch to the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n      /*#__PURE__*/_createTextVNode(\" branch\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"cd docs\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"markbind build\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"markbind deploy\"),\n      /*#__PURE__*/_createTextVNode(\" (make sure you have the push access to \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/reposense/reposense.github.io\" }, \"https://github.com/reposense/reposense.github.io\"),\n      /*#__PURE__*/_createTextVNode(\")\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"After a few minutes, check \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://reposense.org\" }, \"https://reposense.org\"),\n      /*#__PURE__*/_createTextVNode(\" to ensure it has been updated as intended.\")\n    ])\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h2\", { id: \"hot-patching-after-the-release\" }, [\n    /*#__PURE__*/_createTextVNode(\"Hot patching after the release\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#hot-patching-after-the-release\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, \"If critical bugs are found in the release, take the following steps to hot patch it:\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"ol\", null, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Switch to the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n      /*#__PURE__*/_createTextVNode(\" branch.\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Implement the fixes, commit them, and create a pull request from your forked \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n      /*#__PURE__*/_createTextVNode(\" branch to the upstream \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n      /*#__PURE__*/_createTextVNode(\" branch.\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"After merging, release a new version of RepoSense with the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n      /*#__PURE__*/_createTextVNode(\" branch according to the above guide.\")\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createTextVNode(\"Merge the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"release\"),\n      /*#__PURE__*/_createTextVNode(\" branch back into the \"),\n      /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n      /*#__PURE__*/_createTextVNode(\" branch by creating a separate pull request.\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_24 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_25 = /*#__PURE__*/_createElementVNode(\"a\", {\n  class: \"nav-link py-1\",\n  href: \"#project-management\"\n}, \"Project management‎\", -1 /* HOISTED */)\nconst _hoisted_26 = /*#__PURE__*/_createElementVNode(\"nav\", { class: \"nav nav-pills flex-column my-0 nested no-flex-wrap\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#merging-prs\"\n  }, \"Merging PRs‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#making-a-release-on-github\"\n  }, \"Making a release on GitHub‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#deploying-the-production-website\"\n  }, \"Deploying the production website‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#hot-patching-after-the-release\"\n  }, \"Hot patching after the release‎\")\n], -1 /* HOISTED */)\nconst _hoisted_27 = /*#__PURE__*/_createElementVNode(\"footer\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"text-center\" }, [\n    /*#__PURE__*/_createElementVNode(\"small\", null, [\n      /*#__PURE__*/_createTextVNode(\"[\"),\n      /*#__PURE__*/_createElementVNode(\"span\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"Powered by\")\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"https://markbind.org/favicon.ico\",\n        width: \"30\"\n      }),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://markbind.org/\" }, \"MarkBind 6.0.0\"),\n      /*#__PURE__*/_createTextVNode(\", generated on Mon, 14 Apr 2025, 6:24:00 UTC]\")\n    ])\n  ])\n], -1 /* HOISTED */)\n\nreturn function render(_ctx, _cache) {\n  const _component_searchbar = _resolveComponent(\"searchbar\")\n  const _component_navbar = _resolveComponent(\"navbar\")\n  const _component_overlay_source = _resolveComponent(\"overlay-source\")\n  const _component_site_nav = _resolveComponent(\"site-nav\")\n\n  return (_openBlock(), _createElementBlock(_Fragment, null, [\n    _createElementVNode(\"header\", _hoisted_1, [\n      _createVNode(_component_navbar, { type: \"dark\" }, {\n        brand: _withCtx(() => [\n          _hoisted_2\n        ]),\n        right: _withCtx(() => [\n          _createElementVNode(\"li\", null, [\n            _createElementVNode(\"form\", _hoisted_11, [\n              _createVNode(_component_searchbar, {\n                data: _ctx.searchData,\n                placeholder: \"Search\",\n                \"on-hit\": _ctx.searchCallback,\n                \"menu-align-right\": \"\"\n              }, null, 8 /* PROPS */, [\"data\", \"on-hit\"])\n            ])\n          ])\n        ]),\n        default: _withCtx(() => [\n          _createTextVNode(),\n          _hoisted_3,\n          _createTextVNode(),\n          _hoisted_4,\n          _createTextVNode(),\n          _hoisted_5,\n          _createTextVNode(),\n          _hoisted_6,\n          _createTextVNode(),\n          _hoisted_7,\n          _createTextVNode(),\n          _hoisted_8,\n          _createTextVNode(),\n          _hoisted_9,\n          _createTextVNode(),\n          _hoisted_10,\n          _createTextVNode()\n        ]),\n        _: 1 /* STABLE */\n      })\n    ]),\n    _createTextVNode(),\n    _createElementVNode(\"div\", _hoisted_12, [\n      _createVNode(_component_overlay_source, {\n        id: \"site-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"site-nav\"\n      }, {\n        default: _withCtx(() => [\n          _hoisted_13,\n          _createTextVNode(),\n          _createElementVNode(\"div\", _hoisted_14, [\n            _createElementVNode(\"div\", null, [\n              _createVNode(_component_site_nav, null, {\n                default: _withCtx(() => [\n                  _createVNode(_component_overlay_source, {\n                    class: \"site-nav-list site-nav-list-root\",\n                    \"tag-name\": \"ul\",\n                    to: \"mb-site-nav\"\n                  }, {\n                    default: _withCtx(() => [\n                      _hoisted_15,\n                      _createTextVNode(),\n                      _hoisted_16,\n                      _createTextVNode(),\n                      _hoisted_17,\n                      _createTextVNode(),\n                      _hoisted_18,\n                      _createTextVNode(),\n                      _hoisted_19,\n                      _createTextVNode(),\n                      _hoisted_20,\n                      _createTextVNode(),\n                      _hoisted_21,\n                      _createTextVNode(),\n                      _hoisted_22\n                    ]),\n                    _: 1 /* STABLE */\n                  })\n                ]),\n                _: 1 /* STABLE */\n              })\n            ])\n          ])\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _hoisted_23,\n      _createTextVNode(),\n      _createVNode(_component_overlay_source, {\n        id: \"page-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"page-nav\"\n      }, {\n        default: _withCtx(() => [\n          _createElementVNode(\"div\", _hoisted_24, [\n            _createVNode(_component_overlay_source, {\n              id: \"mb-page-nav\",\n              \"tag-name\": \"nav\",\n              to: \"mb-page-nav\",\n              class: \"nav nav-pills flex-column my-0 small no-flex-wrap\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_25,\n                _createTextVNode(),\n                _hoisted_26\n              ]),\n              _: 1 /* STABLE */\n            })\n          ])\n        ]),\n        _: 1 /* STABLE */\n      })\n    ]),\n    _createTextVNode(),\n    _hoisted_27\n  ], 64 /* STABLE_FRAGMENT */))\n}");
    var render = renderFn();
  