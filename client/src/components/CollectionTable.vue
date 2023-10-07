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
    stateStorage="local",
    stateKey="collectionBrowserTableState",
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
                span.p-input-icon-left
                    i.pi.pi-search
                    InputText.p-inputtext-sm(
                        v-model="filters['global'].value",
                        placeholder="Keyword Search",
                    )
    template(#expansion="slotProps")
        AttributeTable(:attributes="attributes.filter((a)=> a.collection_id === slotProps.data.id)", @conceptIdSelected="(value) => $emit('conceptIdSelected', value)")

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
        loading: Boolean,
    },
    data() {
        return {
            filters: {
                global: { value: null, matchMode: FilterMatchMode.CONTAINS },
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
                // {
                //     field: "completeness",
                //     header: "Completeness",
                //     type: COLUMNTYPE.NUMERIC,
                // },
                // {
                //     field: "accuracy",
                //     header: "Accuracy",
                //     type: COLUMNTYPE.NUMERIC,
                // },
                // {
                //     field: "reliability",
                //     header: "Reliability",
                //     type: COLUMNTYPE.NUMERIC,
                // },
                // {
                //     field: "timeliness",
                //     header: "Timeliness",
                //     type: COLUMNTYPE.NUMERIC,
                // },
                // {
                //     field: "consistancy",
                //     header: "Consistency",
                //     type: COLUMNTYPE.NUMERIC,
                // },
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
        setupFilters() {
            //add dynamically generated filters for each column
            let filters: any = Object.assign(this.filters);
            for (const column of this.columns) {
                filters[column.field] = this.defaultFilter(column);
            }
            this.filters = filters;
        },
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
