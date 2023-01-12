<template lang="pug">
.d-flex.align-center
    span.p-input-icon-right
        i.pi.pi-search(@click="doAthenaQuery")
        InputText(
            type="text",
            v-model="athenaSearchString",
            placeholder="Enter Keywords",
            @keyup.enter="doAthenaQuery"
        )
    .ml-3 Search for Concepts in Athena

AthenaTable.my-2(v-if="athenaConcepts", :concepts="athenaConcepts", @conceptIdSelected="(value) => $emit('conceptIdSelected', value)")
</template>

<script setup lang="ts">
import InputText from "primevue/inputtext";
import AthenaTable from "./AthenaTable.vue";
</script>

<script lang="ts">
import { defineComponent } from "vue";
import * as athena from "../requests/athena";

export default defineComponent({
    props:['vocabularies'],
    data() {
        return {
            athenaConcepts: undefined,
            athenaSearchString: "",
        };
    },
    methods: {
        async doAthenaQuery() {
            const result: any = await athena.queryAthena({
                pageSize: 15,
                query: this.athenaSearchString,
                domain: undefined,
                standardConcept: undefined,
                conceptClass: undefined,
                vocabulary: this.vocabularies,
                invalidReason: undefined,
            });

            this.athenaConcepts = result.data.content;
            console.log(result, result.content);
        },
    },
});
</script>


<style scoped>
i {
    cursor: pointer;
}
</style>