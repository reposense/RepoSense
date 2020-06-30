{% set dg_sitenav_items = [
  {name: "Contributing", link: "dg/index.html"},
  {name: "Setting up", link: "dg/settingUp.html"},
  {name: "Workflow", link: "dg/workflow.html"},
  {name: "Design and implementation"},
  {level: 2, name: "Architecture", link: "dg/architecture.html"},
  {level: 2, name: "HTML report", link: "dg/report.html"},
  {name: "Project management", link: "dg/projectManagement.html"},
  {name: "Appendices"},
  {level: 2, name: "Style guides", link: "dg/styleGuides.html"}
]
%}

<span class="lead">****DEVELOPER GUIDE****</span>

<navigation>

{% from "scripts/macros.njk" import show_sitenav_items with context %}

{{ show_sitenav_items(dg_sitenav_items) }}

</navigation>
