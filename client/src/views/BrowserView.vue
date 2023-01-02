<template lang="pug">
div tags:
.tag-container
    n-tag(
        v-for="tag in state.tags",
        size="small",
        type="info",
        @close="removeTag",
        closable
    ) {{ tag }}

a(href="https://ohdsi.github.io/CommonDataModel/", target="_blank") Ohdsi
<br>
a(href="https://athena.ohdsi.org/search-terms/start", target="_blank") Athena


AutoComplete(
    forceSelection,
    minLength="1",
    delay="0",
    placeholder="search for concept..",
    v-model="selectedConcepts",
    :suggestions="filteredConcepts",
    @complete="searchCountry($event)"
)
</template>

//TODO tooltip: info about concept (fetch and "cache"); including link to athena with code as param in url
//link to athena...
//search bar !
//real time search with suggestion(maybe restricted by vocab etc.)  => only valid concepts addable
//vocab dropdown?

//filter result
//bookmark results


//TODO extra component for search..

//! autocomplete does not scale(?) problem displaying 10000 items.. can be limited by minlength; cdm.concept vs concept

<script setup lang="ts">
import { reactive } from "vue";
import { NTag } from "naive-ui";
import AutoComplete from "primevue/autocomplete";

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
</script>

<script lang="ts">
import { defineComponent } from "vue";
import { getAllConcepts } from "../requests/getReq";

export default defineComponent({
    async mounted() {
        const result = await getAllConcepts();
        // console.log("mount", result);
        this.concepts = result;
    },
    data() {
        return {
            selectedConcepts: null,
            filteredConcepts: null,
            concepts: [],
        };
    },
    methods: {
        searchCountry(event: any) {
            const filtered: any = this.concepts.filter((concept: string) => {
                return concept.startsWith(event.query);
            });
            // console.log(filtered)
            this.filteredConcepts = filtered;
        },
    },
});
</script>


<style scoped >
.n-tag {
    margin: 2px;
}

.tag-container {
    width: 30px;
    display: flex;
    justify-content: flex-start;
}
</style>