<template lang="pug">
.d-flex.align-center.mb-2
    span.p-input-icon-right
        i
            span.pi.pi-times(@click="clearAthenaQuery").mr-3
            span.pi.pi-search(@click="doAthenaQuery")
        InputText(
            type="text",
            v-model="athenaSearchString",
            placeholder="Search Athena..",
            @keyup.enter="doAthenaQuery",
            v-tooltip="'Browse Athena for Concept IDs by entering Keyword into the search bar'"
        )
    .d-flex.w-100.mx-5
        ExternalLink.mr-3(
            href="https://github.com/OHDSI/Athena",
            text="Athena - Search Docs"
        )
        ExternalLink(
            href="https://athena.ohdsi.org/search-terms/start",
            text="Athena"
        )

AthenaTable.my-2(
    v-if="athenaConcepts",
    :concepts="athenaConcepts",
    @conceptIdSelected="(value) => $emit('conceptIdSelected', value)",
    :isLoading="isLoading"
)
</template>

<script setup lang="ts">
import ExternalLink from "./ExternalLink.vue";
import InputText from "primevue/inputtext";
import AthenaTable from "./AthenaTable.vue";
</script>

<script lang="ts">
import { defineComponent } from "vue";
import * as athena from "../requests/athena";

export default defineComponent({
    props: ["vocabularies"],
    data() {
        return {
            athenaConcepts: undefined,
            athenaSearchString: "",
            isLoading: false,
        };
    },
    methods: {
        async clearAthenaQuery() {
            this.athenaConcepts = undefined;
            this.athenaSearchString = "";
            return;
        },
        async doAthenaQuery() {
            if (!this.athenaSearchString) {
                this.athenaConcepts = undefined;
                return;
            }

            this.isLoading = true;

            const result: any = await athena.queryAthena({
                pageSize: 10,
                page: 1,
                query: this.athenaSearchString,
                domain: undefined,
                standardConcept: undefined,
                conceptClass: undefined,
                vocabulary: this.vocabularies,
                invalidReason: undefined,
            });

            this.athenaConcepts = result.data.content;

            this.isLoading = false;
        },
    },
});
</script>


<style scoped>
i {
    cursor: pointer;
}

input {
    width: 440px;
}
</style>
