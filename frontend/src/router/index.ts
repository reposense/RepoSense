import {
  createRouter, createWebHashHistory, Router, RouteRecordRaw,
} from 'vue-router';
import Home from '../views/c-home.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: Home,
  },
];

const router: Router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
