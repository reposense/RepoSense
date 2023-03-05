import { createApp, DirectiveBinding } from 'vue';
import { dom } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { ObserveVisibility } from 'vue-observe-visibility';
import hljs from 'highlight.js';
import 'muicss/dist/css/mui.min.css';
import 'normalize.css/normalize.css';
import 'vue-loading-overlay/dist/vue-loading.css';
import '@fontsource/titillium-web';

// Need to import for side effects
import './utils/api';
import './utils/safari_date';
import './utils/load-font-awesome-icons';

import App from './app.vue';
import store from './store/store';

dom.watch();

const app = createApp(App);

app.directive('hljs', {
  mounted: (ele: any, binding: DirectiveBinding) => {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightElement(element);
  },
});

app.directive('observe-visibility', {
  beforeMount: (el, binding, vnode) => {
    (vnode as any).context = binding.instance;
    ObserveVisibility.bind(el, binding, vnode);
  },
  updated: ObserveVisibility.update,
  unmounted: ObserveVisibility.unbind,
});

app.component('font-awesome-icon', FontAwesomeIcon);
app.use(store);

app.mount('#app');
