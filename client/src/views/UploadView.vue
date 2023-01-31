<template lang="pug">
.d-flex.flex-column.align-center
    .w-75.mb-5
        DragAndDropUpload
        //TODO: get person and institution data (create new person/institution or select from existing ones)
    .my-5.w-75
        #info-box.d-flex.flex-column.align-start
            h3 # How To
            p To upload a collection, two kinds of spreadsheets are needed. A collections.csv file containing metadata about the specific collection and an attributes.csv file containing the annotated table fields with associated quality metadata.

            //- h3 Annotation
            //- p Show graphically how attributes/column headers of the original dataset are annotated and make up the attribute csv...
            //show with arrows how collumns are annotated (dont have to annotate all fields)

            //- h3 Spezification
            //- div ...
            //multiple annotations (single attribute can be annotated with multiple codes)
            //parent concepts (attribute can be annotated with parent concept e.g. body measurement instead of body hight)
            //empty fields (quality metadata is not required)
            //on or multiple CSVs (upload as batch or separated)
            //different vocabularies (annotation codes don't have to be of same vocabulary)

            h3 Example
            h4 Upload several collection and several attribute files.
            img(src="../assets/collections01.png")
            img(src="../assets/collections02.png")

            h4 Upload one file with all the collections and one with all the attributes
            img(src="../assets/collections_combined.png")

            //- h3 Quality Metadata Definition
            //- div ...

</template>

<script setup lang="ts">
import DragAndDropUpload from "../components/DragAndDropUpload.vue";
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
img {
    width: 100%;
}

#info-box {
    line-height: 30px;
    text-align: justify;
}

#info-box h3 {
    margin-top: 30px;
}
</style>