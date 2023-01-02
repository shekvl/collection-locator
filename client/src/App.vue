<template lang="pug">
TopMenubar.mb-5

router-view
Toast(position="bottom-left")

</template>


<script setup lang="ts">
import Toast from "primevue/toast";
import TopMenubar from "./components/TopMenubar.vue";
</script>


<style>
#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
}

nav {
    padding: 30px;
}

nav a {
    font-weight: bold;
    color: #2c3e50;
}

nav a.router-link-exact-active {
    color: #b95e42;
}
</style>
