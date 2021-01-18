import { createApp } from 'vue';
import { dom } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import hljs from 'highlight.js';
import App from './app.vue';
import store from './store/store';

import './utils/api';
import './utils/safari_date';
import './utils/load-font-awesome-icons';


dom.watch();
const app = createApp(App);
app.component('font-awesome-icon', FontAwesomeIcon);
app.use(store);
app.mount('#app');
// app.use(hljs.vuePlugin);
app.directive('hljs', {
  mounted(ele, binding) {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightBlock(element);
  },
});
