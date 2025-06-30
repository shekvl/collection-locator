<template lang="pug">
.px-5
    .d-flex
        TabView.w-100(lazy, @tab-change="clearTable()")
            TabPanel(header="Concepts")
                .d-flex.w-75
                    .d-flex.flex-column.w-100

                        .mt-5
                            AthenaSearch(
                                :vocabularies="vocabularies",
                                @conceptIdSelected="(value) => selectConceptOfAttribute(value)"
                            )

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
                                placeholder="Search for Concepts...",
                                v-model="selectedConcepts",
                                :suggestions="filteredConcepts",
                                @complete="filterAnnotationConcept($event)",
                                @item-select="(event) => addToMostRecentConcepts(event.value)",
                                :virtualScrollerOptions="{ items: concepts, itemSize: 40 }",
                                v-tooltip="'Enter concept IDs to locate collections'"
                            )
                        .d-flex.justify-content-start
                            SelectButton#search-mode.ml-2.mt-5(
                                v-model="selectedSearchMode",
                                :options="SEARCH_MODE",
                                :unselectable="false",
                                v-tooltip="'Select search connector'"
                            )
                            Button#search-button.mt-5(
                                type="button",
                                label="Search",
                                size="small",
                                iconPos="right",
                                icon="pi pi-search",
                                :loading="false",
                                @click="doQuery(selectedConcepts, selectedSearchMode)"
                            )


                .selection-panel.d-flex.w-25.flex-column.align-start.mx-5.px-4.py-3
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
            TabPanel(header="Collection Quality")
                .d-flex.w-75
                    .d-flex.flex-column.w-100
                        .mt-5
                            .d-flex
                                Dropdown.mr-3.required(
                                    v-model="selectedQuality",
                                    :options="qualityCharacteristics",
                                    optionLabel="friendly_name",
                                    optionValue="id",
                                    placeholder="Select Quality Characteristic",
                                    :virtualScrollerOptions="{ items: qualities, itemSize: 40 }"
                                )
                                InputNumber.mr-3(
                                    input-id="from1",
                                    v-model="from1",
                                    placeholder="From...",
                                    :minFractionDigits="0",
                                    :maxFractionDigits="2",
                                    v-tooltip="'Specify a lower bound for quality'"
                                )
                                InputNumber.mr-3(
                                    input-id="to1",
                                    v-model="to1",
                                    placeholder="To...",
                                    :minFractionDigits="0",
                                    :maxFractionDigits="2",
                                    v-tooltip="'Specify an upper bound for quality'"
                                )

                                Button#search-button(
                                  type="button",
                                  label="Search",
                                  size="small",
                                  iconPos="right",
                                  icon="pi pi-search",
                                  :disabled="(from1 == null && to1 == null) || selectedQuality == null",
                                  :loading="false",
                                  @click="doQueryByQuality(from1, to1, selectedQuality)"
                                )

            TabPanel(header="Attribute Quality")
                .d-flex.w-75
                    .d-flex.flex-column.w-100

                        .mt-5
                            AthenaSearch(
                                :vocabularies="vocabularies",
                                @conceptIdSelected="(value) => selectConceptOfAttribute(value)"
                            )

                        .d-flex
                            AutoComplete.mx-0(
                                forceSelection,
                                minLength="1",
                                delay="0",
                                multiple,
                                placeholder="Specify Concepts...",
                                v-model="selectedConcepts",
                                :suggestions="filteredConcepts",
                                @complete="filterAnnotationConcept($event)",
                                @item-select="(event) => addToMostRecentConcepts(event.value)",
                                :virtualScrollerOptions="{ items: concepts, itemSize: 40 }",
                                v-tooltip="'Enter concept IDs to match attributes'"
                            )

                        .mt-5.d-flex
                            Dropdown.mr-3.required(
                                v-model="selectedAttributeQuality",
                                :options="qualityCharacteristics",
                                optionLabel="friendly_name",
                                optionValue="id",
                                placeholder="Select Quality Characteristic",
                                :virtualScrollerOptions="{ items: qualities, itemSize: 40 }"
                            )
                            InputNumber.mr-3(
                                input-id="from2",
                                v-model="from2",
                                placeholder="From...",
                                :minFractionDigits="0",
                                :maxFractionDigits="2",
                                v-tooltip="'Specify a lower bound for quality'"
                            )
                            InputNumber.mr-3(
                                input-id="to2",
                                v-model="to2",
                                placeholder="To...",
                                :minFractionDigits="0",
                                :maxFractionDigits="2",
                                v-tooltip="'Specify an upper bound for quality'"
                            )

                            Button#search-button(
                                type="button",
                                label="Search",
                                size="small",
                                iconPos="right",
                                icon="pi pi-search",
                                :disabled="(from2 == null && to2 == null) || selectedAttributeQuality == null",
                                :loading="false",
                                @click="doQueryByAttributeQuality(selectedConcepts, from2, to2, selectedAttributeQuality)"
                            )


                .selection-panel.d-flex.w-25.flex-column.align-start.mx-5.px-4.py-3
                    .mb-1(style="font-size: large") Most Recent Concept List:
                    .most-recents(
                        v-for="item in Array.from(mostRecentConcepts).reverse()",
                        @click="selectConcept(item)",
                        v-tooltip="'Add to searchbar'"
                    ) {{ item }}
            TabPanel(header="Anonymized Data")
                .d-flex.w-75
                    .d-flex.flex-column.w-100

                        .mt-5
                            AthenaSearch(
                                :vocabularies="vocabularies",
                                @conceptIdSelected="selectConceptOfAttribute"
                            )

                        //— your concept picker
                        .d-flex.align-items-center
                            AutoComplete.mx-0(
                                forceSelection
                                :minLength="1"
                                :delay="0"
                                multiple
                                placeholder="Specify Concepts..."
                                v-model="selectedConcepts"
                                :suggestions="filteredConcepts"
                                @complete="filterAnnotationConcept"
                                @item-select="addToMostRecentConcepts"
                                :virtualScrollerOptions="{ items: concepts, itemSize: 40 }"
                                v-tooltip="'Enter concept IDs to match attributes'"
                            )

                        //— now immediately below, one row per selected concept
                        .mt-3
                            div(v-for="range in conceptRangesList" :key="range.concept_id")
                                .d-flex.align-items-center.mb-2
                                    // fixed‐width ID column
                                    span.flex-shrink-0.align-self-center.me-3.font-weight-bold(style="width:80px; text-align:left;") {{ real_id(range.concept_id)+':' }}
                                    // now each v-model goes to its own `range.from` or `range.to`
                                    InputNumber.me-2(
                                        :modelValue="range.from"
                                        @input="e => range.from = e.value"
                                        placeholder="From..."
                                        :minFractionDigits="0"
                                        :maxFractionDigits="2"
                                        style="width:200px!important;"
                                    )
                                    InputNumber(
                                        :modelValue="range.to"
                                        @input="e => range.to = e.value"
                                        placeholder="To..."
                                        :minFractionDigits="0"
                                        :maxFractionDigits="2"
                                        style="width:200px!important;"
                                    )

                        //— your search button
                        Button#search-button.mt-4.align-self-start.w-auto(
                            type="button",
                            label="Search",
                            size="small",
                            iconPos="right",
                            icon="pi pi-search",
                            :disabled="!canSearchAnonymous",
                            :loading="false",
                            @click="doQueryAnonymousData(conceptRangesList)"
                        )

                .selection-panel.d-flex.w-25.flex-column.align-start.mx-5.px-4.py-3
                    .mb-1(style="font-size: large") Most Recent Concept List:
                    .most-recents(
                        v-for="item in Array.from(mostRecentConcepts).reverse()",
                        @click="selectConcept(item)",
                        v-tooltip="'Add to searchbar'"
                    ) {{ item }}

    CollectionTable(v-if="resultCollections.length > 0",
          :collections="resultCollections",
          :attributes="resultAttributes",
          :collection_quality_values="resultCollectionQualityValues",
          :attribute_quality_values="resultAttributeQualityValues",
          :selected_concepts="selectedConcepts",
          :selected_quality_characteristic_name="getQualityCharacteristicName(selectedQuality)",
          :selected_attribute_quality_characteristic_name="getQualityCharacteristicName(selectedAttributeQuality)",
          :loading="isLoadingCollections",
          @clearTable="clearTable()"

      )

    ScrollTop
