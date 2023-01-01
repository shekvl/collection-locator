<template lang="pug">
Menubar#menubar(:model="items")
    template(#start)
        #label(@click="go") Collection Locator
</template>


<script lang="ts" setup>
import Menubar from "primevue/menubar";
import { useRouter } from "vue-router";
const router = useRouter();

const go = function () {
    router.push({ path: "/" });
};

const items = [
    {
        label: "Browser",
        icon: "pi pi-fw pi-search",
        to: "/browser",
    },
    {
        label: "Upload",
        icon: "pi pi-fw pi-upload",
        to: "/upload",
    },
    {
        label: "About",
        icon: "pi pi-fw pi-info-circle",
        to: "/about",
    },
    {
        label: "Profile",
        icon: "pi pi-fw pi-user",
        items: [
            {
                label: "My Collections",
                icon: "pi pi-fw pi-folder-open",
            },
            {
                label: "Bookmarks",
                icon: "pi pi-fw pi-star-fill",
            },
            {
                label: "Settings",
                icon: "pi pi-fw pi-cog",
            },
            {
                separator: true,
            },
            {
                label: "logout",
                icon: "pi pi-fw pi-sign-out",
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
});
</script>

<style>
#label {
    font-size: larger;
    margin-inline-start: 10px;
    margin-inline-end: 10px;
    padding-inline: 10px;
    padding-block: 2px;
    font-weight: 600;
    /* border: solid 2px var(--primary-color);
    border-radius: 5px; */
    text-shadow: rgb(126, 126, 126) 0.5px 0.5px;
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