
    const renderFn = new Function("const { createElementVNode: _createElementVNode, createTextVNode: _createTextVNode, resolveComponent: _resolveComponent, createVNode: _createVNode, withCtx: _withCtx, createStaticVNode: _createStaticVNode, Fragment: _Fragment, openBlock: _openBlock, createElementBlock: _createElementBlock } = Vue\n\nconst _hoisted_1 = /*#__PURE__*/_createElementVNode(\"a\", {\n  href: \"/RepoSense/index.html\",\n  title: \"Home\",\n  class: \"navbar-brand\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"img\", {\n    width: \"30px\",\n    src: \"/RepoSense/favicon.ico\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_2 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/index.html\",\n    class: \"nav-link\"\n  }, \"HOME\")\n], -1 /* HOISTED */)\nconst _hoisted_3 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/showcase.html\",\n    class: \"nav-link\"\n  }, \"SHOWCASE\")\n], -1 /* HOISTED */)\nconst _hoisted_4 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/ug/index.html\",\n    class: \"nav-link\"\n  }, \"USER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_5 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/dg/index.html\",\n    class: \"nav-link\"\n  }, \"DEVELOPER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_6 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/about.html\",\n    class: \"nav-link\"\n  }, \"ABOUT\")\n], -1 /* HOISTED */)\nconst _hoisted_7 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/contact.html\",\n    class: \"nav-link\"\n  }, \"CONTACT\")\n], -1 /* HOISTED */)\nconst _hoisted_8 = /*#__PURE__*/_createElementVNode(\"li\", { tags: \"dev\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://reposense.org\",\n    class: \"nav-link\",\n    target: \"_blank\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fas fa-external-link-alt\"\n      }),\n      /*#__PURE__*/_createTextVNode(\" PRODUCTION SITE\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_9 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://github.com/RepoSense/reposense\",\n    target: \"_blank\",\n    class: \"nav-link\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fab fa-github\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_10 = { class: \"navbar-form\" }\nconst _hoisted_11 = { id: \"flex-body\" }\nconst _hoisted_12 = {\n  id: \"content-wrapper\",\n  class: \"fixed-header-padding\"\n}\nconst _hoisted_13 = /*#__PURE__*/_createElementVNode(\"header\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"jumbotron jumbotron-fluid text-center\",\n    style: {\"padding-top\":\"inherit\",\"padding-bottom\":\"inherit\"}\n  }, [\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"container\" }, [\n      /*#__PURE__*/_createElementVNode(\"h1\", {\n        class: \"display-3\",\n        id: \"reposense\"\n      }, [\n        /*#__PURE__*/_createTextVNode(\"RepoSense\"),\n        /*#__PURE__*/_createElementVNode(\"a\", {\n          class: \"fa fa-anchor\",\n          href: \"#reposense\",\n          onclick: \"event.stopPropagation()\"\n        })\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"div\", { class: \"lead\" }, [\n        /*#__PURE__*/_createElementVNode(\"p\", null, [\n          /*#__PURE__*/_createTextVNode(\"Visualize programmer activities across Git repositories...\\n\"),\n          /*#__PURE__*/_createElementVNode(\"br\"),\n          /*#__PURE__*/_createElementVNode(\"br\"),\n          /*#__PURE__*/_createTextVNode(),\n          /*#__PURE__*/_createElementVNode(\"img\", {\n            src: \"/RepoSense/images/reposenseOverview.png\",\n            width: \"909\",\n            alt: \"RepoSense overview\"\n          }),\n          /*#__PURE__*/_createTextVNode(),\n          /*#__PURE__*/_createElementVNode(\"br\"),\n          /*#__PURE__*/_createElementVNode(\"br\")\n        ])\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_14 = { id: \"overview\" }\nconst _hoisted_15 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"RepoSense can generate interactive visualizations of programmer activities, even across multiple repositories.\"),\n  /*#__PURE__*/_createTextVNode(\" It's ideal for educators and managers to get insights into the programming activities of their mentees. The visualizations can be \"),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"easily shared\"),\n  /*#__PURE__*/_createTextVNode(\" with others (e.g., as an online dashboard), and updating the visualizations periodically \"),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"can be automated\"),\n  /*#__PURE__*/_createTextVNode(\".\")\n], -1 /* HOISTED */)\nconst _hoisted_16 = /*#__PURE__*/_createElementVNode(\"p\", null, \"Some example insights RepoSense can provide:\", -1 /* HOISTED */)\nconst _hoisted_17 = { id: \"insights-about-the-code\" }\nconst _hoisted_18 = /*#__PURE__*/_createElementVNode(\"span\", {\n  \"aria-hidden\": \"true\",\n  class: \"fas fa-code\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_19 = /*#__PURE__*/_createElementVNode(\"span\", { class: \"lead font-weight-bold text-green\" }, \"Insights about the code\", -1 /* HOISTED */)\nconst _hoisted_20 = /*#__PURE__*/_createElementVNode(\"a\", {\n  class: \"fa fa-anchor\",\n  href: \"#insights-about-the-code\",\n  onclick: \"event.stopPropagation()\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_21 = /*#__PURE__*/_createElementVNode(\"ul\", null, [\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Which part of the code was written by Tom? How many lines? How many files?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Which test cases were written by Kim?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Which commit messages were written by Serene?\")\n], -1 /* HOISTED */)\nconst _hoisted_22 = { id: \"insights-about-the-type-of-work\" }\nconst _hoisted_23 = /*#__PURE__*/_createElementVNode(\"span\", {\n  \"aria-hidden\": \"true\",\n  class: \"fas fa-chart-pie\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_24 = /*#__PURE__*/_createElementVNode(\"span\", { class: \"lead font-weight-bold text-green\" }, \"Insights about the type of work\", -1 /* HOISTED */)\nconst _hoisted_25 = /*#__PURE__*/_createElementVNode(\"a\", {\n  class: \"fa fa-anchor\",\n  href: \"#insights-about-the-type-of-work\",\n  onclick: \"event.stopPropagation()\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_26 = /*#__PURE__*/_createElementVNode(\"ul\", null, [\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Which portion of Jacob's code is documentation?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Who hasn't written any test code yet?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Which project did Jolene contribute to in the last month?\")\n], -1 /* HOISTED */)\nconst _hoisted_27 = { id: \"insights-about-the-timing-of-work\" }\nconst _hoisted_28 = /*#__PURE__*/_createElementVNode(\"span\", {\n  \"aria-hidden\": \"true\",\n  class: \"fas fa-business-time\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_29 = /*#__PURE__*/_createElementVNode(\"span\", { class: \"lead font-weight-bold text-green\" }, \"Insights about the timing of work\", -1 /* HOISTED */)\nconst _hoisted_30 = /*#__PURE__*/_createElementVNode(\"a\", {\n  class: \"fa fa-anchor\",\n  href: \"#insights-about-the-timing-of-work\",\n  onclick: \"event.stopPropagation()\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_31 = /*#__PURE__*/_createElementVNode(\"ul\", null, [\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Who is putting in the consistent effort?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Who waits till the deadline to do the work?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Who hasn't started any work yet?\")\n], -1 /* HOISTED */)\nconst _hoisted_32 = { id: \"insights-based-on-comparisons\" }\nconst _hoisted_33 = /*#__PURE__*/_createElementVNode(\"span\", {\n  \"aria-hidden\": \"true\",\n  class: \"fas fa-list-ol\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_34 = /*#__PURE__*/_createElementVNode(\"span\", { class: \"lead font-weight-bold text-green\" }, \"Insights based on comparisons\", -1 /* HOISTED */)\nconst _hoisted_35 = /*#__PURE__*/_createElementVNode(\"a\", {\n  class: \"fa fa-anchor\",\n  href: \"#insights-based-on-comparisons\",\n  onclick: \"event.stopPropagation()\"\n}, null, -1 /* HOISTED */)\nconst _hoisted_36 = /*#__PURE__*/_createElementVNode(\"ul\", null, [\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Which programmers/teams are falling behind?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"How does everyone compare in their front-end coding work over the past two weeks?\"),\n  /*#__PURE__*/_createTextVNode(),\n  /*#__PURE__*/_createElementVNode(\"li\", null, \"Who are the top 10 code contributors?\")\n], -1 /* HOISTED */)\nconst _hoisted_37 = /*#__PURE__*/_createStaticVNode(\"<div class=\\\"jumbotron jumbotron-fluid pt-2\\\"><div class=\\\"container\\\"><div class=\\\"container pt-2\\\"><div class=\\\"row\\\"></div> <div class=\\\"row\\\"><div class=\\\"col-sm\\\"><p><a href=\\\"/RepoSense/showcase.html\\\"><strong>SHOWCASE</strong> of use cases</a></p> <p><a href=\\\"/RepoSense/about.html\\\"><strong>ABOUT</strong> us</a></p> <p><a href=\\\"/RepoSense/contact.html\\\"><strong>CONTACT</strong> us</a></p> <p><a href=\\\"https://github.com/reposense/RepoSense\\\"><strong><span aria-hidden=\\\"true\\\" class=\\\"fab fa-github\\\"></span> GitHub</strong></a></p> <hr> <small><p>This website was built using MarkBind.</p> <p><a href=\\\"http://markbind.org\\\"><img src=\\\"https://markbind.org/images/logo-lightbackground.png\\\" width=\\\"150\\\"></a></p> <p>Deploy previews are powered by Netlify and Surge.</p> <p><a href=\\\"https://www.netlify.com\\\"><img src=\\\"https://www.netlify.com/img/global/badges/netlify-color-bg.svg\\\"></a> <a href=\\\"https://surge.sh\\\"><img width=\\\"55px\\\" src=\\\"https://surge.sh/images/logos/svg/surge-logo.svg\\\"></a></p></small><p></p></div> <div class=\\\"col-sm\\\"><p><strong><strong>USER GUIDE</strong></strong></p> <ul><li><p><a href=\\\"/RepoSense/ug/index.html\\\"><strong>Overview</strong></a></p></li> <li><p><a href=\\\"/RepoSense/ug/generatingReports.html\\\"><strong>Generating reports</strong></a></p></li> <li><p><a href=\\\"/RepoSense/ug/usingReports.html\\\"><strong>Using reports</strong></a></p></li> <li><p><a href=\\\"/RepoSense/ug/customizingReports.html\\\"><strong>Customizing reports</strong></a></p></li> <li><p><a href=\\\"/RepoSense/ug/sharingReports.html\\\"><strong>Sharing reports</strong></a></p></li> <li><p><strong>Appendices</strong></p> <ul><li><p><a href=\\\"/RepoSense/ug/cli.html\\\">CLI syntax reference</a></p></li> <li><p><a href=\\\"/RepoSense/ug/reportConfig.html\\\">Getting started with <code class=\\\"hljs inline no-lang\\\">report-config.yaml</code></a></p></li> <li><p><a href=\\\"/RepoSense/ug/configFiles.html\\\">Config files format</a></p></li> <li><p><a href=\\\"/RepoSense/ug/authorConfigSyntax.html\\\">Advanced syntax: <code class=\\\"hljs inline no-lang\\\">author-config.csv</code></a></p></li> <li><p><a href=\\\"/RepoSense/ug/usingAuthorTags.html\\\">Using <code class=\\\"hljs inline no-lang\\\">@@author</code> tags</a></p></li> <li><p><a href=\\\"/RepoSense/ug/withNetlify.html\\\">RepoSense with Netlify</a></p></li> <li><p><a href=\\\"/RepoSense/ug/withGithubActions.html\\\">RepoSense with GitHub Actions</a></p></li> <li><p><a href=\\\"/RepoSense/ug/runSh.html\\\"><code class=\\\"hljs inline no-lang\\\">run.sh</code> format</a></p></li> <li><p><a href=\\\"/RepoSense/ug/faq.html\\\">FAQ</a></p></li> <li><p><a href=\\\"/RepoSense/ug/troubleshooting.html\\\">Troubleshooting</a></p></li></ul></li></ul></div> <div class=\\\"col-sm\\\"><p><strong><strong>DEVELOPER GUIDE</strong></strong></p> <ul><li><p><a href=\\\"/RepoSense/dg/index.html\\\"><strong>Contributing</strong></a></p></li> <li><p><a href=\\\"/RepoSense/dg/settingUp.html\\\"><strong>Setting up</strong></a></p></li> <li><p><a href=\\\"/RepoSense/dg/learningBasics.html\\\"><strong>Learning the basics</strong></a></p></li> <li><p><a href=\\\"/RepoSense/dg/workflow.html\\\"><strong>Workflow</strong></a></p></li> <li><p><strong>Design and implementation</strong></p> <ul><li><p><a href=\\\"/RepoSense/dg/architecture.html\\\">Architecture</a></p></li> <li><p><a href=\\\"/RepoSense/dg/report.html\\\">HTML report</a></p></li></ul></li> <li><p><a href=\\\"/RepoSense/dg/projectManagement.html\\\"><strong>Project management</strong></a></p></li> <li><p><a href=\\\"/RepoSense/dg/devOpsGuide.html\\\"><strong>DevOps guide</strong></a></p></li> <li><p><strong>Appendices</strong></p> <ul><li><p><a href=\\\"/RepoSense/dg/cli.html\\\">CLI syntax reference</a></p></li> <li><p><a href=\\\"/RepoSense/dg/styleGuides.html\\\">Style guides</a></p></li></ul></li></ul></div></div></div></div></div>\", 1)\nconst _hoisted_38 = /*#__PURE__*/_createElementVNode(\"footer\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"text-center\" }, [\n    /*#__PURE__*/_createElementVNode(\"small\", null, [\n      /*#__PURE__*/_createTextVNode(\"[\"),\n      /*#__PURE__*/_createElementVNode(\"span\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"Powered by\")\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"https://markbind.org/favicon.ico\",\n        width: \"30\"\n      }),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://markbind.org/\" }, \"MarkBind 6.0.0\"),\n      /*#__PURE__*/_createTextVNode(\", generated on Thu, 17 Apr 2025, 7:46:22 UTC]\")\n    ])\n  ])\n], -1 /* HOISTED */)\n\nreturn function render(_ctx, _cache) {\n  const _component_searchbar = _resolveComponent(\"searchbar\")\n  const _component_navbar = _resolveComponent(\"navbar\")\n  const _component_thumbnail = _resolveComponent(\"thumbnail\")\n\n  return (_openBlock(), _createElementBlock(_Fragment, null, [\n    _createVNode(_component_navbar, { type: \"dark\" }, {\n      brand: _withCtx(() => [\n        _hoisted_1\n      ]),\n      right: _withCtx(() => [\n        _createElementVNode(\"li\", null, [\n          _createElementVNode(\"form\", _hoisted_10, [\n            _createVNode(_component_searchbar, {\n              data: _ctx.searchData,\n              placeholder: \"Search\",\n              \"on-hit\": _ctx.searchCallback,\n              \"menu-align-right\": \"\"\n            }, null, 8 /* PROPS */, [\"data\", \"on-hit\"])\n          ])\n        ])\n      ]),\n      default: _withCtx(() => [\n        _createTextVNode(),\n        _hoisted_2,\n        _createTextVNode(),\n        _hoisted_3,\n        _createTextVNode(),\n        _hoisted_4,\n        _createTextVNode(),\n        _hoisted_5,\n        _createTextVNode(),\n        _hoisted_6,\n        _createTextVNode(),\n        _hoisted_7,\n        _createTextVNode(),\n        _hoisted_8,\n        _createTextVNode(),\n        _hoisted_9,\n        _createTextVNode()\n      ]),\n      _: 1 /* STABLE */\n    }),\n    _createTextVNode(),\n    _createElementVNode(\"div\", _hoisted_11, [\n      _createElementVNode(\"div\", _hoisted_12, [\n        _hoisted_13,\n        _createTextVNode(),\n        _createElementVNode(\"span\", _hoisted_14, [\n          _hoisted_15,\n          _createTextVNode(),\n          _hoisted_16,\n          _createTextVNode(),\n          _createElementVNode(\"h4\", _hoisted_17, [\n            _createVNode(_component_thumbnail, {\n              circle: \"\",\n              background: \"#006600\",\n              \"font-color\": \"white\",\n              size: \"50\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_18\n              ]),\n              _: 1 /* STABLE */\n            }),\n            _createTextVNode(),\n            _hoisted_19,\n            _hoisted_20\n          ]),\n          _createTextVNode(),\n          _hoisted_21,\n          _createTextVNode(),\n          _createElementVNode(\"h4\", _hoisted_22, [\n            _createVNode(_component_thumbnail, {\n              circle: \"\",\n              background: \"#006600\",\n              \"font-color\": \"white\",\n              size: \"50\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_23\n              ]),\n              _: 1 /* STABLE */\n            }),\n            _createTextVNode(),\n            _hoisted_24,\n            _hoisted_25\n          ]),\n          _createTextVNode(),\n          _hoisted_26,\n          _createTextVNode(),\n          _createElementVNode(\"h4\", _hoisted_27, [\n            _createVNode(_component_thumbnail, {\n              circle: \"\",\n              background: \"#006600\",\n              \"font-color\": \"white\",\n              size: \"50\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_28\n              ]),\n              _: 1 /* STABLE */\n            }),\n            _createTextVNode(),\n            _hoisted_29,\n            _hoisted_30\n          ]),\n          _createTextVNode(),\n          _hoisted_31,\n          _createTextVNode(),\n          _createElementVNode(\"h4\", _hoisted_32, [\n            _createVNode(_component_thumbnail, {\n              circle: \"\",\n              background: \"#006600\",\n              \"font-color\": \"white\",\n              size: \"50\"\n            }, {\n              default: _withCtx(() => [\n                _hoisted_33\n              ]),\n              _: 1 /* STABLE */\n            }),\n            _createTextVNode(),\n            _hoisted_34,\n            _hoisted_35\n          ]),\n          _createTextVNode(),\n          _hoisted_36\n        ]),\n        _createTextVNode(),\n        _hoisted_37\n      ])\n    ]),\n    _createTextVNode(),\n    _hoisted_38\n  ], 64 /* STABLE_FRAGMENT */))\n}");
    var render = renderFn();
  