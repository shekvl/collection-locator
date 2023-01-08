<template lang="pug">

.d-flex.flex-column.align-start.mb-4
    h3 # Upload
.d-flex.flex-row.justify-center
    el-upload.w-50.pr-5(
        drag,
        multiple,
        auto-upload=false,
        accept=".csv",
        :http-request="() => { return; }",
        v-model:file-list="collectionList"
    )
        el-icon.el-icon--upload
            upload-filled
        .el-upload__text
            | Drop file here or&nbsp;
            em click to upload
        template(#tip)
            .el-upload__tip
                | .csv files of unlimited size

    .signpost.d-flex.flex-column.justify-space-around.pa-2.text-overline
        .d-flex.flex-row.align-center
            el-icon.el-icon--upload
                arrow-left
            | Collections
        .d-flex.flex-row.align-center.justify-end
            div Attributes
            el-icon.el-icon--upload
                arrow-right

    el-upload.w-50.pl-5(
        drag,
        multiple,
        auto-upload=false,
        accept=".csv",
        :http-request="() => { return; }",
        v-model:file-list="attributeList"
    )
        el-icon.el-icon--upload
            upload-filled
        .el-upload__text
            | Drop file here or&nbsp;
            em click to upload
        template(#tip)
            .el-upload__tip
                | .csv files of unlimited size

el-button.mt-5(
    type="primary",
    @click="sendToServer",
    size="large",
    :disabled="collectionList.length == 0 || attributeList.length == 0",
    native-type="submit",
    plain
) UPLOAD
    el-icon.el-icon--right(size="20")
        upload
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { postCollectionFiles } from "../requests/uploadReq";
import {
    UploadFilled,
    Upload,
    ArrowLeft,
    ArrowRight,
} from "@element-plus/icons-vue";

export default defineComponent({
    components: {
        UploadFilled,
        Upload,
        ArrowLeft,
        ArrowRight,
    },
    methods: {
        async sendToServer() {
            const response = await postCollectionFiles(
                this.collectionList.map((i:any) => i.raw),
                this.attributeList.map((i:any) => i.raw)
            );
            this.toast(response.success, response.message);
        },
        toast(success: boolean, message: string) {
            this.$toast.add({
                severity: success ? "success" : "error",
                summary: success ? "Upload successful" : "Upload failed",
                detail: message,
                life: 60000,
            });
        },
    },
    data() {
        return {
            collectionList: [],
            attributeList: [],
        };
    },
});
</script>

<style>
.el-upload-dragger {
    border-width: 5px;
}

.el-upload__tip {
    font-size: medium;
}

.el-upload-list__item-file-name {
    font-size: large;
}

.el-upload-dragger {
    height: 200px;
}

.signpost {
    height: 200px;
    width: 220px;
}
</style>