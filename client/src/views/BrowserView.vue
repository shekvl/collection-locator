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
            .d-flex.justify-start
                Dropdown(
                    v-model="selectedOntology",
                    :options="ontologies",
                    placeholder="Select Vocabulary",
                    :virtualScrollerOptions="{ items: ontologies, itemSize: 40 }"
                )
        .links.w-50.d-flex
            .d-flex.justify-end.align-center.w-100
                a(
                    href="https://ohdsi.github.io/CommonDataModel/",
                    target="_blank"
                )
                    .d-flex.align-center
                        | Ohdsi&nbsp;
                        i.pi.pi-external-link
                a(
                    href="https://athena.ohdsi.org/search-terms/start",
                    target="_blank"
                )
                    .d-flex.align-center
                        | Athena&nbsp;
                        i.pi.pi-external-link

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
                            @click=""
                        )

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
        :value="products",
        v-model:selection="tableSelection",
        v-model:filters="filters",
        v-model:expandedRows="expandedRows",
        filterDisplay="menu",
        :globalFilterFields="columns.map((col) => col.field)",
        :dataKey="products.id",
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
                        MultiSelect(:modelValue="selectedColumns" :options="columns" optionLabel="header" @update:modelValue="onToggle" placeholder="Select columns" style="width: 10em")
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
            div to be done..
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
            :dataType="col.type || 'text'"
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

const products = [
    {
        id: "1000",
        code: "f230fh0g3",
        name: "Bamboo Watch",
        description: "Product Description",
        image: "bamboo-watch.jpg",
        price: 65,
        category: "Accessories",
        quantity: 24,
        inventoryStatus: "INSTOCK",
        rating: 5,
    },
    {
        id: "1001",
        code: "nvklal433",
        name: "Black Watch",
        description: "Product Description",
        image: "black-watch.jpg",
        price: 72,
        category: "Accessories",
        quantity: 61,
        inventoryStatus: "INSTOCK",
        rating: 4,
    },
    {
        id: "1002",
        code: "zz21cz3c1",
        name: "Blue Band",
        description: "Product Description",
        image: "blue-band.jpg",
        price: 79,
        category: "Fitness",
        quantity: 2,
        inventoryStatus: "LOWSTOCK",
        rating: 3,
    },
    {
        id: "1003",
        code: "244wgerg2",
        name: "Blue T-Shirt",
        description: "Product Description",
        image: "blue-t-shirt.jpg",
        price: 29,
        category: "Clothing",
        quantity: 25,
        inventoryStatus: "INSTOCK",
        rating: 5,
    },
    {
        id: "1004",
        code: "h456wer53",
        name: "Bracelet",
        description: "Product Description",
        image: "bracelet.jpg",
        price: 15,
        category: "Accessories",
        quantity: 73,
        inventoryStatus: "INSTOCK",
        rating: 4,
    },
    {
        id: "1005",
        code: "av2231fwg",
        name: "Brown Purse",
        description: "Product Description",
        image: "brown-purse.jpg",
        price: 120,
        category: "Accessories",
        quantity: 0,
        inventoryStatus: "OUTOFSTOCK",
        rating: 4,
    },
    {
        id: "1006",
        code: "bib36pfvm",
        name: "Chakra Bracelet",
        description: "Product Description",
        image: "chakra-bracelet.jpg",
        price: 32,
        category: "Accessories",
        quantity: 5,
        inventoryStatus: "LOWSTOCK",
        rating: 3,
    },
    {
        id: "1007",
        code: "mbvjkgip5",
        name: "Galaxy Earrings",
        description: "Product Description",
        image: "galaxy-earrings.jpg",
        price: 34,
        category: "Accessories",
        quantity: 23,
        inventoryStatus: "INSTOCK",
        rating: 5,
    },
    {
        id: "1008",
        code: "vbb124btr",
        name: "Game Controller",
        description: "Product Description",
        image: "game-controller.jpg",
        price: 99,
        category: "Electronics",
        quantity: 2,
        inventoryStatus: "LOWSTOCK",
        rating: 4,
    },
    {
        id: "1009",
        code: "cm230f032",
        name: "Gaming Set",
        description: "Product Description",
        image: "gaming-set.jpg",
        price: 299,
        category: "Electronics",
        quantity: 63,
        inventoryStatus: "INSTOCK",
        rating: 3,
    },
];

const tableSelection = ref([]);
const expandedRows = ref([]);

const expandAll = function () {
    expandedRows.value = products.filter((p) => p.id);
};
const collapseAll = function () {
    expandedRows.value = null;
};
</script>

<script lang="ts">
import { defineComponent } from "vue";
import {
    getAllConcepts,
    getOntologies,
    getQueryRelationships,
} from "../requests/getReq";
import { FilterMatchMode, FilterOperator } from "primevue/api";

const enum COLUMNTYPE {
    TEXT = "text",
    NUMERIC = "numeric",
    DATE = "date",
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
        for (const column of this.columns) {
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
            selectedConcepts: ["37020651", "1003901"],
            filteredConcepts: null,
            concepts: [],
            axes: null,
            ontologies: [],
            selectedOntology: null,

            filters: {
                global: { value: null, matchMode: FilterMatchMode.CONTAINS },
            },
            columns: [
                //TODO get from db {columns:{{field, header, type},..}, data:{}}
                { field: "code", header: "Code", type: COLUMNTYPE.TEXT },
                { field: "name", header: "Name", type: COLUMNTYPE.TEXT },
                {
                    field: "category",
                    header: "Category",
                    type: COLUMNTYPE.TEXT,
                },
                {
                    field: "quantity",
                    header: "Quantity",
                    type: COLUMNTYPE.NUMERIC,
                },
            ],
            selectedColumns: [],
        };
    },
    created() {
        const objectString = localStorage.getItem(
            "collectionBrowserFilteredColumns"
        );

        this.selectedColumns = objectString
            ? JSON.parse(objectString)
            : Object.assign(this.columns);
    },
    methods: {
        onToggle(value: any) {
            const filteredColumns: any = this.columns.filter((col: any) => {
                return value.includes(col);
            });
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
                    const column: any = this.columns.filter(
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
.n-tag {
    margin: 2px;
    size: large;
}

.tag-container {
    width: 30px;
    display: flex;
    justify-content: flex-start;
}

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
    text-decoration: none;
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