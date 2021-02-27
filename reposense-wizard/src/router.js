import VueRouter from 'vue-router';
import Home from './views/Home.vue';
import Auth from './views/Auth.vue';

const routes = [
    {
      path: '/',
      component: Home,
    },
    {
      path: '/authenticating',
      component: Auth,
    },
];

const router = new VueRouter({
  mode: 'history',
  routes,
});

export default router;
