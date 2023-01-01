<template lang="pug">
div asdf
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