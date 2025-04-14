
    const renderFn = new Function("const { createElementVNode: _createElementVNode, createTextVNode: _createTextVNode, resolveComponent: _resolveComponent, createVNode: _createVNode, withCtx: _withCtx, Fragment: _Fragment, openBlock: _openBlock, createElementBlock: _createElementBlock } = Vue\n\nconst _hoisted_1 = { fixed: \"\" }\nconst _hoisted_2 = /*#__PURE__*/_createElementVNode(\"a\", {\n  href: \"/index.html\",\n  title: \"Home\",\n  class: \"navbar-brand\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"img\", {\n    width: \"30px\",\n    src: \"/favicon.ico\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_3 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/index.html\",\n    class: \"nav-link\"\n  }, \"HOME\")\n], -1 /* HOISTED */)\nconst _hoisted_4 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/showcase.html\",\n    class: \"nav-link\"\n  }, \"SHOWCASE\")\n], -1 /* HOISTED */)\nconst _hoisted_5 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/ug/index.html\",\n    class: \"nav-link\"\n  }, \"USER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_6 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/dg/index.html\",\n    class: \"nav-link\"\n  }, \"DEVELOPER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_7 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/about.html\",\n    class: \"nav-link\"\n  }, \"ABOUT\")\n], -1 /* HOISTED */)\nconst _hoisted_8 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/contact.html\",\n    class: \"nav-link\"\n  }, \"CONTACT\")\n], -1 /* HOISTED */)\nconst _hoisted_9 = /*#__PURE__*/_createElementVNode(\"li\", { tags: \"dev\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://reposense.org\",\n    class: \"nav-link\",\n    target: \"_blank\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fas fa-external-link-alt\"\n      }),\n      /*#__PURE__*/_createTextVNode(\" PRODUCTION SITE\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_10 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://github.com/RepoSense/reposense\",\n    target: \"_blank\",\n    class: \"nav-link\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fab fa-github\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_11 = { class: \"navbar-form\" }\nconst _hoisted_12 = /*#__PURE__*/_createElementVNode(\"div\", {\n  tags: \"dev\",\n  class: \"text-center bg-warning p-2\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"You are looking at the user documentation for the most recent \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n    /*#__PURE__*/_createTextVNode(\" branch of RepoSense (not released to the public yet). \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, [\n      /*#__PURE__*/_createTextVNode(\"The documentation for the latest public release is \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://reposense.org/ug\" }, \"here\"),\n      /*#__PURE__*/_createTextVNode(\".\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_13 = { id: \"flex-body\" }\nconst _hoisted_14 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-top\" }, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"font-weight-bold mb-2\",\n    style: {\"font-size\":\"1.25rem\"}\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"USER GUIDE\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_15 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_16 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/index.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Overview\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_17 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/generatingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Generating reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_18 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/usingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Using reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_19 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/customizingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Customizing reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_20 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/sharingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Sharing reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_21 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Appendices\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-dropdown-btn-container\" }, [\n      /*#__PURE__*/_createElementVNode(\"i\", {\n        class: \"site-nav-dropdown-btn-icon site-nav-rotate-icon\",\n        onclick: \"handleSiteNavClick(this.parentNode.parentNode, false); event.stopPropagation();\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"span\", {\n          class: \"glyphicon glyphicon-menu-down\",\n          \"aria-hidden\": \"true\"\n        })\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createElementVNode(\"ul\", { class: \"site-nav-dropdown-container site-nav-dropdown-container-open site-nav-list\" }, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/cli.html\" }, \"CLI syntax reference\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/reportConfig.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Getting started with \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"report-config.yaml\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/configFiles.html\" }, \"Config files format\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/authorConfigSyntax.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Advanced syntax: \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"author-config.csv\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/usingAuthorTags.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Using \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"@@author\"),\n          /*#__PURE__*/_createTextVNode(\" tags\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/withNetlify.html\" }, \"RepoSense with Netlify\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/withGithubActions.html\" }, \"RepoSense with GitHub Actions\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/runSh.html\" }, [\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"run.sh\"),\n          /*#__PURE__*/_createTextVNode(\" format\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/faq.html\" }, \"FAQ\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/troubleshooting.html\" }, \"Troubleshooting\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_22 = /*#__PURE__*/_createElementVNode(\"div\", {\n  id: \"content-wrapper\",\n  class: \"fixed-header-padding\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"h1\", {\n    class: \"display-4\",\n    id: \"appendix-troubleshooting\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, \"Appendix: Troubleshooting\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#appendix-troubleshooting\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h3\", { id: \"contributions-missing-in-the-ramp-chart-but-appear-in-the-contribution-bar-and-code-panel\" }, [\n    /*#__PURE__*/_createTextVNode(\"Contributions missing in the ramp chart (but appear in the contribution bar and code panel)\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#contributions-missing-in-the-ramp-chart-but-appear-in-the-contribution-bar-and-code-panel\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"This is probably a case of giving an incorrect author name alias (or Git ID) in your \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/configFiles.html#author-config-csv\" }, \"author-config file\"),\n    /*#__PURE__*/_createTextVNode(\".\"),\n    /*#__PURE__*/_createElementVNode(\"br\"),\n    /*#__PURE__*/_createTextVNode(\"\\nPlease refer to \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/configFiles.html#a-note-about-git-author-name\" }, \"A Note About Git Author Name\"),\n    /*#__PURE__*/_createTextVNode(\" above on how to find out the correct author name you are using and how to change it.\"),\n    /*#__PURE__*/_createElementVNode(\"br\"),\n    /*#__PURE__*/_createTextVNode(\"\\nAlso, ensure that you have added all author name aliases you may be using (if you are using multiple computers or have previously changed your author name).\"),\n    /*#__PURE__*/_createElementVNode(\"br\"),\n    /*#__PURE__*/_createTextVNode(\"\\nAlternatively, you may choose to configure \"),\n    /*#__PURE__*/_createElementVNode(\"em\", null, \"RepoSense\"),\n    /*#__PURE__*/_createTextVNode(\" to track the email associated with your local Git config or remote Git host email in a \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/configFiles.html#config-json-standalone-config-file\" }, \"standalone config file\"),\n    /*#__PURE__*/_createTextVNode(\" or \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/configFiles.html#author-config-csv\" }, \"author-config file\"),\n    /*#__PURE__*/_createTextVNode(\", which is more accurate compared to author name aliases. For GitHub, the associated email you are using can be found in your \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/settings/emails\" }, \"GitHub settings\"),\n    /*#__PURE__*/_createTextVNode(\".\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h3\", { id: \"contribution-bar-and-code-panel-is-empty-despite-a-non-empty-ramp-chart\" }, [\n    /*#__PURE__*/_createTextVNode(\"Contribution bar and code panel is empty (despite a non-empty ramp chart)\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#contribution-bar-and-code-panel-is-empty-despite-a-non-empty-ramp-chart\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"The contribution bar and code panel records the lines you have authored to the \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"latest\"),\n    /*#__PURE__*/_createTextVNode(\" commit of the repository and branch you are analyzing.  As such, it is possible that while you have lots of committed contributions, your final authorship contribution is low. This happens if you have only deleted lines or someone else has overwritten your code and taken authorship for it (currently, RepoSense does not have the functionality to track overwritten lines).\"),\n    /*#__PURE__*/_createElementVNode(\"br\"),\n    /*#__PURE__*/_createTextVNode(\"\\nIt is also possible that another user has overridden the authorship of your lines using the \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/usingAuthorTags.html#appendix-using-author-tags\" }, \"@@author tags\"),\n    /*#__PURE__*/_createTextVNode(\".\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h3\", { id: \"reposense-is-not-using-the-standalone-config-file-in-my-local-repository\" }, [\n    /*#__PURE__*/_createTextVNode(\"RepoSense is not using the standalone config file in my local repository\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#reposense-is-not-using-the-standalone-config-file-in-my-local-repository\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Ensure that you have committed the changes to your standalone config file first before running the analysis, as \"),\n    /*#__PURE__*/_createElementVNode(\"em\", null, \"RepoSense\"),\n    /*#__PURE__*/_createTextVNode(\" is unable to detect uncommitted changes to your local repository.\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h3\", { id: \"reposense-fails-on-windows-but-works-on-linux-mac-os\" }, [\n    /*#__PURE__*/_createTextVNode(\"RepoSense fails on Windows (but works on Linux/Mac OS)\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#reposense-fails-on-windows-but-works-on-linux-mac-os\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Possibly, you may have some file names with \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://docs.microsoft.com/en-us/windows/desktop/FileIO/naming-a-file#naming-conventions\" }, \"special characters\"),\n    /*#__PURE__*/_createTextVNode(\" in them, which is disallowed in Windows OS. As such, \"),\n    /*#__PURE__*/_createElementVNode(\"em\", null, \"RepoSense\"),\n    /*#__PURE__*/_createTextVNode(\" is unable to clone your repository fully, thus failing the analysis.\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h3\", { id: \"some-file-types-are-not-shown-in-the-file-type-filter-even-if-i-have-included-them-in-the-file-formats-when-generating-the-report\" }, [\n    /*#__PURE__*/_createTextVNode(\"Some file types are not shown in the file type filter even if I have included them in the file formats when generating the report\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#some-file-types-are-not-shown-in-the-file-type-filter-even-if-i-have-included-them-in-the-file-formats-when-generating-the-report\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"The files of these types may be \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://en.wikipedia.org/wiki/Binary_file\" }, \"binary files\"),\n    /*#__PURE__*/_createTextVNode(\". \"),\n    /*#__PURE__*/_createElementVNode(\"em\", null, \"RepoSense\"),\n    /*#__PURE__*/_createTextVNode(\" will group binary files under one single file type \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"binary\"),\n    /*#__PURE__*/_createTextVNode(\". Common binary files include images (\"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \".jpg\"),\n    /*#__PURE__*/_createTextVNode(\", \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \".png\"),\n    /*#__PURE__*/_createTextVNode(\"), applications (\"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \".exe\"),\n    /*#__PURE__*/_createTextVNode(\"), zip files (\"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \".zip\"),\n    /*#__PURE__*/_createTextVNode(\", \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \".rar\"),\n    /*#__PURE__*/_createTextVNode(\") and certain document types (\"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \".docx\"),\n    /*#__PURE__*/_createTextVNode(\", \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \".pptx\"),\n    /*#__PURE__*/_createTextVNode(\").\")\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"h3\", { id: \"reposense-doesn-t-work-on-the-browser\" }, [\n    /*#__PURE__*/_createTextVNode(\"RepoSense doesn't work on the browser\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#reposense-doesn-t-work-on-the-browser\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"RepoSense uses ES6, a version of JavaScript widely supported by \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://caniuse.com/?search=es6\" }, \"most browsers\"),\n    /*#__PURE__*/_createTextVNode(\". Please use RepoSense with a browser that supports ES6.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_23 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_24 = /*#__PURE__*/_createElementVNode(\"a\", {\n  class: \"nav-link py-1\",\n  href: \"#appendix-troubleshooting\"\n}, \"Appendix: Troubleshooting‎\", -1 /* HOISTED */)\nconst _hoisted_25 = /*#__PURE__*/_createElementVNode(\"nav\", { class: \"nav nav-pills flex-column my-0 nested no-flex-wrap\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#contributions-missing-in-the-ramp-chart-but-appear-in-the-contribution-bar-and-code-panel\"\n  }, \"Contributions missing in the ramp chart (but appear in the contribution bar and code panel)‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#contribution-bar-and-code-panel-is-empty-despite-a-non-empty-ramp-chart\"\n  }, \"Contribution bar and code panel is empty (despite a non-empty ramp chart)‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#reposense-is-not-using-the-standalone-config-file-in-my-local-repository\"\n  }, \"RepoSense is not using the standalone config file in my local repository‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#reposense-fails-on-windows-but-works-on-linux-mac-os\"\n  }, \"RepoSense fails on Windows (but works on Linux/Mac OS)‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#some-file-types-are-not-shown-in-the-file-type-filter-even-if-i-have-included-them-in-the-file-formats-when-generating-the-report\"\n  }, \"Some file types are not shown in the file type filter even if I have included them in the file formats when generating the report‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#reposense-doesn-t-work-on-the-browser\"\n  }, \"RepoSense doesn't work on the browser‎\")\n], -1 /* HOISTED */)\nconst _hoisted_26 = /*#__PURE__*/_createElementVNode(\"footer\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"text-center\" }, [\n    /*#__PURE__*/_createElementVNode(\"small\", null, [\n      /*#__PURE__*/_createTextVNode(\"[\"),\n      /*#__PURE__*/_createElementVNode(\"span\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"Powered by\")\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"https://markbind.org/favicon.ico\",\n        width: \"30\"\n      }),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://markbind.org/\" }, \"MarkBind 6.0.0\"),\n      /*#__PURE__*/_createTextVNode(\", generated on Mon, 14 Apr 2025, 7:33:51 UTC]\")\n    ])\n  ])\n], -1 /* HOISTED */)\n\nreturn function render(_ctx, _cache) {\n  const _component_searchbar = _resolveComponent(\"searchbar\")\n  const _component_navbar = _resolveComponent(\"navbar\")\n  const _component_overlay_source = _resolveComponent(\"overlay-source\")\n  const _component_site_nav = _resolveComponent(\"site-nav\")\n\n  return (_openBlock(), _createElementBlock(_Fragment, null, [\n    _createElementVNode(\"header\", _hoisted_1, [\n      _createVNode(_component_navbar, { type: \"dark\" }, {\n        brand: _withCtx(() => [\n          _hoisted_2\n        ]),\n        right: _withCtx(() => [\n          _createElementVNode(\"li\", null, [\n            _createElementVNode(\"form\", _hoisted_11, [\n              _createVNode(_component_searchbar, {\n                data: _ctx.searchData,\n                placeholder: \"Search\",\n                \"on-hit\": _ctx.searchCallback,\n                \"menu-align-right\": \"\"\n              }, null, 8 /* PROPS */, [\"data\", \"on-hit\"])\n            ])\n          ])\n        ]),\n        default: _withCtx(() => [\n          _createTextVNode(),\n          _hoisted_3,\n          _createTextVNode(),\n          _hoisted_4,\n          _createTextVNode(),\n          _hoisted_5,\n          _createTextVNode(),\n          _hoisted_6,\n          _createTextVNode(),\n          _hoisted_7,\n          _createTextVNode(),\n          _hoisted_8,\n          _createTextVNode(),\n          _hoisted_9,\n          _createTextVNode(),\n          _hoisted_10,\n          _createTextVNode()\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _hoisted_12\n    ]),\n    _createTextVNode(),\n    _createElementVNode(\"div\", _hoisted_13, [\n      _createVNode(_component_overlay_source, {\n        id: \"site-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"site-nav\"\n      }, {\n        default: _withCtx(() => [\n          _hoisted_14,\n          _createTextVNode(),\n          _createElementVNode(\"div\", _hoisted_15, [\n            _createElementVNode(\"div\", null, [\n              _createVNode(_component_site_nav, null, {\n                default: _withCtx(() => [\n                  _createVNode(_component_overlay_source, {\n                    class: \"site-nav-list site-nav-list-root\",\n                    \"tag-name\": \"ul\",\n                    to: \"mb-site-nav\"\n                  }, {\n                    default: _withCtx(() => [\n                      _hoisted_16,\n                      _createTextVNode(),\n                      _hoisted_17,\n                      _createTextVNode(),\n                      _hoisted_18,\n                      _createTextVNode(),\n                      _hoisted_19,\n                      _createTextVNode(),\n                      _hoisted_20,\n                      _createTextVNode(),\n                      _hoisted_21\n                    ]),\n                    _: 1 /* STABLE */\n                  })\n                ]),\n                _: 1 /* STABLE */\n              })\n            ])\n          ])\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _hoisted_22,\n      _createTextVNode(),\n      _createVNode(_component_overlay_source, {\n        id: \"page-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"page-nav\"\n      }, {\n        default: _withCtx(() => [\n          _createElementVNode(\"div\", _hoisted_23, [\n            _createVNode(_component_overlay_source, {\n              id: \"mb-page-nav\",\n              \"tag-name\": \"nav\",\n              to: \"mb-page-nav\",\n              class: \"nav nav-pills flex-column my-0 small no-flex-wrap\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_24,\n                _createTextVNode(),\n                _hoisted_25\n              ]),\n              _: 1 /* STABLE */\n            })\n          ])\n        ]),\n        _: 1 /* STABLE */\n      })\n    ]),\n    _createTextVNode(),\n    _hoisted_26\n  ], 64 /* STABLE_FRAGMENT */))\n}");
    var render = renderFn();
  