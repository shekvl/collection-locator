<template>
    <div class="about">
        <h1>This is an about page {{ $t("a.b") }}</h1>
    </div>
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
