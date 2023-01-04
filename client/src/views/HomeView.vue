<template lang="pug">
DeferredContent
    .home(@click="toast()") {{ $t('a.b') }}
    .test {{ a }}
</template>

<script setup lang="ts">
import DeferredContent from 'primevue/deferredcontent';
</script>

<script lang="ts">
import { defineComponent } from "vue";
import { test } from "../requests/uploadReq";

export default defineComponent({
    name: "HomeView",
    components: {},
    data() {
        return {
            a: "asdf",
        };
    },
    async created() {
        this.a = await test()
    },
    methods: {
        toast() {
            this.$toast.add({
                severity: "info",
                summary: "Success Message",
                detail: "Message Content",
                life: 30000,
            });
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
