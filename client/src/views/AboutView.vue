<template lang="pug">
<div class="about">
    h1 This is an about page {{ $t('a.b') }}

    el-tooltip.box-item(
        effect="dark",
        content="Top Center prompts info",
        placement="top"
    )
        el-button top

    el-breadcrumb(:separator-icon="ArrowRight")
        el-breadcrumb-item(:to="{ path: '/' }") homepage
        el-breadcrumb-item promotion management
        el-breadcrumb-item promotion list
        el-breadcrumb-item promotion detail

    el-button(:plain="true", @click="open3(), toast()") warning

    .example-pagination-block
        .example-demonstration When you have few pages
        el-pagination(layout="prev, pager, next", :total="50")

        it-button(@click="confirmModal = true", type="danger") Delete account
        it-modal(v-model="confirmModal")
            template(#header)
                h3 Delete account
            template(#body)
                | Account will be deleted permanently, please confirm
            template(#actions)
                it-button(@click="confirmModal = false") Cancel
                it-button(
                    type="danger",
                    @click=`
        $Message.success({ text: 'Account deleted' }),
          (confirmModal = false)
      `
                ) Delete

        it-button(@click="defaultModal = true") Modal
        it-modal(v-model="defaultModal")
            template(#image)
                img(src="https://images.unsplash.com/photo-1549277513-f1b32fe1f8f5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1950&q=80")
            template(#header)
                h3(style="margin: 0") Header
            template(#body)
                p Modal body. You can put here whatever you want: forms, images, text
            template(#actions)
                it-button(type="primary", @click="toast(), (defaultModal = false)") Got it

    card(style="width: 25rem; margin-bottom: 2em")
        template(#title)
            | Simple Card
        template(#content)
            p
                | Lorem ipsum dolor sit amet, consectetur adipisicing elit. Inventore sed consequuntur error repudiandae numquam deserunt
                | quisquam repellat libero asperiores earum nam nobis, culpa ratione quam perferendis esse, cupiditate neque quas!
    card(style="width: 25em")
        template(#header)
            img(
                src="https://www.primefaces.org/wp-content/uploads/2020/02/primefacesorg-primevue-2020.png",
                style="height: 15rem"
            )
        template(#title)
            | Advanced Card
        template(#subtitle)
            | Card subtitle
        template(#content)
            p
                | Lorem ipsum dolor sit amet, consectetur adipisicing elit. Inventore sed consequuntur error repudiandae numquam deserunt
                | quisquam repellat libero asperiores earum nam nobis, culpa ratione quam perferendis esse, cupiditate neque quas!
        template(#footer)
            button(icon="pi pi-check", label="Save")
                button.p-button-secondary(
                    icon="pi pi-times",
                    label="Cancel",
                    style="margin-left: 0.5em"
                )

    Toast(position="bottom-left", group="about")
</div>
</template>


<script lang="ts" setup>
import { ArrowRight } from "@element-plus/icons-vue";
</script>

<script lang="ts">
import { defineComponent } from "vue";
import { ElMessage } from "element-plus";
import Card from "primevue/card";
import Toast from "primevue/toast"

const open3 = () => {
    ElMessage({
        showClose: true,
        message: "Warning, this is a warning message.",
        type: "error",
        grouping: true,
        duration: 5000,
    });
};

export default defineComponent({
    name: "HomeView",
    components: {},
    methods: {
        toast() {
            this.$toast.add({
                severity: "info",
                summary: "Success Message",
                detail: "yoo",
                group: "about",
                life: 30000,
            });
        },
    },
    data() {
        return {
            confirmModal: false,
            defaultModal: false,
        };
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

<style>
.el-message__content {
    font-size: 20px;
}
</style>
