<template lang="pug">
.d-flex.flex-column.align-center
    .w-75.mb-5
        DragAndDropUpload

    .my-5.w-75
        #info-box.d-flex.flex-column.align-start.mb-4
            h3 # How To

            p(v-html="$t('howto.p1')")

            h4 Annotation veranschaulichung
            p(v-html="$t('howto.p2')")


            p(v-html="$t('howto.p3')")
            p(v-html="$t('howto.p4')")

            img(:src="collections01")
            img(:src="collections02")

            p(v-html="$t('howto.p5')")


            img(:src="collectionsCombined")

            div &nbsp;
            div open questions:
            div how are quality characteristics defined?
            div problem incomplete mapping between vocabs

        // problem null values..
        // problem multiple annotation..

        //multip annotation
        //empty fields (required and not required)
        //on or multiple csvs
        //different vocabularies
        //show with arrows how collumns are annotated (dont have to annotate all fields)
        //covid example?
        //biobank related content
        //parent group (body measurement)
</template>

<script setup lang="ts">
import DragAndDropUpload from "../components/DragAndDropUpload.vue";
import collections01 from "../assets/collections01.png";
import collections02 from "../assets/collections02.png";
import collectionsCombined from "../assets/collections_combined.png";
</script>


<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    i18n: {
        messages: {
            en: {
                howto: {
                    p1: "To upload a collection, two kinds of spreadsheets are needed. A collections.csv and a attributes.csv file.",
                    p2:"",
                    p3:"Spezification:",
                    p4:"Upload collections separately, each in one file",
                    p5:"Upload collections and attributes combined, each in one spreadsheet",
                },
            },
            de: {},
        },
    },
});
</script>

<style>
img {
    /* scale:80%; */
    width: 800px;
}

#info-box {
    line-height: 40px;
}
</style>