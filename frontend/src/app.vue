<template lang="pug">
#app
  router-view
</template>

<script lang='ts'>
import { defineComponent } from 'vue';

const app = defineComponent({
  name: 'app',
});

export default app;
</script>

<style lang="scss">
@import './styles/style.scss';
@import './styles/panels.scss';
</style>
