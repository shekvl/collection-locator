<template lang="pug">
a(:href="href")
    span {{ text }}
</template>


<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    props: {
        href: String,
        text: String,
    },
});
</script>

<style scoped>
a {
    text-decoration: none;
    color:var(--primary-color)
}
</style>