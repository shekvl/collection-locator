<template lang="pug">
DataTable.p-datatable-sm(
    :value="collection_quality_values",
    :dataKey="collection_quality_values.quality_characteristic_name",
    responsiveLayout="scroll",
    rowHover,
    showGridlines,
    removableSort,
    stripedRows
)
    template(#header)
          .table-header Collection Quality Characteristics
    Column(
        v-for="col of columns",
        :field="col.field",
        :header="col.header",
        :key="col.field",
        sortable
    )
        template(#body="slotProps")
            div {{ slotProps.data[col.field] }}
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
      collection_quality_values: [],
    },
    data() {
        return {
            columns: [
                {
                    field: "quality_characteristic_name",
                    header: "Quality Characteristic",
                    type: COLUMNTYPE.TEXT,
                },
                {
                    field: "quality_value",
                    header: "Value",
                    type: COLUMNTYPE.NUMERIC,
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