</template>

<script setup lang="ts">
import {ref, reactive, watch, onMounted, computed} from 'vue';
import AutoComplete from 'primevue/autocomplete';
import VirtualScroller from 'primevue/virtualscroller';
import ScrollTop from 'primevue/scrolltop';
import TabView from 'primevue/tabview';
import TabPanel from 'primevue/tabpanel';
import Dropdown from 'primevue/dropdown';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import SelectButton from 'primevue/selectbutton';
import CollectionTable from '@/components/CollectionTable.vue';
import AthenaSearch from '@/components/AthenaSearch.vue';
import QueryComponent from '@/components/QueryComponent.vue';

import {
    getAllConcepts,
    getVocabularies,
    getRelationshipsOfInterest,
    queryRelationships,
    queryAny,
    queryAll,
    getQualityCharacteristics,
    queryCollectionsByQuality,
    queryCollectionsByAttributeQuality, real_id, real_ids
} from '@/requests/dbRequestFunctions';
import {node2Axios} from "@/requests/axios";
import {transformServerData} from "@/requests/dataTransformService";

const SEARCH_MODE = {
    ANY: 'OR',
    ALL: 'AND'
};


// 1) your reactive state

const selectedConcepts = ref<string[]>([ '1315411: cetuximab (RxNorm)' ])

