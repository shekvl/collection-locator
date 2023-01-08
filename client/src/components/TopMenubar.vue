<template lang="pug">
Menubar#menubar(:model="items")
    template(#start)
        #logo(@click="go") Collection Locator
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
import SpeedDial from "primevue/speeddial";
import ContextMenu from "primevue/contextmenu";
import { useRouter } from "vue-router";
const router = useRouter();

const go = function () {
    router.push({ path: "/" });
};

const items = [
    {
        label: "Browser",
        icon: "pi pi-search",
        to: "/browser",
    },
    {
        label: "Upload",
        icon: "pi pi-upload",
        to: "/fileupload",
    },
    {
        label: "About",
        icon: "pi pi-info-circle",
        to: "/about",
    },
    {
        label: "Profile",
        icon: "pi pi-user",
        items: [
            {
                label: "My Collections",
                icon: "pi pi-folder-open",
            },
            {
                label: "Bookmarks",
                icon: "pi pi-star-fill",
            },
            {
                label: "Settings",
                icon: "pi pi-cog",
            },
            {
                separator: true,
            },
            {
                label: "logout",
                icon: "pi pi-sign-out",
            },
        ],
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
                // { //not yet translated
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
.p-speeddial {
    position: absolute;
    right: 50px;
}

#logo {
    font-size: larger;
    margin-inline-start: 10px;
    margin-inline-end: 10px;
    padding-inline: 10px;
    padding-block: 2px;
    font-weight: 600;
    /* border: solid 2px var(--primary-color);
    border-radius: 5px; */
    /* text-shadow: var(--primary-color) -0.5px -0.5px; */
    cursor: pointer;
}

/* #menubar {
    justify-content: flex-end;
    padding-right: 20px;
} */

/*TODO: put profile dropdown menu in menubar on right side */
/* #innerMenu {
    border: none;
    padding: unset;
    padding-right: 30px;
    direction: rtl;
} */
</style>