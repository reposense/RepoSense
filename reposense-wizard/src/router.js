import VueRouter from 'vue-router';
import Home from './Home.vue';
import Auth from './Auth.vue';

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