const emit = defineEmits<{
    (e: 'conceptIdSelected', value: string): void
}>()

// New: each entry holds one concept’s range
const conceptRangesList = ref<
    { concept_id: string; from: number | null; to: number | null }[]
>([])

// Keep ranges in sync, but **reuse** existing entries so .from/.to never reset
watch(
    selectedConcepts,
    (newIds) => {
        console.log("newIds: "+newIds.length);
        console.log(newIds);
        const keep = conceptRangesList.value;
        // console.log("keep");
        // console.log(keep);
        conceptRangesList.value = newIds.map((id) => {
            const existing = keep.find((r) => r.concept_id === id)
            return existing || { concept_id: id, from: null, to: null }
        });
        console.log("conceptRangesList.value");
        console.log(conceptRangesList.value);
        return conceptRangesList.value;
    },
    { immediate: true, flush: 'sync', deep: true }
)
const canSearchAnonymous = computed(() => {
    // Only allow when there’s at least one range **and** every range has both ends filled
    return conceptRangesList.value.length > 0
        && conceptRangesList.value.every(r => r.from != null && r.to != null);
});

const filteredConcepts = ref(null);
const concepts = ref([]);
const axes = ref<any[]>([]);
const vocabularies = ref<string[]>([]);
const qualityCharacteristics = ref<any[]>([]);
const from1 = ref<number | null>(null);
const to1 = ref<number | null>(null);
const from2 = ref<number | null>(null);
const to2 = ref<number | null>(null);
const selectedQuality = ref(null);
const selectedAttributeQuality = ref(null);
const selectedVocabulary = ref('LOINC');
const selectedSearchMode = ref(SEARCH_MODE.ANY);
const resultCollections = ref([]);
const resultAttributes = ref([]);
const resultCollectionQualityValues = ref([]);
const resultAttributeQualityValues = ref([]);
const mostRecentConcepts = ref(new Set<string>(['40567867: Age (SNOMED)', '1315411: cetuximab (RxNorm)', '4219780: Left colectomy (SNOMED)']));
const isLoadingCollections = ref(false);

function selectConcept(concept_id: string) {
    if (!selectedConcepts.value.includes(concept_id)) {
        selectedConcepts.value.push(concept_id)
        // no need to manually seed ranges—watch(selectedConcepts) does that now
    }
}

function selectConceptOfAttribute(concept_id: string) {
    selectConcept(concept_id);
    mostRecentConcepts.value.delete(concept_id);
    mostRecentConcepts.value.add(concept_id);
}

function filterAnnotationConcept(event: any) {
    filteredConcepts.value = concepts.value.filter((c: string) =>
        c.toLowerCase().startsWith(event.query.toLowerCase())
    );
}

function filterRelationshipValues(event: any, relationship: any) {
    relationship.filteredValues = relationship.distinct_values.filter((value: string) =>
        value.toLowerCase().startsWith(event.query.toLowerCase())
    );
}

function getQualityCharacteristicName(id: any) {
    const item = qualityCharacteristics.value.find((c) => c.id === id);
    return item ? item.name : null;
}

function toastNothingFound() {
    // assumes a global toast plugin is available
    window.$toast?.add({
        severity: 'error',
        summary: 'Nothing Found!',
        life: 1000
    });
}

function hasNoCollections() {
    return resultCollections.value.length === 0;
}

function clearTable() {
    resultCollections.value = [];
    resultAttributes.value = [];
    resultCollectionQualityValues.value = [];
    selectedQuality.value = null;
    selectedAttributeQuality.value = null;
}

