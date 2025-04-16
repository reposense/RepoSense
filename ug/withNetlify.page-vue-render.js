
    const renderFn = new Function("const { createElementVNode: _createElementVNode, createTextVNode: _createTextVNode, resolveComponent: _resolveComponent, createVNode: _createVNode, withCtx: _withCtx, createStaticVNode: _createStaticVNode, Fragment: _Fragment, openBlock: _openBlock, createElementBlock: _createElementBlock } = Vue\n\nconst _hoisted_1 = { fixed: \"\" }\nconst _hoisted_2 = /*#__PURE__*/_createElementVNode(\"a\", {\n  href: \"/index.html\",\n  title: \"Home\",\n  class: \"navbar-brand\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"img\", {\n    width: \"30px\",\n    src: \"/favicon.ico\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_3 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/index.html\",\n    class: \"nav-link\"\n  }, \"HOME\")\n], -1 /* HOISTED */)\nconst _hoisted_4 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/showcase.html\",\n    class: \"nav-link\"\n  }, \"SHOWCASE\")\n], -1 /* HOISTED */)\nconst _hoisted_5 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/ug/index.html\",\n    class: \"nav-link\"\n  }, \"USER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_6 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/dg/index.html\",\n    class: \"nav-link\"\n  }, \"DEVELOPER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_7 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/about.html\",\n    class: \"nav-link\"\n  }, \"ABOUT\")\n], -1 /* HOISTED */)\nconst _hoisted_8 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/contact.html\",\n    class: \"nav-link\"\n  }, \"CONTACT\")\n], -1 /* HOISTED */)\nconst _hoisted_9 = /*#__PURE__*/_createElementVNode(\"li\", { tags: \"dev\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://reposense.org\",\n    class: \"nav-link\",\n    target: \"_blank\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fas fa-external-link-alt\"\n      }),\n      /*#__PURE__*/_createTextVNode(\" PRODUCTION SITE\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_10 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://github.com/RepoSense/reposense\",\n    target: \"_blank\",\n    class: \"nav-link\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fab fa-github\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_11 = { class: \"navbar-form\" }\nconst _hoisted_12 = /*#__PURE__*/_createElementVNode(\"div\", {\n  tags: \"dev\",\n  class: \"text-center bg-warning p-2\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"You are looking at the user documentation for the most recent \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n    /*#__PURE__*/_createTextVNode(\" branch of RepoSense (not released to the public yet). \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, [\n      /*#__PURE__*/_createTextVNode(\"The documentation for the latest public release is \"),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://reposense.org/ug\" }, \"here\"),\n      /*#__PURE__*/_createTextVNode(\".\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_13 = { id: \"flex-body\" }\nconst _hoisted_14 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-top\" }, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"font-weight-bold mb-2\",\n    style: {\"font-size\":\"1.25rem\"}\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"USER GUIDE\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_15 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_16 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/index.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Overview\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_17 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/generatingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Generating reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_18 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/usingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Using reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_19 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/customizingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Customizing reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_20 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/sharingReports.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Sharing reports\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_21 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Appendices\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-dropdown-btn-container\" }, [\n      /*#__PURE__*/_createElementVNode(\"i\", {\n        class: \"site-nav-dropdown-btn-icon site-nav-rotate-icon\",\n        onclick: \"handleSiteNavClick(this.parentNode.parentNode, false); event.stopPropagation();\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"span\", {\n          class: \"glyphicon glyphicon-menu-down\",\n          \"aria-hidden\": \"true\"\n        })\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createElementVNode(\"ul\", { class: \"site-nav-dropdown-container site-nav-dropdown-container-open site-nav-list\" }, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/cli.html\" }, \"CLI syntax reference\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/reportConfig.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Getting started with \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"report-config.yaml\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/configFiles.html\" }, \"Config files format\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/authorConfigSyntax.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Advanced syntax: \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"author-config.csv\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/usingAuthorTags.html\" }, [\n          /*#__PURE__*/_createTextVNode(\"Using \"),\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"@@author\"),\n          /*#__PURE__*/_createTextVNode(\" tags\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/withNetlify.html\" }, \"RepoSense with Netlify\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/withGithubActions.html\" }, \"RepoSense with GitHub Actions\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/runSh.html\" }, [\n          /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"run.sh\"),\n          /*#__PURE__*/_createTextVNode(\" format\")\n        ])\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/faq.html\" }, \"FAQ\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/ug/troubleshooting.html\" }, \"Troubleshooting\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_22 = {\n  id: \"content-wrapper\",\n  class: \"fixed-header-padding\"\n}\nconst _hoisted_23 = /*#__PURE__*/_createElementVNode(\"h1\", {\n  class: \"display-4\",\n  id: \"appendix-reposense-with-netlify\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"span\", null, \"Appendix: RepoSense with Netlify\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#appendix-reposense-with-netlify\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_24 = { id: \"section-setting-up\" }\nconst _hoisted_25 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createTextVNode(\"Note that Netlify has a low limit for free tier users (only 300 \"),\n  /*#__PURE__*/_createElementVNode(\"em\", null, \"build minutes\"),\n  /*#__PURE__*/_createTextVNode(\" per month as at June 2020 -- a single report generation can take 2-3 build minutes, longer if your report includes many/big repositories). Due to this, we will not be supporting this method going forward.\")\n], -1 /* HOISTED */)\nconst _hoisted_26 = /*#__PURE__*/_createElementVNode(\"h2\", { id: \"setting-up\" }, [\n  /*#__PURE__*/_createTextVNode(\"Setting up\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#setting-up\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_27 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createElementVNode(\"span\", { class: \"badge bg-dark bigger-level0\" }, \"Step 1\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, [\n    /*#__PURE__*/_createTextVNode(\"Fork the \"),\n    /*#__PURE__*/_createElementVNode(\"em\", null, \"publish-RepoSense\"),\n    /*#__PURE__*/_createTextVNode(\" repository\")\n  ]),\n  /*#__PURE__*/_createTextVNode(\" using this \"),\n  /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/RepoSense/publish-RepoSense/fork\" }, \"link\"),\n  /*#__PURE__*/_createTextVNode(\". Optionally, you can rename the fork to match your RepoSense report e.g., \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"project-code-dashboard\"),\n  /*#__PURE__*/_createTextVNode(\".\")\n], -1 /* HOISTED */)\nconst _hoisted_28 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createElementVNode(\"span\", { class: \"badge bg-dark bigger-level0\" }, \"Step 2\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"Set up Netlify for your fork\"),\n  /*#__PURE__*/_createTextVNode(\" as described in this \"),\n  /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://www.netlify.com/blog/2016/09/29/a-step-by-step-guide-deploying-on-netlify/\" }, \"guide\"),\n  /*#__PURE__*/_createTextVNode(\".\"),\n  /*#__PURE__*/_createElementVNode(\"br\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"mark\", null, [\n    /*#__PURE__*/_createTextVNode(\"You don't need to follow \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Step 5: Configure Your Settings\"),\n    /*#__PURE__*/_createTextVNode(\" as we have provided you with a self-configured build script\")\n  ]),\n  /*#__PURE__*/_createTextVNode(\":\")\n], -1 /* HOISTED */)\nconst _hoisted_29 = /*#__PURE__*/_createElementVNode(\"ul\", null, [\n  /*#__PURE__*/_createElementVNode(\"li\", null, [\n    /*#__PURE__*/_createTextVNode(\"Ensure that the following \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"netlify.toml\"),\n    /*#__PURE__*/_createTextVNode(\" is in your main repo:\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_30 = /*#__PURE__*/_createStaticVNode(\"<pre><code class=\\\"hljs toml\\\"><span><span class=\\\"hljs-section\\\">[build]</span>\\n</span><span> <span class=\\\"hljs-attr\\\">command</span> = <span class=\\\"hljs-string\\\">&quot;&quot;&quot;</span>\\n</span><span><span class=\\\"hljs-string\\\"> wget https://download.java.net/java/ga/jdk11/openjdk-11_linux-x64_bin.tar.gz &amp;&amp;</span>\\n</span><span><span class=\\\"hljs-string\\\"> tar -xvzf openjdk-11_linux-x64_bin.tar.gz &amp;&amp;</span>\\n</span><span><span class=\\\"hljs-string\\\"> mv jdk-11 ~/openjdk11 &amp;&amp;</span>\\n</span><span><span class=\\\"hljs-string\\\"> export JAVA_HOME=$HOME/openjdk11 &amp;&amp;</span>\\n</span><span><span class=\\\"hljs-string\\\"> export PATH=$JAVA_HOME/bin:$PATH &amp;&amp;</span>\\n</span><span><span class=\\\"hljs-string\\\"> pip install requests &amp;&amp; ./run.sh</span>\\n</span><span><span class=\\\"hljs-string\\\"> &quot;&quot;&quot;</span>\\n</span><span>\\n</span><span> <span class=\\\"hljs-attr\\\">publish</span> = <span class=\\\"hljs-string\\\">&quot;./reposense-report&quot;</span>\\n</span><span> <span class=\\\"hljs-attr\\\">base</span> = <span class=\\\"hljs-string\\\">&quot;./&quot;</span>\\n</span></code></pre><p>After Netlify finishes building the site, you should be able to see a dummy report at the URL of your Netlify site.</p>\", 2)\nconst _hoisted_32 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createElementVNode(\"span\", { class: \"badge bg-dark bigger-level0\" }, \"Step 3\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"Generate the report you want\"),\n  /*#__PURE__*/_createTextVNode(\" by updating the settings in your fork.\")\n], -1 /* HOISTED */)\nconst _hoisted_33 = /*#__PURE__*/_createStaticVNode(\"<ol><li>Go to the <code class=\\\"hljs inline no-lang\\\">run.sh</code> file of your fork (on GitHub).</li> <li>Update the last line (i.e., the command for running RepoSense) to match the report you want to generate:<br> <code class=\\\"hljs inline no-lang\\\">java -jar RepoSense.jar --repos FULL_REPO_URL</code> (assuming you want to generate a default report for just one repo)<br>\\ne.g., <code class=\\\"hljs inline no-lang\\\">java -jar RepoSense.jar --repos https://github.com/reposense/RepoSense.git</code> (<mark>note the .git at the end of the repo URL</mark>)</li> <li>Commit the file. This will trigger Netlify to rebuild the report.</li> <li>Go to the URL of your Netlify site to see the updated RepoSense report (it might take about 2-5 minutes for Netlify to generate the report).</li></ol>\", 1)\nconst _hoisted_34 = /*#__PURE__*/_createElementVNode(\"div\", { id: \"section-pr-previews\" }, [\n  /*#__PURE__*/_createElementVNode(\"h2\", { id: \"pr-previews\" }, [\n    /*#__PURE__*/_createTextVNode(\"PR previews\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      class: \"fa fa-anchor\",\n      href: \"#pr-previews\",\n      onclick: \"event.stopPropagation()\"\n    })\n  ]),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"After setting up Netlify for your repo containing RepoSense settings, when a PR comes in to that repo to update any setting, you can scroll down the PR page and in \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"All checks have passed\"),\n    /*#__PURE__*/_createTextVNode(\", click on the \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Details\"),\n    /*#__PURE__*/_createTextVNode(\" beside \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"deploy/netlify — Deploy preview ready!\"),\n    /*#__PURE__*/_createTextVNode(\" to see a preview of the report as per the changes in the PR.\\n\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      href: \"/RepoSense/images/publishingguide-netlifypreview.png\",\n      target: \"_self\"\n    }, [\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"/RepoSense/images/publishingguide-netlifypreview.png\",\n        alt: \"Netlify Preview\",\n        class: \"img-fluid\",\n        title: \"Netlify Preview\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_35 = /*#__PURE__*/_createElementVNode(\"h2\", { id: \"updating-the-report\" }, [\n  /*#__PURE__*/_createTextVNode(\"Updating the report\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#updating-the-report\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_36 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"Manual:\"),\n  /*#__PURE__*/_createTextVNode(\" Netlify UI has a way for you to trigger a build, using which you can cause the report to be updated.\")\n], -1 /* HOISTED */)\nconst _hoisted_37 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"Automated:\"),\n  /*#__PURE__*/_createTextVNode(\" Netlify's can be set up to update the report whenever a target repo of your report is updated, provided you are able to update the target repos in a certain way.\")\n], -1 /* HOISTED */)\nconst _hoisted_38 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Click on \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Settings\"),\n    /*#__PURE__*/_createTextVNode(\" in the top, choose \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Build & deploy\"),\n    /*#__PURE__*/_createTextVNode(\" from the left panel and scroll to \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Build hooks\"),\n    /*#__PURE__*/_createTextVNode(\".\\n\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      href: \"/RepoSense/images/using-netlify-build-hooks.png\",\n      target: \"_self\"\n    }, [\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"/RepoSense/images/using-netlify-build-hooks.png\",\n        alt: \"Build hooks\",\n        class: \"img-fluid\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_39 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Click \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Add build hook\"),\n    /*#__PURE__*/_createTextVNode(\", give your webhook a name, and choose the \"),\n    /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"master\"),\n    /*#__PURE__*/_createTextVNode(\" branch to build. A Netlify URL will be generated.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_40 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Go to your target repository (the repository you want to analyze) and click on \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Settings\"),\n    /*#__PURE__*/_createTextVNode(\".\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_41 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Select \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Webhooks\"),\n    /*#__PURE__*/_createTextVNode(\" on left panel and click on \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Add webhook\"),\n    /*#__PURE__*/_createTextVNode(\".\\n\"),\n    /*#__PURE__*/_createElementVNode(\"a\", {\n      href: \"/RepoSense/images/using-netlify-add-hook.png\",\n      target: \"_self\"\n    }, [\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"/RepoSense/images/using-netlify-add-hook.png\",\n        alt: \"Add webhook\",\n        class: \"img-fluid\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_42 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createTextVNode(\"Copy the Netlify URL and paste it in the URL form field.\\n\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/images/using-netlify-url.png\",\n    target: \"_self\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"img\", {\n      src: \"/RepoSense/images/using-netlify-url.png\",\n      alt: \"Webhook url\",\n      class: \"img-fluid\"\n    })\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_43 = /*#__PURE__*/_createElementVNode(\"p\", null, \"Note: Although the build url is not that secretive, it should be kept safe to prevent any misuse.\", -1 /* HOISTED */)\nconst _hoisted_44 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Select \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"application.json\"),\n    /*#__PURE__*/_createTextVNode(\" as content type.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_45 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Select \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Let me select individual events\"),\n    /*#__PURE__*/_createTextVNode(\" and based on your requirements check the checkboxes.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_46 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Leave the \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Active\"),\n    /*#__PURE__*/_createTextVNode(\" checkbox checked.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_47 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createTextVNode(\"Click on \"),\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Add webhook\"),\n    /*#__PURE__*/_createTextVNode(\" to save the webhook and add it.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_48 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_49 = /*#__PURE__*/_createElementVNode(\"a\", {\n  class: \"nav-link py-1\",\n  href: \"#appendix-reposense-with-netlify\"\n}, \"Appendix: RepoSense with Netlify‎\", -1 /* HOISTED */)\nconst _hoisted_50 = /*#__PURE__*/_createElementVNode(\"nav\", { class: \"nav nav-pills flex-column my-0 nested no-flex-wrap\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#setting-up\"\n  }, \"Setting up‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#pr-previews\"\n  }, \"PR previews‎\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"nav-link py-1\",\n    href: \"#updating-the-report\"\n  }, \"Updating the report‎\")\n], -1 /* HOISTED */)\nconst _hoisted_51 = /*#__PURE__*/_createElementVNode(\"footer\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"text-center\" }, [\n    /*#__PURE__*/_createElementVNode(\"small\", null, [\n      /*#__PURE__*/_createTextVNode(\"[\"),\n      /*#__PURE__*/_createElementVNode(\"span\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"Powered by\")\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"https://markbind.org/favicon.ico\",\n        width: \"30\"\n      }),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://markbind.org/\" }, \"MarkBind 6.0.0\"),\n      /*#__PURE__*/_createTextVNode(\", generated on Wed, 16 Apr 2025, 5:20:58 UTC]\")\n    ])\n  ])\n], -1 /* HOISTED */)\n\nreturn function render(_ctx, _cache) {\n  const _component_searchbar = _resolveComponent(\"searchbar\")\n  const _component_navbar = _resolveComponent(\"navbar\")\n  const _component_overlay_source = _resolveComponent(\"overlay-source\")\n  const _component_site_nav = _resolveComponent(\"site-nav\")\n  const _component_box = _resolveComponent(\"box\")\n\n  return (_openBlock(), _createElementBlock(_Fragment, null, [\n    _createElementVNode(\"header\", _hoisted_1, [\n      _createVNode(_component_navbar, { type: \"dark\" }, {\n        brand: _withCtx(() => [\n          _hoisted_2\n        ]),\n        right: _withCtx(() => [\n          _createElementVNode(\"li\", null, [\n            _createElementVNode(\"form\", _hoisted_11, [\n              _createVNode(_component_searchbar, {\n                data: _ctx.searchData,\n                placeholder: \"Search\",\n                \"on-hit\": _ctx.searchCallback,\n                \"menu-align-right\": \"\"\n              }, null, 8 /* PROPS */, [\"data\", \"on-hit\"])\n            ])\n          ])\n        ]),\n        default: _withCtx(() => [\n          _createTextVNode(),\n          _hoisted_3,\n          _createTextVNode(),\n          _hoisted_4,\n          _createTextVNode(),\n          _hoisted_5,\n          _createTextVNode(),\n          _hoisted_6,\n          _createTextVNode(),\n          _hoisted_7,\n          _createTextVNode(),\n          _hoisted_8,\n          _createTextVNode(),\n          _hoisted_9,\n          _createTextVNode(),\n          _hoisted_10,\n          _createTextVNode()\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _hoisted_12\n    ]),\n    _createTextVNode(),\n    _createElementVNode(\"div\", _hoisted_13, [\n      _createVNode(_component_overlay_source, {\n        id: \"site-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"site-nav\"\n      }, {\n        default: _withCtx(() => [\n          _hoisted_14,\n          _createTextVNode(),\n          _createElementVNode(\"div\", _hoisted_15, [\n            _createElementVNode(\"div\", null, [\n              _createVNode(_component_site_nav, null, {\n                default: _withCtx(() => [\n                  _createVNode(_component_overlay_source, {\n                    class: \"site-nav-list site-nav-list-root\",\n                    \"tag-name\": \"ul\",\n                    to: \"mb-site-nav\"\n                  }, {\n                    default: _withCtx(() => [\n                      _hoisted_16,\n                      _createTextVNode(),\n                      _hoisted_17,\n                      _createTextVNode(),\n                      _hoisted_18,\n                      _createTextVNode(),\n                      _hoisted_19,\n                      _createTextVNode(),\n                      _hoisted_20,\n                      _createTextVNode(),\n                      _hoisted_21\n                    ]),\n                    _: 1 /* STABLE */\n                  })\n                ]),\n                _: 1 /* STABLE */\n              })\n            ])\n          ])\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _createElementVNode(\"div\", _hoisted_22, [\n        _hoisted_23,\n        _createTextVNode(),\n        _createElementVNode(\"div\", _hoisted_24, [\n          _createVNode(_component_box, {\n            type: \"warning\",\n            seamless: \"\"\n          }, {\n            default: _withCtx(() => [\n              _hoisted_25\n            ]),\n            _: 1 /* STABLE */\n          }),\n          _createTextVNode(),\n          _hoisted_26,\n          _createTextVNode(),\n          _hoisted_27,\n          _createTextVNode(),\n          _hoisted_28,\n          _createTextVNode(),\n          _hoisted_29,\n          _createTextVNode(),\n          _hoisted_30,\n          _createTextVNode(),\n          _hoisted_32,\n          _createTextVNode(),\n          _hoisted_33\n        ]),\n        _createTextVNode(),\n        _hoisted_34,\n        _createTextVNode(),\n        _hoisted_35,\n        _createTextVNode(),\n        _hoisted_36,\n        _createTextVNode(),\n        _hoisted_37,\n        _createTextVNode(),\n        _createElementVNode(\"ol\", null, [\n          _hoisted_38,\n          _createTextVNode(),\n          _hoisted_39,\n          _createTextVNode(),\n          _hoisted_40,\n          _createTextVNode(),\n          _hoisted_41,\n          _createTextVNode(),\n          _createElementVNode(\"li\", null, [\n            _hoisted_42,\n            _createTextVNode(),\n            _createVNode(_component_box, {\n              type: \"info\",\n              seamless: \"\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_43\n              ]),\n              _: 1 /* STABLE */\n            })\n          ]),\n          _createTextVNode(),\n          _hoisted_44,\n          _createTextVNode(),\n          _hoisted_45,\n          _createTextVNode(),\n          _hoisted_46,\n          _createTextVNode(),\n          _hoisted_47\n        ])\n      ]),\n      _createTextVNode(),\n      _createVNode(_component_overlay_source, {\n        id: \"page-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"page-nav\"\n      }, {\n        default: _withCtx(() => [\n          _createElementVNode(\"div\", _hoisted_48, [\n            _createVNode(_component_overlay_source, {\n              id: \"mb-page-nav\",\n              \"tag-name\": \"nav\",\n              to: \"mb-page-nav\",\n              class: \"nav nav-pills flex-column my-0 small no-flex-wrap\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_49,\n                _createTextVNode(),\n                _hoisted_50\n              ]),\n              _: 1 /* STABLE */\n            })\n          ])\n        ]),\n        _: 1 /* STABLE */\n      })\n    ]),\n    _createTextVNode(),\n    _hoisted_51\n  ], 64 /* STABLE_FRAGMENT */))\n}");
    var render = renderFn();
  