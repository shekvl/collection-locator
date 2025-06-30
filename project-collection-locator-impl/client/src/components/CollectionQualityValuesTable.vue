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
            div.strong(v-if="selected_quality_characteristic === slotProps.data.quality_characteristic_name") {{ slotProps.data[col.field] }}
            div(v-if="selected_quality_characteristic !== slotProps.data.quality_characteristic_name") {{ slotProps.data[col.field] }}
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
      selected_quality_characteristic: String
    },
    data() {
        return {
            columns: [
                {
                    field: "friendly_name",
                    header: "Quality Characteristic",
                    type: COLUMNTYPE.TEXT,
                },
                {
                    field: "quality_value",
                    header: "Value",
                    type: COLUMNTYPE.NUMERIC,
                },
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
.strong {
    font-weight: bold;
    color: darkgreen;
}
</style>
