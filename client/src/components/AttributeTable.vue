<template lang="pug">
DataTable.p-datatable-sm(
    :value="attributes",
    :dataKey="attributes.concept_id",
    responsiveLayout="scroll",
    rowHover,
    showGridlines,
    removableSort,
    stripedRows
)
    Column(
        v-for="col of columns",
        :field="col.field",
        :header="col.header",
        :key="col.field",
        sortable
    )
        template(#body="slotProps")
            .d-flex.concept_id(v-if="col.field === 'concept_id'")
                div {{ slotProps.data.concept_id }}
                button(
                    @click="$emit('conceptIdSelected', slotProps.data.concept_id)",
                    title="Add concept id to searchbar"
                ) add
                //- todo tooltip?

            div(v-else) {{ slotProps.data[col.field] }}
</template>


<script setup lang="ts">
import { ref } from "vue";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import ColumnGroup from "primevue/columngroup";
import Row from "primevue/row";
import Button from "primevue/button"
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
        attributes: [],
    },
    data() {
        return {
            columns: [
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
        };
    },
});
</script>


<style scoped>
.concept_id {
    /* cursor: pointer; */
    /* font-weight: bold;
    font-size: large; */
}

button{
    margin-left:10px;
    color: var(--primary-color);
}
</style>