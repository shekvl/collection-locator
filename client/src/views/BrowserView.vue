<template lang="pug">
.px-5
    .d-flex
        TabView.w-100(lazy)
            TabPanel(header="Concepts")
                .d-flex.w-75
                    .d-flex.flex-column.w-100
                        .d-flex
                            //- TODO: Currently, only annotated concepts can be entered directly, because select is filled with those concepts as option. It could be changed so it autocompletes all supported omop concepts by fetching all cdm concepts instead of just the annotated ones.
                            //- autoComplete => real time search with suggestion
                            //- forceSelection  => only valid concepts addable
                            //- autocomplete does not scale. problem displaying 10000 items.. can be limited by minlength; cdm.concept vs concept
                            AutoComplete.mx-0(
                                forceSelection,
                                minLength="1",
                                delay="0",
                                multiple,
                                placeholder="Search for Concept..",
                                v-model="selectedConcepts",
                                :suggestions="filteredConcepts",
                                @complete="filterAnnotationConcept($event)",
                                @item-select="(event) => addToMostRecentConcepts(event.value)",
                                :virtualScrollerOptions="{ items: concepts, itemSize: 40 }",
                                v-tooltip="'Enter concept IDs to locate collections'"
                            )
                            Button#search-button(
                                type="button",
                                label="Search",
                                size="small",
                                iconPos="right",
                                icon="pi pi-search",
                                :loading="false",
                                @click="doQuery(selectedConcepts, selectedSearchMode)"
                            )
                            SelectButton#search-mode.ml-2(
                                v-model="selectedSearchMode",
                                :options="SEARCH_MODE",
                                :unselectable="false",
                                v-tooltip="'Select search connector'"
                            )

                        .mt-5
                            AthenaSearch(
                                :vocabularies="vocabularies",
                                @conceptIdSelected="(value) => selectConceptOfAttribute(value)"
                            )

                .selection-panel.d-flex.w-25.flex-column.align-start.mx-5.px-4.py-2
                    .mb-1(style="font-size: large") Most Recent Concept List:
                    .most-recents(
                        v-for="item in Array.from(mostRecentConcepts).reverse()",
                        @click="selectConcept(item)",
                        v-tooltip="'Add to searchbar'"
                    ) {{ item }}

            TabPanel(header="Relationships")
                .d-flex.flex-column
                    .d-flex.justify-start.mb-2
                        Dropdown(
                            v-model="selectedVocabulary",
                            :options="vocabularies",
                            placeholder="Select Vocabulary",
                            :virtualScrollerOptions="{ items: vocabularies, itemSize: 40 }"
                        )
                    .d-flex.flex-column.justify-start.align-start.mb-2(
                        v-if="selectedVocabulary == 'LOINC'"
                    )
                        strong LOINC AXES
                        AutoComplete(
                            v-for="axis in axes",
                            forceSelection,
                            multiple,
                            minLength="0",
                            delay="0",
                            :placeholder="axis.name",
                            v-model="axis.selectedValues",
                            :suggestions="axis.filteredValues",
                            @complete="filterRelationshipValues($event, axis)",
                            :virtualScrollerOptions="{ items: axis.distinct_values, itemSize: 40 }"
                        )
                    .d-flex.justify-content-start
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
        :loading="isLoadingCollections",
        @conceptIdSelected="(value) => selectConceptOfAttribute(value)"
    )

    ScrollTop
</template>


<script setup lang="ts">
import AutoComplete from "primevue/autocomplete";
import VirtualScroller from "primevue/virtualscroller";
import ScrollTop from "primevue/scrolltop";
import TabView from "primevue/tabview";
import TabPanel from "primevue/tabpanel";
import Dropdown from "primevue/dropdown";
import Button from "primevue/button";
import SelectButton from "primevue/selectbutton";
import CollectionTable from "../components/CollectionTable.vue";
import AthenaSearch from "../components/AthenaSearch.vue";
</script>

<script lang="ts">
import { defineComponent } from "vue";
import {
    getAllConcepts,
    getVocabularies,
    getRelationshipsOfInterest,
    queryRelationships,
    queryAny,
    queryAll,
} from "../requests/dbReq";

const enum SEARCH_MODE {
    ANY = "OR",
    ALL = "AND",
}

export default defineComponent({
    async mounted() {
        const vocabularies = await getVocabularies();
        this.vocabularies = vocabularies
            .map((o: any) => o.vocabulary_id)
            .sort(); //TODO: only show vocabularies with relationshipsOfInterest

        const concepts = await getAllConcepts();
        this.concepts = concepts;

        const axes = await getRelationshipsOfInterest("axes", "LOINC"); //TODO: automate
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
            vocabularies: [],
            selectedVocabulary: "LOINC",
            resultCollections: [],
            resultAttributes: [],
            selectedSearchMode: SEARCH_MODE.ANY,
            mostRecentConcepts: new Set([
                "36033638",
                "45458440",
                "45876191", //descendent (36033638)
                "3667069", //maps to (940658)
            ]),
            isLoadingCollections: false,
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
        async doQuery(concept_ids: [], search_mode: SEARCH_MODE) {
            this.isLoadingCollections = true;

            let result: any = [];
            if (search_mode == SEARCH_MODE.ALL) {
                result = await queryAll(Object.values(concept_ids));
            } else {
                result = await queryAny(Object.values(concept_ids));
            }

            this.resultCollections = result.data.collections || [];
            this.resultAttributes = result.data.attributes || [];

            this.isLoadingCollections = false;

            if (this.hasNoCollections()) {
                this.toastNothingFound();
            }
        },
        async doQueryRelationship() {
            this.isLoadingCollections = true;

            const relationships = [];
            //TODO: generatlize (no just LOINC Axes)
            for (const a of this.axes) {
                const axis: any = a;
                if (axis.selectedValues.length > 0) {
                    relationships.push({
                        relationship_id: axis.relationship_id,
                        concept_names: axis.selectedValues,
                    });
                }
            }

            const result: any = await queryRelationships(
                this.selectedVocabulary,
                relationships
            );
            this.doQuery(result.data, SEARCH_MODE.ANY);
        },
        filterAnnotationConcept(event: any) {
            const filtered: any = this.concepts.filter((concept: string) => {
                return concept.startsWith(event.query);
            });
            this.filteredConcepts = filtered;
        },
        filterRelationshipValues(event: any, relationship: any) {
            const filtered: any = relationship.distinct_values.filter(
                (value: string) => {
                    return value
                        .toLowerCase()
                        .startsWith(event.query.toLowerCase());
                }
            );
            relationship.filteredValues = filtered;
        },
        toastNothingFound() {
            this.$toast.add({
                severity: "error",
                summary: "Nothing Found!",
                life: 1000,
            });
        },
        hasNoCollections() {
            return this.resultCollections.length == 0;
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