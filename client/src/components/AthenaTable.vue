<template lang="pug">
DataTable.p-datatable-sm(
    :value="concepts",
    :dataKey="concepts.concept_id",
    responsiveLayout="scroll",
    rowHover,
    showGridlines,
    removableSort,
    stripedRows,
    :loading="isLoading"
)
    Column(
        v-for="col of columns",
        :field="col.field",
        :header="col.header",
        :key="col.field",
        sortable
    )
        template(#body="slotProps")
            .d-flex.concept_id(v-if="col.field === 'id'")
                button.mr-2(
                    @click="$emit('conceptIdSelected', slotProps.data.id)",
                    v-tooltip="'Add to searchbar'"
                )
                    i.pi.pi-plus
                div {{ slotProps.data.id }}

            div(v-else) {{ slotProps.data[col.field] }}
</template>


<script setup lang="ts">
import { ref } from "vue";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import ColumnGroup from "primevue/columngroup";
import Row from "primevue/row";
import Button from "primevue/button";
</script>

<script lang="ts">
import { defineComponent } from "vue";

const enum COLUMNTYPE {
    TEXT = "text",
    NUMERIC = "numeric",
    DATE = "date",
}

export default defineComponent({
    props: {
        concepts: [],
        isLoading: Boolean,
    },
    data() {
        return {
            columns: [
                {
                    field: "id",
                    header: "Concept ID",
                    type: COLUMNTYPE.NUMERIC,
                },
                { field: "code", header: "Code", type: COLUMNTYPE.TEXT },
                {
                    field: "name",
                    header: "Name",
                    type: COLUMNTYPE.TEXT,
                },
                {
                    field: "vocabulary",
                    header: "Vocabulary",
                    type: COLUMNTYPE.TEXT,
                },
                // {
                //     field: "className",
                //     header: "Class",
                //     type: COLUMNTYPE.TEXT,
                // },
                // {
                //     field: "standardConcept",
                //     header: "Concept",
                //     type: COLUMNTYPE.TEXT,
                // },
                // {
                //     field: "invalidReason",
                //     header: "Validity",
                //     type: COLUMNTYPE.TEXT,
                // },
                // {
                //     field: "domain",
                //     header: "Domain",
                //     type: COLUMNTYPE.TEXT,
                // },
            ],
        };
    },
});
</script>


<style scoped>
button {
    margin-left: 10px;
    color: var(--primary-color);
}
</style>