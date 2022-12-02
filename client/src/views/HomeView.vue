<template>
    <div class="home">{{ $t("a.b") }}</div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    name: "HomeView",
    components: {},
    i18n: {
        messages: {
            en: {
                a: {
                    b: "english",
                },
            },
            de: {
                a: {
                    b: "deutsch",
                },
            },
        },
    },
});
</script>
