
    const renderFn = new Function("const { createElementVNode: _createElementVNode, createTextVNode: _createTextVNode, resolveComponent: _resolveComponent, createVNode: _createVNode, withCtx: _withCtx, createStaticVNode: _createStaticVNode, Fragment: _Fragment, openBlock: _openBlock, createElementBlock: _createElementBlock } = Vue\n\nconst _hoisted_1 = /*#__PURE__*/_createElementVNode(\"a\", {\n  href: \"/RepoSense/index.html\",\n  title: \"Home\",\n  class: \"navbar-brand\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"img\", {\n    width: \"30px\",\n    src: \"/RepoSense/favicon.ico\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_2 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/index.html\",\n    class: \"nav-link\"\n  }, \"HOME\")\n], -1 /* HOISTED */)\nconst _hoisted_3 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/showcase.html\",\n    class: \"nav-link\"\n  }, \"SHOWCASE\")\n], -1 /* HOISTED */)\nconst _hoisted_4 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/ug/index.html\",\n    class: \"nav-link\"\n  }, \"USER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_5 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/dg/index.html\",\n    class: \"nav-link\"\n  }, \"DEVELOPER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_6 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/about.html\",\n    class: \"nav-link\"\n  }, \"ABOUT\")\n], -1 /* HOISTED */)\nconst _hoisted_7 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/RepoSense/contact.html\",\n    class: \"nav-link\"\n  }, \"CONTACT\")\n], -1 /* HOISTED */)\nconst _hoisted_8 = /*#__PURE__*/_createElementVNode(\"li\", { tags: \"dev\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://reposense.org\",\n    class: \"nav-link\",\n    target: \"_blank\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fas fa-external-link-alt\"\n      }),\n      /*#__PURE__*/_createTextVNode(\" PRODUCTION SITE\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_9 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://github.com/RepoSense/reposense\",\n    target: \"_blank\",\n    class: \"nav-link\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fab fa-github\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_10 = { class: \"navbar-form\" }\nconst _hoisted_11 = { id: \"flex-body\" }\nconst _hoisted_12 = {\n  id: \"content-wrapper\",\n  class: \"fixed-header-padding\"\n}\nconst _hoisted_13 = /*#__PURE__*/_createElementVNode(\"h1\", {\n  class: \"display-3\",\n  id: \"showcase\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"span\", null, \"Showcase\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#showcase\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_14 = /*#__PURE__*/_createElementVNode(\"h3\", { id: \"case-1-monitoring-student-programmers-individual-projects\" }, [\n  /*#__PURE__*/_createTextVNode(\"Case 1: Monitoring student programmers (\"),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"individual\"),\n  /*#__PURE__*/_createTextVNode(\" projects)\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#case-1-monitoring-student-programmers-individual-projects\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_15 = /*#__PURE__*/_createStaticVNode(\"<ul><li><p><strong>Scenario:</strong> RepoSense is used to monitor a Software Engineering course in which students build a project over 8 weeks.</p></li> <li><p><strong>Links:</strong> <a href=\\\"https://nus-cs2103-ay2021s1.github.io/ip-dashboard/?search=&amp;sort=groupTitle&amp;sortWithin=title&amp;since=2020-08-14&amp;until=2020-09-27&amp;timeframe=commit&amp;mergegroup=&amp;groupSelect=groupByRepos&amp;breakdown=false\\\">report</a> | <a href=\\\"https://github.com/nus-cs2103-AY2021S1/ip-dashboard\\\">repo containing the settings</a></p></li> <li><p><strong>Example usages:</strong></p> <ul><li>To compare students based on the amount of code written, we can sort by contribution, as done in <a href=\\\"https://nus-cs2103-ay2021s1.github.io/ip-dashboard/?search=&amp;sort=totalCommits%20dsc&amp;sortWithin=title&amp;since=2020-08-14&amp;until=2020-09-27&amp;timeframe=commit&amp;mergegroup=&amp;groupSelect=groupByRepos&amp;breakdown=false\\\">this view</a>.</li> <li><a href=\\\"https://nus-cs2103-ay2021s1.github.io/ip-dashboard/?search=keanecjy&amp;sort=totalCommits%20dsc&amp;sortWithin=title&amp;since=2020-08-14&amp;until=2020-09-27&amp;timeframe=commit&amp;mergegroup=&amp;groupSelect=groupByRepos&amp;breakdown=false&amp;tabOpen=true&amp;tabType=authorship&amp;tabAuthor=keanecjy&amp;tabRepo=keanecjy%2Fip%5Bmaster%5D&amp;authorshipIsMergeGroup=false&amp;authorshipFileTypes=java~md~fxml~bat~gradle~txt\\\">This view</a> shows us code written by a specific student.</li></ul></li></ul>\", 1)\nconst _hoisted_16 = /*#__PURE__*/_createElementVNode(\"h3\", { id: \"case-2-monitoring-student-programmers-team-projects\" }, [\n  /*#__PURE__*/_createTextVNode(\"Case 2: Monitoring student programmers (\"),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"team\"),\n  /*#__PURE__*/_createTextVNode(\" projects)\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#case-2-monitoring-student-programmers-team-projects\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_17 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Scenario:\"),\n    /*#__PURE__*/_createTextVNode(\" Similar to case 1 above, but this time students are doing team projects.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_18 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Links:\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://nus-cs2103-ay2122s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=false&since=2021-09-17&until=2021-11-19\" }, \"report\"),\n    /*#__PURE__*/_createTextVNode(\" | \"),\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/nus-cs2103-AY2122S1/tp-dashboard\" }, \"settings\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_19 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"Example usages:\")\n], -1 /* HOISTED */)\nconst _hoisted_20 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createTextVNode(\"To find the breakdown of the work done, we can tick the \"),\n  /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"breakdown by file type\"),\n  /*#__PURE__*/_createTextVNode(\" checkbox, as shown in \"),\n  /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://nus-cs2103-ay2122s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2021-09-17&until=2021-11-19\" }, \"this view\"),\n  /*#__PURE__*/_createTextVNode(\". After that, we can filter out certain file types by un-ticking the file type.\")\n], -1 /* HOISTED */)\nconst _hoisted_21 = /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"merge all groups\", -1 /* HOISTED */)\nconst _hoisted_22 = /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Contribution\", -1 /* HOISTED */)\nconst _hoisted_23 = /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://nus-cs2103-ay2122s1.github.io/tp-dashboard/?search=&sort=totalCommits%20dsc&sortWithin=title&timeframe=week&mergegroup=AY2122S1-CS2103-F09-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103-F09-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103-F09-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103-F09-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103-F10-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103-F10-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103-F10-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103-F10-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T14-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T14-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T14-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T14-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T16-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T16-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T16-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103-T16-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103-W14-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103-W14-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103-W14-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103-W14-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F11-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F11-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F11-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F11-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F12-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F12-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F12-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F12-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F13-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F13-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F13-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-F13-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T09-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T09-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T09-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T09-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T10-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T10-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T10-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T10-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T11-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T11-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T11-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T11-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T12-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T12-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T12-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T12-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T13-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T13-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T13-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T13-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T15-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T15-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T15-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T15-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T17-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T17-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T17-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-T17-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W08-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W08-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W08-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W08-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W10-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W10-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W10-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W10-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W11-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W11-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W11-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W11-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W12-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W12-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W12-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W12-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W13-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W13-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W13-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W13-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W15-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W15-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W15-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W15-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W16-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W16-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W16-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W16-4%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W17-1%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W17-2%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W17-3%2Ftp%5Bmaster%5D~AY2122S1-CS2103T-W17-4%2Ftp%5Bmaster%5D&groupSelect=groupByRepos&breakdown=true&since=2021-09-17&until=2021-11-19&checkedFileTypes=docs~functional-code~test-code~other\" }, \"this view\", -1 /* HOISTED */)\nconst _hoisted_24 = /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"granularity\", -1 /* HOISTED */)\nconst _hoisted_25 = /*#__PURE__*/_createElementVNode(\"code\", { class: \"hljs inline no-lang\" }, \"Week\", -1 /* HOISTED */)\nconst _hoisted_26 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://nus-cs2103-ay2122s1.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2021-10-29&until=2021-11-11\" }, \"This view\"),\n  /*#__PURE__*/_createTextVNode(\" shows the activities near the submission deadline of \"),\n  /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://nus-cs2103-ay2122s1.github.io/website/admin/tp-w13.html\" }, \"8 November 2021\"),\n  /*#__PURE__*/_createTextVNode(\" (note how some have overshot the deadline and some others show a frenzy of activities very near to the deadline).\")\n], -1 /* HOISTED */)\nconst _hoisted_27 = /*#__PURE__*/_createElementVNode(\"h3\", { id: \"case-3-monitoring-student-programmers-multiple-external-projects\" }, [\n  /*#__PURE__*/_createTextVNode(\"Case 3: Monitoring student programmers (\"),\n  /*#__PURE__*/_createElementVNode(\"strong\", null, \"multiple\"),\n  /*#__PURE__*/_createTextVNode(\" external projects)\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#case-3-monitoring-student-programmers-multiple-external-projects\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_28 = /*#__PURE__*/_createStaticVNode(\"<ul><li><p><strong>Scenario:</strong> Similar to cases 1 and 2 above, but this time, each student works on multiple projects. Furthermore, most projects are external OSS projects, not within the control of the teacher.</p></li> <li><p><strong>Links:</strong> <a href=\\\"https://nus-cs3281.github.io/2020-dashboard/?search=&amp;sort=groupTitle&amp;sortWithin=title&amp;since=2019-12-01&amp;timeframe=commit&amp;mergegroup=&amp;groupSelect=groupByRepos&amp;breakdown=false\\\">report</a> | <a href=\\\"https://github.com/nus-cs3281/2020-dashboard\\\">settings</a></p></li> <li><p><strong>Example usages:</strong></p> <ul><li><a href=\\\"https://nus-cs3281.github.io/2020-dashboard/?search=&amp;sort=groupTitle&amp;sortWithin=title&amp;since=2019-12-01&amp;timeframe=commit&amp;mergegroup=&amp;groupSelect=groupByRepos&amp;breakdown=false&amp;tabOpen=true&amp;tabType=zoom&amp;zA=anubh-v&amp;zR=CATcher-org%2FCATcher%5Bmaster%5D&amp;zACS=153.40466101694915&amp;zS=2019-12-01&amp;zFS=&amp;zU=2021-06-15&amp;zMG=false&amp;zFTF=commit&amp;zFGS=groupByRepos&amp;zFR=false\\\">This view</a> shows the commit messages written by a specific student.</li> <li>Note how we can use the <code class=\\\"hljs inline no-lang\\\">group by</code> drop-down to organize activities around projects or individual authors.</li> <li>Similarly, we can use the <code class=\\\"hljs inline no-lang\\\">merge all groups</code> check-box to see the sum of activities in a specific project or by a specific student.</li></ul></li></ul>\", 1)\nconst _hoisted_29 = /*#__PURE__*/_createElementVNode(\"footer\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"text-center\" }, [\n    /*#__PURE__*/_createElementVNode(\"small\", null, [\n      /*#__PURE__*/_createTextVNode(\"[\"),\n      /*#__PURE__*/_createElementVNode(\"span\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"Powered by\")\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"https://markbind.org/favicon.ico\",\n        width: \"30\"\n      }),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://markbind.org/\" }, \"MarkBind 6.0.0\"),\n      /*#__PURE__*/_createTextVNode(\", generated on Sat, 19 Apr 2025, 16:18:32 UTC]\")\n    ])\n  ])\n], -1 /* HOISTED */)\n\nreturn function render(_ctx, _cache) {\n  const _component_searchbar = _resolveComponent(\"searchbar\")\n  const _component_navbar = _resolveComponent(\"navbar\")\n  const _component_tooltip = _resolveComponent(\"tooltip\")\n\n  return (_openBlock(), _createElementBlock(_Fragment, null, [\n    _createVNode(_component_navbar, { type: \"dark\" }, {\n      brand: _withCtx(() => [\n        _hoisted_1\n      ]),\n      right: _withCtx(() => [\n        _createElementVNode(\"li\", null, [\n          _createElementVNode(\"form\", _hoisted_10, [\n            _createVNode(_component_searchbar, {\n              data: _ctx.searchData,\n              placeholder: \"Search\",\n              \"on-hit\": _ctx.searchCallback,\n              \"menu-align-right\": \"\"\n            }, null, 8 /* PROPS */, [\"data\", \"on-hit\"])\n          ])\n        ])\n      ]),\n      default: _withCtx(() => [\n        _createTextVNode(),\n        _hoisted_2,\n        _createTextVNode(),\n        _hoisted_3,\n        _createTextVNode(),\n        _hoisted_4,\n        _createTextVNode(),\n        _hoisted_5,\n        _createTextVNode(),\n        _hoisted_6,\n        _createTextVNode(),\n        _hoisted_7,\n        _createTextVNode(),\n        _hoisted_8,\n        _createTextVNode(),\n        _hoisted_9,\n        _createTextVNode()\n      ]),\n      _: 1 /* STABLE */\n    }),\n    _createTextVNode(),\n    _createElementVNode(\"div\", _hoisted_11, [\n      _createElementVNode(\"div\", _hoisted_12, [\n        _hoisted_13,\n        _createTextVNode(),\n        _hoisted_14,\n        _createTextVNode(),\n        _hoisted_15,\n        _createTextVNode(),\n        _hoisted_16,\n        _createTextVNode(),\n        _createElementVNode(\"ul\", null, [\n          _hoisted_17,\n          _createTextVNode(),\n          _hoisted_18,\n          _createTextVNode(),\n          _createElementVNode(\"li\", null, [\n            _hoisted_19,\n            _createTextVNode(),\n            _createElementVNode(\"ul\", null, [\n              _hoisted_20,\n              _createTextVNode(),\n              _createElementVNode(\"li\", null, [\n                _createTextVNode(\"To find how teams compare in terms of total work done, we can tick the \"),\n                _hoisted_21,\n                _createTextVNode(\" check-box and sort groups by \"),\n                _hoisted_22,\n                _createTextVNode(\", as seen in \"),\n                _hoisted_23,\n                _createTextVNode(\". Also, note how \"),\n                _createVNode(_component_tooltip, null, {\n                  content: _withCtx(() => [\n                    _createTextVNode(\"i.e., each ramp represents the work done by the entire team in the whole week\")\n                  ]),\n                  default: _withCtx(() => [\n                    _createTextVNode(\"the \"),\n                    _hoisted_24,\n                    _createTextVNode(\" of the ramps is set to \"),\n                    _hoisted_25\n                  ]),\n                  _: 1 /* STABLE */\n                }),\n                _createTextVNode(\" to reduce clutter.\")\n              ]),\n              _createTextVNode(),\n              _hoisted_26\n            ])\n          ])\n        ]),\n        _createTextVNode(),\n        _hoisted_27,\n        _createTextVNode(),\n        _hoisted_28\n      ])\n    ]),\n    _createTextVNode(),\n    _hoisted_29\n  ], 64 /* STABLE_FRAGMENT */))\n}");
    var render = renderFn();
  