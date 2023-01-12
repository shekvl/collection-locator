<template lang="pug">
.px-5
    .d-flex
        .w-75
            AthenaSearch(:vocabularies="ontologies", @conceptIdSelected="(value) => selectConceptOfAttribute(value)")
        .links.w-25.d-flex
            .d-flex.justify-end.align-start.w-100
                //- ExternalLink(href="https://ohdsi.github.io/CommonDataModel/", text="Ohdsi")
                ExternalLink(
                    href="https://github.com/OHDSI/Athena",
                    text="Athena - Search Docs"
                )
                ExternalLink(
                    href="https://athena.ohdsi.org/search-terms/start",
                    text="Athena"
                )

    .d-flex
        TabView.w-100(lazy)
            TabPanel(header="Concepts")
                .d-flex.w-50
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
                            @item-select="(event) => addToMostRecentConcepts(event.value)",
                            :virtualScrollerOptions="{ items: concepts, itemSize: 40 }"
                        )
                        Button#search-button(
                            type="button",
                            label="Search",
                            size="small",
                            iconPos="right",
                            icon="pi pi-search",
                            :loading="false",
                            @click="selectedSearchMode === SEARCH_MODE.ANY ? doQueryAny(selectedConcepts) : doQueryAll(selectedConcepts)"
                        )
                        SelectButton#search-mode.ml-2(
                            v-model="selectedSearchMode",
                            :options="SEARCH_MODE",
                            :unselectable="false"
                        )
                        //get children
                        //any -> set of children
                        //all -> all of any of children

                .selection-panel.d-flex.w-25.flex-column.align-start.mx-5.px-4.py-2
                    div most recent concepts:
                    .most-recents(
                        v-for="item in Array.from(mostRecentConcepts).reverse()",
                        @click="selectConcept(item)"
                    ) {{ item }}
                .d-flex.flex-column.justify-start.w-25.mx-5
                    .selection-panel.d-flex.flex-column.align-start.px-4.py-2
                        div composite descriptors:
                    Button.mt-2(
                        label="create",
                        icon="pi pi-plus",
                        iconPos="right",
                        style="width: fit-content"
                    )

            TabPanel(header="Relationships")
                .d-flex.flex-column
                    .d-flex.justify-start
                        Dropdown(
                            v-model="selectedOntology",
                            :options="ontologies",
                            placeholder="Select Vocabulary",
                            :virtualScrollerOptions="{ items: ontologies, itemSize: 40 }"
                        )
                    .d-flex.justify-start.align-center
                        div axes:
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
                            :virtualScrollerOptions="{ items: axis.value_names, itemSize: 40 }"
                        )
                    Button#search-button(
                        type="button",
                        label="Search",
                        size="small",
                        iconPos="right",
                        icon="pi pi-search",
                        :loading="false",
                        @click="doQueryRelationship()"
                    )
    CollectionTable(
        :collections="resultCollections",
        :attributes="resultAttributes",
        @conceptIdSelected="(value) => selectConceptOfAttribute(value)"
    )

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
import CollectionTable from "../components/CollectionTable.vue";
import AthenaSearch from "../components/AthenaSearch.vue"
</script>

<script lang="ts">
import { defineComponent } from "vue";
import {
    getAllConcepts,
    getOntologies,
    getQueryRelationships,
    queryRelationships,
    queryAny,
    queryAll,
} from "../requests/getReq";

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
            selectedConcepts: ["36033638"],
            filteredConcepts: null,
            concepts: [],
            axes: [],
            ontologies: [],
            selectedOntology: "LOINC",
            resultCollections: [],
            resultAttributes: [],
            selectedSearchMode: SEARCH_MODE.ANY,
            mostRecentConcepts: new Set([
                "36033638",
                "45458440",
                "45876191", //descendent (36033638)
                "3667069", //maps to (940658)
            ]),
        };
    },
    methods: {
        selectConceptOfAttribute(concept_id: string) {
            this.selectConcept(concept_id);
            this.addToMostRecentConcepts(concept_id);
        },
        selectConcept(concept_id: string) {
            this.selectedConcepts.push(concept_id);
        },
        addToMostRecentConcepts(concept_id: string) {
            this.mostRecentConcepts.delete(concept_id);
            this.mostRecentConcepts.add(concept_id);
        },
        async doQueryAll(concept_ids: []) {
            //TODO start loading (show loading process if fetching from db takes some time)
            const result: any = await queryAll(Object.values(concept_ids));
            this.resultCollections = result.data.collections || [];
            this.resultAttributes = result.data.attributes || [];
            //TODO set loading finished
        },
        async doQueryAny(concept_ids: []) {
            //TODO start loading (show loading process if fetching from db takes some time)
            const result: any = await queryAny(Object.values(concept_ids));
            this.resultCollections = result.data.collections || [];
            this.resultAttributes = result.data.attributes || [];
            //TODO set loading finished
        },
        async doQueryRelationship() {
            const relationships = [];
            for (const a of this.axes) {
                //TODO:generatlize
                const axis: any = a;
                if (axis.selectedValues.length > 0) {
                    relationships.push({
                        relationship_id: axis.relationship_id,
                        concept_names: axis.selectedValues,
                    });
                }
            }

            const result: any = await queryRelationships(
                this.selectedOntology,
                relationships
            );
            this.doQueryAny(result.data);
        },
        searchConcepts(event: any) {
            const filtered: any = this.concepts.filter((concept: string) => {
                return concept.startsWith(event.query);
            });
            this.filteredConcepts = filtered;
        },
        filter(event: any, object: any) {
            const filtered: any = object.value_names.filter((value: string) => {
                return value
                    .toLowerCase()
                    .startsWith(event.query.toLowerCase());
            });
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

#search-button,
#search-mode {
    margin: 2px;
    min-width: fit-content;
    height: 42px;
}

.p-autocomplete {
    margin: 2px;
    height: fit-content;
}

.selection-panel {
    width: auto;
    height: 200px;
    border: solid 1px #e6e9ec;
    border-radius: 8px;
    overflow-y: auto;
    min-width: fit-content;
}
</style>