async function doQuery(concept_ids: string[], search_mode: string) {

    console.log("doQuery: concept_ids");
    console.log(concept_ids);
    isLoadingCollections.value = true;
    const result =
        search_mode === SEARCH_MODE.ALL
            ? await queryAll(real_ids(concept_ids))
            : await queryAny(real_ids(concept_ids));

    resultCollections.value = result.data.collections || [];
    resultAttributes.value = result.data.attributes || [];
    resultCollectionQualityValues.value = result.data.collection_quality_values || [];
    resultAttributeQualityValues.value = result.data.attribute_quality_values || [];
    isLoadingCollections.value = false;
    if (hasNoCollections()) toastNothingFound();
}

async function doQueryByQuality(f: number | null, t: number | null, qid: any) {
    isLoadingCollections.value = true;
    const result = await queryCollectionsByQuality(f, t, qid);
    resultCollections.value = result.data.collections || [];
    resultAttributes.value = result.data.attributes || [];
    resultCollectionQualityValues.value = result.data.collection_quality_values || [];
    resultAttributeQualityValues.value = result.data.attribute_quality_values || [];
    isLoadingCollections.value = false;
    if (hasNoCollections()) toastNothingFound();
}

async function doQueryByAttributeQuality(
    concept_ids: string[],
    f: number | null,
    t: number | null,
    qid: any
) {
    isLoadingCollections.value = true;
    const result = await queryCollectionsByAttributeQuality(concept_ids, f, t, qid);
    resultCollections.value = result.data.collections || [];
    resultAttributes.value = result.data.attributes || [];
    resultCollectionQualityValues.value = result.data.collection_quality_values || [];
    resultAttributeQualityValues.value = result.data.attribute_quality_values || [];
    isLoadingCollections.value = false;
    if (hasNoCollections()) toastNothingFound();
}

async function doQueryAnonymousData(rangesList: typeof conceptRangesList.value) {
    isLoadingCollections.value = true

    const payload = rangesList.map(r => ({
        concept: {
            concept_id:  real_id(r.concept_id),
            concept_name: ''
        },
        value: {
            fromValue: r.from != null ? String(r.from) : '',
            toValue:   r.to   != null ? String(r.to)   : ''
        }
    }))

    try {
        const { data } = await node2Axios.post('/api/data', payload)
        console.log(data)
        let transformedData = transformServerData(data);
        console.log(transformedData);
        resultCollections.value = transformedData.collections || [];
        resultAttributes.value = transformedData.attributes || [];
        resultCollectionQualityValues.value = [];
        resultAttributeQualityValues.value = [];
        isLoadingCollections.value = false;
        if (hasNoCollections()) toastNothingFound();

    } finally {
        isLoadingCollections.value = false
    }
}

// async function doQueryAnonymousData(ranges: Record<number, { from: number|null; to: number|null }>) {
//     // 1) build payload
//     const payload = Object.entries(ranges)
//         .map(([id, range]) => ({
//             concept: {
//                 concept_id:    Number(id),
//                 concept_name:  ''           // per your request
//             },
//             value: {
//                 fromValue:     range.from   != null ? String(range.from) : '',
//                 toValue:       range.to     != null ? String(range.to)   : ''
//             }
//         }))
//
//     try {
//         // 2) send POST to http://localhost:5002/api/data
//         const resp = await node2Axios.post('/api/data', payload)
//         // 3) handle response just like in doQuery…()
//         //    for example:
//         // resultCollections.value            = resp.data.collections            || []
//         // resultAttributes.value             = resp.data.attributes             || []
//         // resultCollectionQualityValues.value= resp.data.collection_quality_values|| []
//         // resultAttributeQualityValues.value = resp.data.attribute_quality_values || []
//         console.log(resp.data);
//     } catch (err: any) {
//         console.error('Anonymous-data query failed', err)
//         // ... your error handling …
//     } finally {
//         isLoadingCollections.value = false
//     }
// }

async function doQueryRelationship() {
    isLoadingCollections.value = true;
    const relationships = axes.value
        .filter((a) => a.selectedValues?.length > 0)
        .map((axis: any) => ({
            relationship_id: axis.relationship_id,
            concept_names: axis.selectedValues
        }));
    const result = await queryRelationships(selectedVocabulary.value, relationships);
    await doQuery(result.data, SEARCH_MODE.ANY);
}

onMounted(async () => {
    vocabularies.value = (await getVocabularies())
        .map((v: any) => v.vocabulary_id)
        .sort();
    qualityCharacteristics.value = await getQualityCharacteristics();
    concepts.value = await getAllConcepts();
    const relAxes = await getRelationshipsOfInterest('axes', 'LOINC');
    axes.value = relAxes.map((axis: any) => ({
        ...axis,
        selectedValues: [],
        filteredValues: []
    }));
});
</script>
