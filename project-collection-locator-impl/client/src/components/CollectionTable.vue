<template lang="pug">

//- primevue datatable [https://www.primefaces.org/primevue/datatable]
DataTable.p-datatable-sm(
    ref="dt",
    :value="collections",
    v-model:selection="tableSelection",
    v-model:filters="filters",
    v-model:expandedRows="expandedRows",
    filterDisplay="menu",
    :globalFilterFields="columns.map((col) => col.field)",
    :dataKey="collections.id",
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
    exportFilename="collections",
    :exportFunction="export",
    stripedRows
    :loading="loading"
)
    template.d-flex(#header)
        .d-flex.justify-space-between.align-center
            .d-flex.align-center
                .table-header.mx-5 Collections
                div(style="text-align:left")
                    MultiSelect(:modelValue="selectedColumns" :options="columns" optionLabel="header" @update:modelValue="onToggle" placeholder="Select columns" style="width: 10em")
            .d-flex.align-center
                Button.p-button-outlined.p-button-sm(
                    icon="pi pi-plus",
                    label="Expand all",
                    @click="expandAll"
                )
                Button.p-button-outlined.p-button-sm.mx-2(
                    icon="pi pi-minus",
                    label="Collapse all",
                    @click="collapseAll"
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
                Button.p-button-outlined.p-button-sm.mx-2(
                    icon="pi pi-times",
                    label="Clear Data",
                    @click="$emit('clearTable')",
                )
                span.p-input-icon-left
                    i.pi.pi-search
                    InputText.p-inputtext-sm(
                        v-model="filters['global'].value",
                        placeholder="Keyword Search",
                    )
    template(#expansion="slotProps")
        .v-container
            .v-row
                AttributeTable.v-col-5(
                  :attributes="attributes.filter((a)=> a.collection_id === slotProps.data.id)",
                  :attribute_quality_values="attribute_quality_values.filter((a)=> a.collection_id === slotProps.data.id)",
                  :selected_attribute_quality_characteristic_name="selected_attribute_quality_characteristic_name"
                  :selected_concepts="selected_concepts",
                  @conceptIdSelected="(value) => $emit('conceptIdSelected', value)"
                )
                CollectionQualityValuesTable.v-col-3(
                  :collection_quality_values="collection_quality_values.filter((a)=> a.collection_id === slotProps.data.id)"
                  :selected_quality_characteristic="selected_quality_characteristic_name"
                )

    template(#empty) No collections found
    //- Column(selectionMode="multiple", headerStyle="width: 3rem", :reorderableColumns="false")
    Column(:expander="true", headerStyle="width: 3rem", :reorderableColumns="false")
    Column(
        v-for="col of selectedColumns",
        :field="col.field",
        :header="col.header",
        :key="col.field",
        :dataType="col.type",
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
</template>

<script setup lang="ts">
import { ref } from "vue";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import ColumnGroup from "primevue/columngroup";
import Row from "primevue/row";
import InputText from "primevue/inputtext";
import MultiSelect from "primevue/multiselect";
import AttributeTable from "../components/AttributeTable.vue";
import CollectionQualityValuesTable from "@/components/CollectionQualityValuesTable.vue";

import { onBeforeMount } from 'vue'

const STATE_KEY = 'primevue_datatable_collectionBrowserTableState'

onBeforeMount(() => {
    const raw = localStorage.getItem(STATE_KEY)
    if (!raw) return

        const state = JSON.parse(raw)

        if (state.filters && typeof state.filters === 'object') {
            for (const field of Object.keys(state.filters)) {
                const filter = state.filters[field]
                // PrimeVue v3 shape: { value, matchMode }  or { operator, constraints: [...] }
                if (filter && filter.value === null) {
                    filter.value = ''      // empty matches-all
                }
                if (filter && Array.isArray(filter.constraints)) {
                    filter.constraints.forEach((c: any) => {
                        if (c.value === null) c.value = ''
                    })
                }
            }
            localStorage.setItem(STATE_KEY, JSON.stringify(state))
        }
})
</script>

<script lang="ts">
import { defineComponent } from "vue";
import { FilterMatchMode, FilterOperator } from "primevue/api";

const enum COLUMNTYPE {
    TEXT = "text",
    NUMERIC = "numeric",
    DATE = "date",
}

export default defineComponent({
    async mounted() {
        this.setupFilters();
    },
    props: {
        collections: {
            type: [],
            default: [],
        },
        attributes: [],
        collection_quality_values: [],
        attribute_quality_values: [],
        selected_concepts: [],
        selected_quality_characteristic_name: String,
        selected_attribute_quality_characteristic_name: String,
        loading: Boolean,
    },
    data() {
        return {
            filters: {
                global: { value: '', matchMode: FilterMatchMode.CONTAINS },
            },
            columns: [
                { field: "id", header: "ID", type: COLUMNTYPE.NUMERIC },
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
                },
                {
                    field: "institution_id",
                    header: "Institution",
                    type: COLUMNTYPE.TEXT,
                },
            ],
            selectedColumns: [],
            tableSelection: [],
            expandedRows: [],
        };
    },
    created() {
        this.fetchSelectedColumnsFromLocalStorage();
    },
    methods: {
        print(value: any) {
            console.log(value);
        },
        // setupFilters() {
        //     //add dynamically generated filters for each column
        //     let filters: any = Object.assign(this.filters);
        //     for (const column of this.columns) {
        //         filters[column.field] = this.defaultFilter(column);
        //     }
        //     this.filters = filters;
        // },

        setupFilters() {
                this.filters = {
                    global: { value: '', matchMode: FilterMatchMode.CONTAINS }
                };
        },
            // … leave clearColumnFilter, clearTableFilters as-is (or tweak them too) …

        fetchSelectedColumnsFromLocalStorage() {
            const objectString = localStorage.getItem(
                "BrowserVisibleCollectionTableColumns"
            );

            this.selectedColumns = objectString
                ? JSON.parse(objectString)
                : Object.assign(this.columns);
        },
        storeSelectedColumnsToLocalStorage() {
            localStorage.setItem(
                "BrowserVisibleCollectionTableColumns",
                JSON.stringify(this.selectedColumns)
            );
        },
        expandAll() {
            const coll: [] = this.collections;
            this.expandedRows = coll.filter((p: any) => p.id);
        },
        collapseAll() {
            this.expandedRows = [];
        },
        onToggle(value: any) {
            const filteredColumns: any = this.columns.filter((col: any) => {
                return value.includes(col);
            });
            this.selectedColumns = Object.assign(filteredColumns);
            this.storeSelectedColumnsToLocalStorage();
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
                        value: '',
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
                    this.filters.global.value = '';
                } else {
                    const column: any = this.columns.filter(
                        (c) => c.field === key
                    )[0];
                    this.clearColumnFilter(column);
                }
            }
        },
    },
});
</script>


<style scoped>
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
