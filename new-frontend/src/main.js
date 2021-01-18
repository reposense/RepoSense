import { createApp } from 'vue';
import { dom } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import App from './app.vue';
import store from './store/store';


dom.watch();
const app = createApp(App);
app.component('font-awesome-icon', FontAwesomeIcon);
app.use(store);
app.mount('#app');
