import { createRouter, createWebHashHistory } from 'vue-router';
import Home from '../views/c-home.vue';

const routes = [
  {
    path: '/',
    component: Home,
  },
  {
    path: '/widget',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../views/c-widget-home.vue'),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
