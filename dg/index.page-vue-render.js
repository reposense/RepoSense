
    const renderFn = new Function("const { createElementVNode: _createElementVNode, createTextVNode: _createTextVNode, resolveComponent: _resolveComponent, createVNode: _createVNode, withCtx: _withCtx, createStaticVNode: _createStaticVNode, Fragment: _Fragment, openBlock: _openBlock, createElementBlock: _createElementBlock } = Vue\n\nconst _hoisted_1 = { fixed: \"\" }\nconst _hoisted_2 = /*#__PURE__*/_createElementVNode(\"a\", {\n  href: \"/index.html\",\n  title: \"Home\",\n  class: \"navbar-brand\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"img\", {\n    width: \"30px\",\n    src: \"/favicon.ico\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_3 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/index.html\",\n    class: \"nav-link\"\n  }, \"HOME\")\n], -1 /* HOISTED */)\nconst _hoisted_4 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/showcase.html\",\n    class: \"nav-link\"\n  }, \"SHOWCASE\")\n], -1 /* HOISTED */)\nconst _hoisted_5 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/ug/index.html\",\n    class: \"nav-link\"\n  }, \"USER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_6 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/dg/index.html\",\n    class: \"nav-link\"\n  }, \"DEVELOPER GUIDE\")\n], -1 /* HOISTED */)\nconst _hoisted_7 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/about.html\",\n    class: \"nav-link\"\n  }, \"ABOUT\")\n], -1 /* HOISTED */)\nconst _hoisted_8 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"/contact.html\",\n    class: \"nav-link\"\n  }, \"CONTACT\")\n], -1 /* HOISTED */)\nconst _hoisted_9 = /*#__PURE__*/_createElementVNode(\"li\", { tags: \"dev\" }, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://reposense.org\",\n    class: \"nav-link\",\n    target: \"_blank\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fas fa-external-link-alt\"\n      }),\n      /*#__PURE__*/_createTextVNode(\" PRODUCTION SITE\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_10 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    href: \"https://github.com/RepoSense/reposense\",\n    target: \"_blank\",\n    class: \"nav-link\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"span\", {\n        \"aria-hidden\": \"true\",\n        class: \"fab fa-github\"\n      })\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_11 = { class: \"navbar-form\" }\nconst _hoisted_12 = { id: \"flex-body\" }\nconst _hoisted_13 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-top\" }, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"font-weight-bold mb-2\",\n    style: {\"font-size\":\"1.25rem\"}\n  }, [\n    /*#__PURE__*/_createElementVNode(\"span\", null, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"DEVELOPER GUIDE\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_14 = { class: \"nav-component slim-scroll\" }\nconst _hoisted_15 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/index.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Contributing\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_16 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/settingUp.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Setting up\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_17 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/learningBasics.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Learning the basics\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_18 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/workflow.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Workflow\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_19 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Design and implementation\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-dropdown-btn-container\" }, [\n      /*#__PURE__*/_createElementVNode(\"i\", {\n        class: \"site-nav-dropdown-btn-icon site-nav-rotate-icon\",\n        onclick: \"handleSiteNavClick(this.parentNode.parentNode, false); event.stopPropagation();\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"span\", {\n          class: \"glyphicon glyphicon-menu-down\",\n          \"aria-hidden\": \"true\"\n        })\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createElementVNode(\"ul\", { class: \"site-nav-dropdown-container site-nav-dropdown-container-open site-nav-list\" }, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/architecture.html\" }, \"Architecture\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/report.html\" }, \"HTML report\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_20 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/projectManagement.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"Project management\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_21 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/devOpsGuide.html\" }, [\n      /*#__PURE__*/_createElementVNode(\"strong\", null, \"DevOps guide\")\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_22 = /*#__PURE__*/_createElementVNode(\"li\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", {\n    class: \"site-nav-default-list-item site-nav-list-item-0\",\n    onclick: \"handleSiteNavClick(this)\"\n  }, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Appendices\"),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"div\", { class: \"site-nav-dropdown-btn-container\" }, [\n      /*#__PURE__*/_createElementVNode(\"i\", {\n        class: \"site-nav-dropdown-btn-icon site-nav-rotate-icon\",\n        onclick: \"handleSiteNavClick(this.parentNode.parentNode, false); event.stopPropagation();\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"span\", {\n          class: \"glyphicon glyphicon-menu-down\",\n          \"aria-hidden\": \"true\"\n        })\n      ])\n    ])\n  ]),\n  /*#__PURE__*/_createElementVNode(\"ul\", { class: \"site-nav-dropdown-container site-nav-dropdown-container-open site-nav-list\" }, [\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/cli.html\" }, \"CLI syntax reference\")\n      ])\n    ]),\n    /*#__PURE__*/_createTextVNode(),\n    /*#__PURE__*/_createElementVNode(\"li\", null, [\n      /*#__PURE__*/_createElementVNode(\"div\", {\n        class: \"site-nav-default-list-item site-nav-list-item-1\",\n        onclick: \"handleSiteNavClick(this)\"\n      }, [\n        /*#__PURE__*/_createElementVNode(\"a\", { href: \"/RepoSense/dg/styleGuides.html\" }, \"Style guides\")\n      ])\n    ])\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_23 = {\n  id: \"content-wrapper\",\n  class: \"fixed-header-padding\"\n}\nconst _hoisted_24 = /*#__PURE__*/_createElementVNode(\"h1\", {\n  class: \"display-3\",\n  id: \"developer-guide\"\n}, [\n  /*#__PURE__*/_createElementVNode(\"span\", null, \"Developer guide\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#developer-guide\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_25 = /*#__PURE__*/_createElementVNode(\"h2\", { id: \"contributing\" }, [\n  /*#__PURE__*/_createTextVNode(\"Contributing\"),\n  /*#__PURE__*/_createElementVNode(\"a\", {\n    class: \"fa fa-anchor\",\n    href: \"#contributing\",\n    onclick: \"event.stopPropagation()\"\n  })\n], -1 /* HOISTED */)\nconst _hoisted_26 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"lead\" }, [\n  /*#__PURE__*/_createElementVNode(\"p\", null, [\n    /*#__PURE__*/_createElementVNode(\"strong\", null, \"Thank you for your interest in contributing to RepoSense!\"),\n    /*#__PURE__*/_createTextVNode(\" We welcome PRs, especially from students.\")\n  ])\n], -1 /* HOISTED */)\nconst _hoisted_27 = /*#__PURE__*/_createElementVNode(\"p\", null, \"Here is a quick overview of how to get started.\", -1 /* HOISTED */)\nconst _hoisted_28 = /*#__PURE__*/_createStaticVNode(\"<ol><li><strong>Set up the project</strong> on your computer (see <a href=\\\"/RepoSense/dg/settingUp.html\\\">the <em>Setting up</em> page</a>)</li> <li><strong>Learn the basics</strong> of the required background knowledge (see <a href=\\\"/RepoSense/dg/learningBasics.html\\\">the <em>Learning Basics</em> page</a>)</li> <li><strong>Learn our workflow</strong> (see <a href=\\\"/RepoSense/dg/workflow.html\\\">the <em>Workflow</em> page</a>)</li> <li><strong>Submit PRs</strong> as per the workflow. If you encounter any problems, post in <a href=\\\"https://github.com/reposense/RepoSense/issues\\\">our issue tracker</a>.</li></ol>\", 1)\nconst _hoisted_29 = /*#__PURE__*/_createElementVNode(\"p\", null, [\n  /*#__PURE__*/_createTextVNode(\"To gain a more solid understanding of the project implementation and workflow, you may want to start by working on \"),\n  /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://github.com/reposense/RepoSense/issues?q=is%3Aopen+is%3Aissue+label%3Ad.FirstTimers\" }, \"issues that are suitable for first timers\"),\n  /*#__PURE__*/_createTextVNode(\".\")\n], -1 /* HOISTED */)\nconst _hoisted_30 = /*#__PURE__*/_createElementVNode(\"p\", null, \"We look forward to your PR. Happy coding!\", -1 /* HOISTED */)\nconst _hoisted_31 = /*#__PURE__*/_createElementVNode(\"div\", { class: \"nav-component slim-scroll\" }, null, -1 /* HOISTED */)\nconst _hoisted_32 = /*#__PURE__*/_createElementVNode(\"footer\", null, [\n  /*#__PURE__*/_createElementVNode(\"div\", { class: \"text-center\" }, [\n    /*#__PURE__*/_createElementVNode(\"small\", null, [\n      /*#__PURE__*/_createTextVNode(\"[\"),\n      /*#__PURE__*/_createElementVNode(\"span\", null, [\n        /*#__PURE__*/_createElementVNode(\"strong\", null, \"Powered by\")\n      ]),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"img\", {\n        src: \"https://markbind.org/favicon.ico\",\n        width: \"30\"\n      }),\n      /*#__PURE__*/_createTextVNode(),\n      /*#__PURE__*/_createElementVNode(\"a\", { href: \"https://markbind.org/\" }, \"MarkBind 6.0.0\"),\n      /*#__PURE__*/_createTextVNode(\", generated on Sat, 19 Apr 2025, 16:18:32 UTC]\")\n    ])\n  ])\n], -1 /* HOISTED */)\n\nreturn function render(_ctx, _cache) {\n  const _component_searchbar = _resolveComponent(\"searchbar\")\n  const _component_navbar = _resolveComponent(\"navbar\")\n  const _component_overlay_source = _resolveComponent(\"overlay-source\")\n  const _component_site_nav = _resolveComponent(\"site-nav\")\n  const _component_box = _resolveComponent(\"box\")\n\n  return (_openBlock(), _createElementBlock(_Fragment, null, [\n    _createElementVNode(\"header\", _hoisted_1, [\n      _createVNode(_component_navbar, { type: \"dark\" }, {\n        brand: _withCtx(() => [\n          _hoisted_2\n        ]),\n        right: _withCtx(() => [\n          _createElementVNode(\"li\", null, [\n            _createElementVNode(\"form\", _hoisted_11, [\n              _createVNode(_component_searchbar, {\n                data: _ctx.searchData,\n                placeholder: \"Search\",\n                \"on-hit\": _ctx.searchCallback,\n                \"menu-align-right\": \"\"\n              }, null, 8 /* PROPS */, [\"data\", \"on-hit\"])\n            ])\n          ])\n        ]),\n        default: _withCtx(() => [\n          _createTextVNode(),\n          _hoisted_3,\n          _createTextVNode(),\n          _hoisted_4,\n          _createTextVNode(),\n          _hoisted_5,\n          _createTextVNode(),\n          _hoisted_6,\n          _createTextVNode(),\n          _hoisted_7,\n          _createTextVNode(),\n          _hoisted_8,\n          _createTextVNode(),\n          _hoisted_9,\n          _createTextVNode(),\n          _hoisted_10,\n          _createTextVNode()\n        ]),\n        _: 1 /* STABLE */\n      })\n    ]),\n    _createTextVNode(),\n    _createElementVNode(\"div\", _hoisted_12, [\n      _createVNode(_component_overlay_source, {\n        id: \"site-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"site-nav\"\n      }, {\n        default: _withCtx(() => [\n          _hoisted_13,\n          _createTextVNode(),\n          _createElementVNode(\"div\", _hoisted_14, [\n            _createElementVNode(\"div\", null, [\n              _createVNode(_component_site_nav, null, {\n                default: _withCtx(() => [\n                  _createVNode(_component_overlay_source, {\n                    class: \"site-nav-list site-nav-list-root\",\n                    \"tag-name\": \"ul\",\n                    to: \"mb-site-nav\"\n                  }, {\n                    default: _withCtx(() => [\n                      _hoisted_15,\n                      _createTextVNode(),\n                      _hoisted_16,\n                      _createTextVNode(),\n                      _hoisted_17,\n                      _createTextVNode(),\n                      _hoisted_18,\n                      _createTextVNode(),\n                      _hoisted_19,\n                      _createTextVNode(),\n                      _hoisted_20,\n                      _createTextVNode(),\n                      _hoisted_21,\n                      _createTextVNode(),\n                      _hoisted_22\n                    ]),\n                    _: 1 /* STABLE */\n                  })\n                ]),\n                _: 1 /* STABLE */\n              })\n            ])\n          ])\n        ]),\n        _: 1 /* STABLE */\n      }),\n      _createTextVNode(),\n      _createElementVNode(\"div\", _hoisted_23, [\n        _hoisted_24,\n        _createTextVNode(),\n        _hoisted_25,\n        _createTextVNode(),\n        _hoisted_26,\n        _createTextVNode(),\n        _hoisted_27,\n        _createTextVNode(),\n        _hoisted_28,\n        _createTextVNode(),\n        _createVNode(_component_box, {\n          type: \"info\",\n          seamless: \"\"\n        }, {\n          default: _withCtx(() => [\n            _hoisted_29\n          ]),\n          _: 1 /* STABLE */\n        }),\n        _createTextVNode(),\n        _hoisted_30\n      ]),\n      _createTextVNode(),\n      _createVNode(_component_overlay_source, {\n        id: \"page-nav\",\n        class: \"fixed-header-padding\",\n        \"tag-name\": \"nav\",\n        to: \"page-nav\"\n      }, {\n        default: _withCtx(() => [\n          _hoisted_31\n        ]),\n        _: 1 /* STABLE */\n      })\n    ]),\n    _createTextVNode(),\n    _hoisted_32\n  ], 64 /* STABLE_FRAGMENT */))\n}");
    var render = renderFn();
  