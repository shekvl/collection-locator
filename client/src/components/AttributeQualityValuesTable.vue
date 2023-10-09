<template lang="pug">
DataTable.p-datatable-sm(
    :value="attribute_quality_values",
    :dataKey="attribute_quality_values.quality_characteristic_name",
    responsiveLayout="scroll",
    rowHover,
    showGridlines,
    removableSort,
    stripedRows
)
    template(#header)
          .table-header Attribute Quality Characteristics
    Column(
        v-for="col of columns",
        :field="col.field",
        :header="col.header",
        :key="col.field",
        sortable
    )
        template(#body="slotProps")
            div.strong(v-if="is_matched_concept && selected_attribute_quality_characteristic === slotProps.data.quality_characteristic_name") {{ slotProps.data[col.field] }}
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
      attribute_quality_values: [],
      selected_attribute_quality_characteristic: String,
      is_matched_concept: Boolean
    },
    async mounted() {
      // console.log(this.attribute_quality_values);
      // console.log(this.selected_attribute_quality_characteristic);
      console.log(this.is_matched_concept);
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
    methods: {
      matchQA: function(qa: any) {
        console.log("q="+this.selected_attribute_quality_characteristic)
        console.log(qa)
        return this.selected_attribute_quality_characteristic == qa.data.quality_characteristic_name
      }
    }
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
