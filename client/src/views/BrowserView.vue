<template lang="pug">
//- div tags:
//- .tag-container
//-     n-tag(
//-         v-for="tag in state.tags",
//-         size="large",
//-         type="info",
//-         @close="removeTag",
//-         closable
//-     ) {{ tag }}

a(href="https://ohdsi.github.io/CommonDataModel/", target="_blank") Ohdsi
<br>
a(href="https://athena.ohdsi.org/search-terms/start", target="_blank") Athena

AutoComplete(
    forceSelection,
    minLength="2",
    delay="0",
    multiple,
    placeholder="search for concept..",
    v-model="selectedConcepts",
    :suggestions="filteredConcepts",
    @complete="searchConcepts($event)",
    @item-select="(event) => { addTag(event.value), log.delete(event.value), log.add(event.value); printLog(); }",
    :virtualScrollerOptions="{ items: concepts, itemSize: 40 }"
)

div recent log:
div(v-for="item in Array.from(log).reverse()") {{ item }}

AutoComplete(
    v-for="axis in axes",
    forceSelection,
    multiple,
    minLength="1",
    delay="0",
    :placeholder="axis.name",
    v-model="axis.selectedValues",
    :suggestions="axis.filteredValues",
    @complete="filter($event, axis)",
    :virtualScrollerOptions="{ items: axis.distinct_values, itemSize: 40 }"
)
</template>

//TODO tooltip: info about concept (fetch and "cache"); including link to athena with code as param in url
//link to athena...
//search bar !
//real time search with suggestion(maybe restricted by vocab etc.)  => only valid concepts addable
//vocab dropdown?

//filter result
//bookmark results
//composite descriptor selection panel
//browse all concepts (sort hierachy)
//show recent concept list

//TODO extra component for search..

//! autocomplete does not scale(?) problem displaying 10000 items.. can be limited by minlength; cdm.concept vs concept

<script setup lang="ts">
import { reactive } from "vue";
import { NTag } from "naive-ui";
import AutoComplete from "primevue/autocomplete";
import VirtualScroller from "primevue/virtualscroller";

let state = reactive({ tags: new Set(["37020651", "1003901"]) });

const removeTag = function (event: any) {
    const tag =
        event.target.parentElement.parentElement.parentElement.textContent;
    state.tags.delete(tag);
    console.log("tag removed:", tag);
};

const addTag = function (tag: string) {
    state.tags.add(tag);
    console.log("tag added:", tag);
};

const log = new Set();
function printLog() {
    console.log(log);
}
</script>

<script lang="ts">
import { defineComponent } from "vue";
import { getAllConcepts, getQueryRelationships } from "../requests/getReq";

export default defineComponent({
    async mounted() {
        const concepts = await getAllConcepts();
        this.concepts = concepts;

        const axes = await getQueryRelationships("axes", "LOINC");
        for (const axis of axes) {
            axis.selectedValues = [];
            axis.filteredValues = [];
        }
        this.axes = axes;
    },
    data() {
        return {
            selectedConcepts: ["37020651", "1003901"],
            filteredConcepts: null,
            concepts: [],
            axes: null,
        };
    },
    methods: {
        searchConcepts(event: any) {
            const filtered: any = this.concepts.filter((concept: string) => {
                return concept.startsWith(event.query);
            });
            this.filteredConcepts = filtered;
        },
        filter(event: any, object: any) {
            const filtered: any = object.distinct_values.filter(
                (value: string) => {
                    return value
                        .toLowerCase()
                        .startsWith(event.query.toLowerCase());
                }
            );
            object.filteredValues = filtered;
        },
    },
});
</script>


<style >
.n-tag {
    margin: 2px;
    size: large;
}

.tag-container {
    width: 30px;
    display: flex;
    justify-content: flex-start;
}

.p-autocomplete {
    padding-block: 3px;
    margin: 2px;
}

/* .p-autocomplete-token{
    border-radius: 5px !important;
} */
</style>