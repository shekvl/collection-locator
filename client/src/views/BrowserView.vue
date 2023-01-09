<template lang="pug">
//- div tags:
//- .tag-container
//-     n-tag(
//-         v-for="tag in tags.value",
//-         size="large",
//-         type="info",
//-         @close="removeTag",
//-         closable
//-     ) {{ tag }}
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

    DataTable.p-datatable-sm(
        ref="dt",
        :value="resultCollections",
        v-model:selection="tableSelection",
        v-model:filters="filters",
        v-model:expandedRows="expandedRows",
        filterDisplay="menu",
        :globalFilterFields="columns.collection.map((col) => col.field)",
        :dataKey="resultCollections.id",
        responsiveLayout="scroll",
        rowHover,
        showGridlines,
        :rows="10",
        :paginator="true",
        paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown",
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}",
        :rowsPerPageOptions="[10,25,50,100]",
        removableSort,
        sortMode="multiple",
        stateStorage="local",
        stateKey="collectionBrowserTableState",
        exportFilename="collections",
        :exportFunction="export",
        stripedRows
    )
        template.d-flex(#header)
            .d-flex.justify-space-between.align-center
                .d-flex.align-center
                    .table-header.mx-5 Collections
                    div(style="text-align:left")
                        MultiSelect(:modelValue="selectedColumns" :options="columns.collection" optionLabel="header" @update:modelValue="onToggle" placeholder="Select columns" style="width: 10em")
                .d-flex.align-center
                    Button.p-button-outlined.p-button-sm.mx-2(
                        icon="pi pi-plus",
                        label="Expand all",
                        @click="expandAll"
                    )
                    Button.p-button-outlined.p-button-sm(
                        icon="pi pi-minus",
                        label="Collapse all",
                        @click="collapseAll"
                    )
                    Button.p-button-outlined.p-button-sm.mx-2(
                        icon="pi pi-bookmark",
                        label="Bookmark selected",
                        @click=""
                    )
                    Button.p-button-outlined.p-button-sm(
                        icon="pi pi-download",
                        label="Export",
                        @click="exportCSV($event)"
                    )
                    Button.p-button-outlined.p-button-sm.mx-2(
                        icon="pi pi-filter-slash",
                        label="Clear",
                        @click="clearTableFilters"
                    )
                    span.p-input-icon-left
                        i.pi.pi-search
                        InputText.p-inputtext-sm(
                            v-model="filters['global'].value",
                            placeholder="Keyword Search"
                        )
        template(#expansion="slotProps")
            DataTable.p-datatable-sm(
                :value="resultAttributes",
                :dataKey="resultAttributes.concept_id",
                responsiveLayout="scroll",
                rowHover,
                showGridlines,
                removableSort,
                stripedRows
            )
                Column(
                    v-for="col of columns.attribute",
                    :field="col.field",
                    :header="col.header",
                    :key="col.field",
                    sortable,
                )
        //- template.d-flex(#footer) //conflicts with paginator
        //-     div asdf

        template(#empty) No collections found
        template(#loading) Loading collections..
        Column(selectionMode="multiple", headerStyle="width: 3rem", :reorderableColumns="false")
        Column(:expander="true", headerStyle="width: 3rem", :reorderableColumns="false")
        Column(
            v-for="col of selectedColumns",
            :field="col.field",
            :header="col.header",
            :key="col.field",
            sortable,
        )
            template(#filter="{ filterModel }")
                InputText.p-column-filter(
                    type="text",
                    v-model="filterModel.value",
                    :placeholder="`Search by ${filterModel.value} - ${filterModel.matchMode}`"
                )
            template(#filterclear="{ filterCallback }")
                Button.p-button-outlined.p-button-sm(
                    label="Primary",
                    @click="clearColumnFilter(col)"
                ) Clear

    ScrollTop
</template>

//TODO tooltip: info about concept (fetch and "cache"); including link to athena with code as param in url
//real time search with suggestion(maybe restricted by vocab etc.)  => only valid concepts addable
//all/any concept search toggle

//filter result
//bookmark results
//composite descriptor selection panel
//browse all concepts (sort hierachy)
//show recent concept list

//show tabs according to query_relationships

//TODO extra COMPONENT for search..

//! autocomplete does not scale(?) problem displaying 10000 items.. can be limited by minlength; cdm.concept vs concept

//primevue datatable [https://www.primefaces.org/primevue/datatable]
//body slotprops => make concept ids addable to search
//download table option? use exportCSV() predefined method
//:loading option with #loading tamplet with skeleton? im fetching takes some time (necessary with paging?)

//context menu -> favorites... although separate star button could also visualize state.. (how to visualize that already in a bookmark folder?)
//select checkboxes (download only selected, bookmark [sets, only add once])

//allow missing quality metadata
//infor about how to use table
//componentinize BrowserView()


<script setup lang="ts">
import { ref } from "vue";
import { NTag } from "naive-ui";
import AutoComplete from "primevue/autocomplete";
import VirtualScroller from "primevue/virtualscroller";
import ScrollTop from "primevue/scrolltop";
import TabView from "primevue/tabview";
import TabPanel from "primevue/tabpanel";
import Dropdown from "primevue/dropdown";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import ColumnGroup from "primevue/columngroup";
import Row from "primevue/row";
import InputText from "primevue/inputtext";
import MultiSelect from "primevue/multiselect";
import ExternalLink from "../components/ExternalLink.vue";
import SelectButton from "primevue/selectbutton";

let tags = ref(new Set(["37020651", "1003901"]));

const removeTag = function (event: any) {
    const tag =
        event.target.parentElement.parentElement.parentElement.textContent;
    tags.value.delete(tag);
    console.log("tag removed:", tag);
};

// const addTag = function (tag: string) {
//     tags.value.add(tag);
//     console.log("tag added:", tag);
// };

const mostRecentConcepts = new Set();
function print(string) {
    console.log(string);
}
</script>

<script lang="ts">
import { defineComponent } from "vue";
import {
    getAllConcepts,
    getOntologies,
    getQueryRelationships,
    queryAny,
} from "../requests/getReq";
import { FilterMatchMode, FilterOperator } from "primevue/api";

const enum COLUMNTYPE {
    TEXT = "text",
    NUMERIC = "numeric",
    DATE = "date",
}

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

        //add dynamically generate filters for each column
        let filters: any = Object.assign(this.filters);
        for (const column of this.columns.collection) {
            filters[column.field] = {
                operator: FilterOperator.AND,
                constraints: [
                    {
                        value: null,
                        matchMode:
                            column.type === COLUMNTYPE.NUMERIC
                                ? FilterMatchMode.EQUALS
                                : column.type === COLUMNTYPE.DATE
                                ? FilterMatchMode.DATE_IS
                                : FilterMatchMode.STARTS_WITH,
                    },
                ],
            };
        }
        this.filters = filters;
    },
    data() {
        return {
            selectedConcepts: ["36033638", "45458440"],
            filteredConcepts: null,
            concepts: [],
            axes: null,
            ontologies: [],
            selectedOntology: null,

            filters: {
                global: { value: null, matchMode: FilterMatchMode.CONTAINS },
            },
            columns: {
                collection: [
                    //probably on the client side after all
                    { field: "name", header: "Name", type: COLUMNTYPE.TEXT },
                    {
                        field: "number_of_records",
                        header: "Number of Records",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "person_name",
                        header: "Submitted By",
                        type: COLUMNTYPE.TEXT,
                    }, //name not id
                    {
                        field: "institution_id",
                        header: "Institution",
                        type: COLUMNTYPE.TEXT,
                    },
                    {
                        field: "completeness",
                        header: "Completeness",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "accuracy",
                        header: "Accuracy",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "reliability",
                        header: "Reliability",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "timeliness",
                        header: "Timeliness",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "consistancy",
                        header: "Consistancy",
                        type: COLUMNTYPE.NUMERIC,
                    },
                ],
                attribute: [
                    {
                        field: "concept_id",
                        header: "Concept ID",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "attribute_name",
                        header: "Column Name",
                        type: COLUMNTYPE.TEXT,
                    },
                    { field: "code", header: "Code", type: COLUMNTYPE.TEXT },
                    {
                        field: "vocabulary_id",
                        header: "Vocabulary",
                        type: COLUMNTYPE.TEXT,
                    },
                    {
                        field: "completeness",
                        header: "Completeness",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "accuracy",
                        header: "Accuracy",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "reliability",
                        header: "Reliability",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "timeliness",
                        header: "Timeliness",
                        type: COLUMNTYPE.NUMERIC,
                    },
                    {
                        field: "consistancy",
                        header: "Consistancy",
                        type: COLUMNTYPE.NUMERIC,
                    },
                ],
            },
            selectedColumns: [],
            tableSelection: [],
            expandedRows: [],
            resultCollections: [],
            resultAttributes: [],
            selectedSearchMode: SEARCH_MODE.ANY,
        };
    },
    created() {
        const objectString = localStorage.getItem(
            "collectionBrowserFilteredColumns"
        );

        this.selectedColumns = objectString
            ? JSON.parse(objectString)
            : Object.assign(this.columns.collection);
    },
    methods: {
        expandAll() {
            this.expandedRows = this.resultCollections.filter((p: any) => p.id);
        },
        collapseAll() {
            this.expandedRows = [];
        },
        async doQueryAny() {
            //start loading
            const result: any = await queryAny(
                Object.values(this.selectedConcepts)
            );
            console.log(result);
            this.resultCollections = result.data.collections;
            this.resultAttributes = result.data.attributes;
            //create table
            //loading finished
        },
        onToggle(value: any) {
            const filteredColumns: any = this.columns.collection.filter(
                (col: any) => {
                    return value.includes(col);
                }
            );
            this.selectedColumns = Object.assign(filteredColumns);
            console.log(this.selectedColumns);
            localStorage.setItem(
                "collectionBrowserFilteredColumns",
                JSON.stringify(filteredColumns)
            );
        },
        exportCSV() {
            const dt: any = this.$refs.dt;
            dt.exportCSV();
        },
        defaultFilter(column: any) {
            return {
                operator: FilterOperator.AND,
                constraints: [
                    {
                        value: null,
                        matchMode:
                            column.type === COLUMNTYPE.NUMERIC
                                ? FilterMatchMode.EQUALS
                                : column.type === COLUMNTYPE.DATE
                                ? FilterMatchMode.DATE_IS
                                : FilterMatchMode.STARTS_WITH,
                    },
                ],
            };
        },
        clearColumnFilter(column: any) {
            const filters: any = this.filters;
            filters[column.field] = this.defaultFilter(column);
        },
        clearTableFilters() {
            for (const key in this.filters) {
                if (key === "global") {
                    this.filters.global.value = null;
                } else {
                    const column: any = this.columns.collection.filter(
                        (c) => c.field === key
                    )[0];
                    this.clearColumnFilter(column);
                }
            }
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
/* .n-tag {
    margin: 2px;
    size: large;
} */

/* .tag-container {
    width: 30px;
    display: flex;
    justify-content: flex-start;
} */

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

/* .p-autocomplete-token{
    border-radius: 5px !important;
} */
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

<style>
@media (max-width: 1300px) {
    .p-datatable-header Button,
    #search-button {
        font-size: 0 !important;
    }

    .p-datatable-header .p-button-icon,
    #search-button .p-button-icon {
        margin: 0 !important;
    }
}
</style>