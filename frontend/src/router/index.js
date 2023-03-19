import { createRouter, createWebHashHistory } from 'vue-router';
import Home from '../views/c-home.vue';
import Widget from '../views/c-widget.vue';

const routes = [
  {
    path: '/',
    component: Home,
  },
  {
    path: '/widget',
    component: Widget,
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    // component: () => import(/* webpackChunkName: "about" */ '../views/c-about.vue'),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
