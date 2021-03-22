import Vue from 'vue';
import { dom } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import hljs from 'highlight.js';
import App from './app.vue';
import store from './store/store';

import './utils/api';
import './utils/safari_date';
import './utils/load-font-awesome-icons';


dom.watch();
Vue.component('font-awesome-icon', FontAwesomeIcon);
// app.use(hljs.vuePlugin);
Vue.directive('hljs', {
  mounted(ele, binding) {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightBlock(element);
  },
});

new Vue({
  render: (h) => h(App),
  store,
}).$mount('#app');
