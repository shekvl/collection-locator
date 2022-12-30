<template lang="pug">
.home(@click="toast()") {{ $t('a.b') }}
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    name: "HomeView",
    components: {},
     methods: {
        toast() {
            this.$toast.add({
                severity: "info",
                summary: "Success Message",
                detail: "Message Content",
                life: 30000,
            });
            console.log("dick", this.$toast)
        },
    },
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
