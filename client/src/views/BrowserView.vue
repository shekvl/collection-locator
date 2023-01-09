<template lang="pug">
.px-5
    .d-flex
        .w-50
            //- .d-flex.justify-start
                Dropdown(
                    v-model="selectedOntology",
                    :options="ontologies",
                    placeholder="Select Vocabulary",
                    :virtualScrollerOptions="{ items: ontologies, itemSize: 40 }"
                )
        .links.w-50.d-flex
            .d-flex.justify-end.align-center.w-100
                //- ExternalLink(href="https://ohdsi.github.io/CommonDataModel/", text="Ohdsi")
                ExternalLink(href="https://athena.ohdsi.org/search-terms/start", text="Athena")

    .d-flex
        TabView.w-75(lazy)
            TabPanel(header="Concepts")
                .d-flex.w-66
                    .d-flex
                        AutoComplete(
                            forceSelection,
                            minLength="1",
                            delay="0",
                            multiple,
                            placeholder="search for concept..",
                            v-model="selectedConcepts",
                            :suggestions="filteredConcepts",
                            @complete="searchConcepts($event)",
                            @item-select="(event) => { mostRecentConcepts.delete(event.value), mostRecentConcepts.add(event.value); }",
                            :virtualScrollerOptions="{ items: concepts, itemSize: 40 }"
                        )
                        Button#search-button(
                            type="button",
                            label="Search",
                            size="small",
                            iconPos="right",
                            icon="pi pi-search",
                            :loading="false",
                            @click="doQueryAny()"
                        )
                        SelectButton(v-model="selectedSearchMode" :options="SEARCH_MODE" )
                        //get children
                        //any -> set of children
                        //all -> all of any of children

                #most-recent-panel.d-flex.w-33.flex-column.align-start.mx-5.px-4.py-2
                    div most recent concepts:
                    .most-recents(
                        v-for="item in Array.from(mostRecentConcepts).reverse()",
                        @click="selectedConcepts.push(item)"
                    ) {{ item }}

            TabPanel(header="Axes")
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

    CollectionTable(:collections="resultCollections", :attributes="resultAttributes")

    ScrollTop
</template>


//TODO tooltip: info about concept (fetch and "cache"); including link to athena with code as param in url
//real time search with suggestion(maybe restricted by vocab etc.)  => only valid concepts addable

//bookmark results
//!composite descriptor selection panel
//browse all concepts in use (sort hierachy)

//show tabs according to query_relationships

//TODO extra COMPONENT for search..

//! autocomplete does not scale(?) problem displaying 10000 items.. can be limited by minlength; cdm.concept vs concept


<script setup lang="ts">
import { ref } from "vue";
import AutoComplete from "primevue/autocomplete";
import VirtualScroller from "primevue/virtualscroller";
import ScrollTop from "primevue/scrolltop";
import TabView from "primevue/tabview";
import TabPanel from "primevue/tabpanel";
import Dropdown from "primevue/dropdown";
import Button from "primevue/button";
import ExternalLink from "../components/ExternalLink.vue";
import SelectButton from "primevue/selectbutton";
import CollectionTable from "../components/CollectionTable.vue"

const mostRecentConcepts = new Set(["36033638", "45458440"]);
</script>

<script lang="ts">
import { defineComponent } from "vue";
import {
    getAllConcepts,
    getOntologies,
    getQueryRelationships,
    queryAny,
} from "../requests/getReq";
// import { FilterMatchMode, FilterOperator } from "primevue/api";

// const enum COLUMNTYPE {
//     TEXT = "text",
//     NUMERIC = "numeric",
//     DATE = "date",
// }

const enum SEARCH_MODE {
    ANY = "ANY",
    ALL = "ALL",
}

export default defineComponent({
    async mounted() {
        const ontologies = await getOntologies();
        this.ontologies = ontologies.map((o: any) => o.vocabulary_id);

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
            selectedConcepts: [],
            filteredConcepts: null,
            concepts: [],
            axes: null,
            ontologies: [],
            selectedOntology: null,
            resultCollections: [],
            resultAttributes: [],
            selectedSearchMode: SEARCH_MODE.ANY,
        };
    },
    methods: {
        async doQueryAny() {
            //TODO start loading (show loading process if fetching from db takes some time)
            const result: any = await queryAny(
                Object.values(this.selectedConcepts)
            );
            this.resultCollections = result.data.collections;
            this.resultAttributes = result.data.attributes;
            //TODO set loading finished
        },
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


<style>
.p-tabview-panel {
    display: flex;
    justify-content: flex-start;
}

.p-dropdown {
    min-width: 12rem;
    width: fit-content;
}

.p-dropdown-label {
    text-align: start;
}
</style>

<style scoped>
a {
    margin-right: 20px;
}

.most-recents {
    color: var(--primary-text);
    cursor: pointer;
    padding-inline: 10px;
    padding-block: 5px;
    border-radius: 8px;
}

.most-recents:hover {
    background-color: rgba(213, 213, 213, 0.541);
}

#search-button {
    margin: 2px;
    min-width: fit-content;
    height: 42px;
}

.p-autocomplete {
    margin: 2px;
    height: fit-content;
}

#most-recent-panel {
    width: auto;
    height: 200px;
    border: solid 1px #e6e9ec;
    border-radius: 8px;
    overflow-y: auto;
    min-width: fit-content;
}
</style>