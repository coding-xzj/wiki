<template>
  <the-header></the-header>
  <div class="main">
    <router-view />
  </div>
</template>

<script lang="ts" setup>
import TheHeader from "@/components/the-header.vue";
</script>

<style>
html,
body,
#app {
  width: 100%;
  height: 100%;
}
.main {
  padding-top: 64px;
  height: 100%;
}
.ant-layout {
  height: 100%;
}
</style>