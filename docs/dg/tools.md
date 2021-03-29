<variable name="title">Appendix: Tools for front-end development</variable>
<frontmatter>
  title: "{{ title | safe }}"
</frontmatter>

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

The tools that you need to learn before working on the project include Vue.js, Pug, and SCSS. 
</div>

### Vue.js

<box type="info" seamless>

Vue.js uses JavaScript as its programming language. Before learning **Vue.js**, you may need to first get yourself familiar with JavaScript syntax first.
You can refer to the [Javascript documentation](https://devdocs.io/javascript/) to learn the basic syntax. There are plenty of other resources available and please feel free to find the resource most suitable for you.
</box>

RepoSense uses **Vue.js** (Vue2) in its front-end implementation. In particular, major user interface components, such as [summary view](report.html#summary-view-v-summary-js), [authorship view](report.html#authorship-view-v-authorship-js), and [zoom view](report.html#zoom-view-v-zoom-js), are implemented as Vue components. The corresponding source files are in `frontend/src/static/js`.

* If you are new to Vue.js, you may want to start learning by looking at the [the beginner tutorial](https://www.vuemastery.com/courses/intro-to-vue-js/). 
* You can dive deeper later by checking the [Vue.js documentation](https://vuejs.org/v2/guide/index.html) to learn about essential concepts such as component life cycle hooks, and component properties.
* It is recommended if you can work on some small projects first to gain more solid understanding of Vue.js.

<box type="warning" seamless>

The guide above uses HTML as the component template, which is not the case with RepoSense. You may wish to learn more about [Pug](#pug) and its connection with HTML.
</box>

#### Vuex

RepoSense uses **Vuex** for the state management of the Vue components.

* You can check the [Vuex guide](https://vuex.vuejs.org/guide/#the-simplest-store) to find out how Vuex can be used in a Vue project. 
* There is also a [course](https://vueschool.io/courses/vuex-for-everyone) available that will walk you through an example of creating Vue application with Vuex.

<!-- ------------------------------------------------------------------------------------------------------ -->

### Pug

RepoSense uses Pug files as the template of each Vue component. The corresponding HTML templates will later be generated from the Pug files by [spuild](https://github.com/ongspxm/spuild2) when generating the report.

<box type="info" seamless>

Since Pug is used to generate the HTML template, it is recommended that you have a basic knowledge of HTML before starting to learn Pug. Once you understand how HTML works, you can proceed to focus on how Pug is translated into HTML.
</box>

* You can refer to the [official documentation](https://pugjs.org/api/getting-started.html) or [this tutorial](https://www.youtube.com/watch?v=kt3cEjjkCZA) to learn about the syntax of pug and how it is translated into HTML.
* To get a hands-on experience, here is a [Pug to HTML converter](https://pughtml.com/). Feel free to try out a couple of examples on your own.

<!-- ------------------------------------------------------------------------------------------------------ -->

### Scss

SCSS is used for styling the Pug template. The corresponding CSS will later be generated from the SCSS files by [spuild](https://github.com/ongspxm/spuild2) when generating the report. The corresponding source files are in `frontend/src/static/css`. 

<box type="info" seamless>

It is recommended that you have a basic knowledge of CSS before starting to learn SCSS. Once you understand how CSS works, you can proceed to focus on how SCSS is translated into CSS.
</box>

* You can refer to the [style rules](https://sass-lang.com/documentation/style-rules) to learn about the similarities and differences between SCSS and CSS. 
