<template>
  <a-layout>
    <the-header></the-header>
    <div class="main">
      <router-view></router-view>
    </div>
    <the-footer></the-footer>
  </a-layout>
</template>

<script lang="ts" setup>
import TheHeader from "@/components/the-header.vue";
import TheFooter from "@/components/the-footer.vue";
</script>

<style>
</style>
