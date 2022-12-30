<template lang="pug">
nav
    router-link(to="/") Home
    router-link(to="/about") About

    //- https://ohdsi.github.io/CommonDataModel/

router-view
Toast(position="bottom-left")
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Toast from "primevue/toast";

export default defineComponent({
    components:{Toast}
})
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
    color: #42b983;
}
</style>
