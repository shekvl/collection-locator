<template lang="pug">
Menubar#menubar(:model="items")
    template(#start)
        #logo.d-flex(@click="goToHome")
            img(src="favicon-32x32.png")
            .ml-2 Collection Locator
    template(#end)
        Button.p-button-rounded.p-button-text(
            icon="pi pi-language",
            @click="openLangContextMenu"
        )
        ContextMenu(ref="langContextMenu", :model="langList")
</template>


<script lang="ts" setup>
import Menubar from "primevue/menubar";
import Button from "primevue/button";
import ContextMenu from "primevue/contextmenu";
import { useRouter } from "vue-router";
const router = useRouter();

const goToHome = function () {
    router.push({ path: "/" });
};

const items = [
    {
        label: "Search",
        icon: "pi pi-search",
        to: "/browser",
    },
    {
      label: "Upload Quality Model",
      icon: "pi pi-upload",
      to: "/qualitymodelupload",
    },
    {
        label: "Upload Collection Data",
        icon: "pi pi-upload",
        to: "/fileupload",
    },
];

const i18n = {
    messages: {
        en: {},
        de: {},
    },
};
</script>


<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    name: "TopMenubar",
    methods: {
        openLangContextMenu(event: any) {
            const langContextMenu: any = this.$refs.langContextMenu;
            langContextMenu.show(event);
        },
    },
    data() {
        return {
            langList: [
                {
                    label: "English",
                    command: () => {
                        const i18n: any = this.$i18n;
                        i18n.locale = "en";
                    },
                },
                // { //not yet translated //TODO
                //     label: "Deutsch",
                //     command: () => {
                //         const i18n: any = this.$i18n;
                //         i18n.locale = "de";
                //     },
                // },
            ],
        };
    },
});
</script>

<style>
#logo {
    font-size: larger;
    padding-inline: 10px;
    padding-block: 2px;
    font-weight: 600;
    cursor: pointer;
}
</style>
