import { createApp, configureCompat } from 'vue';
import { dom } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import hljs from 'highlight.js';

// Need to import for side effects
import './utils/api';
import './utils/safari_date';
import './utils/load-font-awesome-icons';

import App from './app.vue';
import store from './store/store';

dom.watch();
configureCompat({ WATCH_ARRAY: false });
const app = createApp(App);

app.directive('hljs', {
  mounted: (ele, binding) => {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightBlock(element);
  },
});

app.component('font-awesome-icon', FontAwesomeIcon);
app.use(store);

app.mount('#app');
