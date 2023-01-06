<template lang="pug">
a(:href="href", target="_blank")
    span {{ text }}&nbsp;
        i.pi.pi-external-link
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
    color: var(--primary-color);
}
</style>