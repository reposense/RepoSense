import Vue from 'vue';
import Vuex from 'vuex';

import githubStore from './githubStore';

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    githubStore,
  },
});

export default store;
