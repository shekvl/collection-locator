<template lang="pug">
.d-flex.flex-column.align-center
    .w-75.mb-5
        QualityModelDragAndDropUpload
    .my-5.w-75
        #info-box.d-flex.flex-column.align-start
            h3 # How To
            p To upload a quality model, it is necessary to specify a .csv file containing the definitions of quality characteristics and quality metrics.
</template>

<script setup lang="ts">
import QualityModelDragAndDropUpload from "@/components/QualityModelDragAndDropUpload.vue";
</script>


<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    i18n: {
        messages: {
            en: {},
            de: {},
        },
    },
});
</script>

<style>
#info-box img {
    width: 50em;
}

#info-box {
    line-height: 30px;
    text-align: justify;
}

#info-box h3 {
    margin-top: 30px;
}
</style>
