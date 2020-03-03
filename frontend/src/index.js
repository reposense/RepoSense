import Vue from 'vue';

import app from './app.vue';
import './static/css/style.scss';

// eslint-disable-next-line no-unused-vars
import api from './static/js/api';
// eslint-disable-next-line camelcase,no-unused-vars
import safari_date from './static/js/safari_date';

window.BASE_URL = 'https://github.com';
window.REPORT_ZIP = null;
window.REPOS = {};
window.isMacintosh = navigator.platform.includes('Mac');

window.hashParams = {};
window.addHash = function addHash(newKey, newVal) {
  window.hashParams[newKey] = newVal;
};
window.removeHash = function removeHash(key) {
  delete window.hashParams[key];
};

window.encodeHash = function encodeHash() {
  const { hashParams } = window;

  window.location.hash = Object.keys(hashParams)
    .map((key) => `${key}=${encodeURIComponent(hashParams[key])}`)
    .join('&');
};

window.decodeHash = function decodeHash() {
  const hashParams = {};

  window.location.hash.slice(1).split('&')
    .forEach((param) => {
      const [key, val] = param.split('=');
      if (key) {
        try {
          hashParams[key] = decodeURIComponent(val);
        } catch (error) {
          this.userUpdated = false;
          this.isLoading = false;
        }
      }
    });
  window.hashParams = hashParams;
};

/* global Vue hljs */
Vue.directive('hljs', {
  inserted(ele, binding) {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightBlock(element);
  },
});

window.app = new Vue(app);
