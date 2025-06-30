<!--<template lang="pug">-->
<!--DataTable.p-datatable-sm(-->
<!--    :value="attribute_quality_values",-->
<!--    :dataKey="attribute_quality_values.quality_characteristic_name",-->
<!--    responsiveLayout="scroll",-->
<!--    rowHover,-->
<!--    showGridlines,-->
<!--    removableSort,-->
<!--    stripedRows-->
<!--)-->
<!--    template(#header)-->
<!--          .table-header Attribute Quality Characteristics-->
<!--    Column(-->
<!--        v-for="col of columns",-->
<!--        :field="col.field",-->
<!--        :header="col.header",-->
<!--        :key="col.field",-->
<!--        sortable-->
<!--    )-->
<!--        template(#body="slotProps")-->
<!--            div.strong(v-if="is_matched_concept && selected_attribute_quality_characteristic === slotProps.data.quality_characteristic_name") {{ slotProps.data[col.field] }}-->
<!--            div(v-else) {{ slotProps.data[col.field] }}-->
<!--</template>-->


<!--<script setup lang="ts">-->
<!--import { ref } from "vue";-->
<!--import DataTable from "primevue/datatable";-->
<!--import Column from "primevue/column";-->
<!--import ColumnGroup from "primevue/columngroup";-->
<!--import Row from "primevue/row";-->
<!--import Button from "primevue/button";-->
<!--</script>-->

<!--<script lang="ts">-->
<!--import { defineComponent } from "vue";-->

<!--const enum COLUMNTYPE {-->
<!--    TEXT = "text",-->
<!--    NUMERIC = "numeric",-->
<!--    DATE = "date",-->
<!--}-->

<!--export default defineComponent({-->
<!--    props: {-->
<!--      attribute_quality_values: [],-->
<!--      selected_attribute_quality_characteristic: String,-->
<!--      is_matched_concept: Boolean-->
<!--    },-->
<!--    async mounted() {-->
<!--      console.log(this.attribute_quality_values);-->
<!--      console.log(this.selected_attribute_quality_characteristic);-->
<!--      console.log(this.is_matched_concept);-->
<!--    },-->
<!--    data() {-->
<!--        return {-->
<!--            columns: [-->
<!--                {-->
<!--                    field: "friendly_name",-->
<!--                    header: "Quality Characteristic",-->
<!--                    type: COLUMNTYPE.TEXT,-->
<!--                },-->
<!--                {-->
<!--                    field: "quality_value",-->
<!--                    header: "Value",-->
<!--                    type: COLUMNTYPE.NUMERIC,-->
<!--                },-->
<!--            ],-->
<!--        };-->
<!--    },-->
<!--    methods: {-->
<!--      matchQA: function(qa: any) {-->
<!--        console.log("q="+this.selected_attribute_quality_characteristic)-->
<!--        console.log(qa)-->
<!--        return this.selected_attribute_quality_characteristic == qa.data.quality_characteristic_name-->
<!--      }-->
<!--    }-->
<!--});-->
<!--</script>-->


<!--<style scoped>-->
<!--button {-->
<!--    margin-left: 10px;-->
<!--    color: var(&#45;&#45;primary-color);-->
<!--}-->
<!--.strong {-->
<!--    font-weight: bold;-->
<!--    color: darkgreen;-->
<!--}-->
<!--</style>-->


<template lang="pug">
DataTable.p-datatable-sm(
    :value="attribute_quality_values"
    dataKey="quality_characteristic_name"
    responsiveLayout="scroll"
    rowHover
    showGridlines
    removableSort
    stripedRows
)
    template(#header)
        .table-header Attribute Quality Characteristics

    Column(
        v-for="col in columns"
        :field="col.field"
        :header="col.header"
        :key="col.field"
        sortable
    )
        template(#body="slotProps")
            div.strong(
                v-if="is_matched_concept && selected_attribute_quality_characteristic === slotProps.data.quality_characteristic_name"
            ) {{ slotProps.data[col.field] }}
            div(v-else) {{ slotProps.data[col.field] }}
</template>

<script setup lang="ts">
import { ref, toRefs, onMounted } from 'vue'
import DataTable from 'primevue/datatable'
import Column    from 'primevue/column'

// 1) Declare props
interface AttributeQualityValue {
    quality_characteristic_name: string
    friendly_name:               string
    quality_value:               number
}

const props = defineProps<{
    attribute_quality_values: AttributeQualityValue[]
    selected_attribute_quality_characteristic: string
    is_matched_concept: boolean
}>()

// 2) Destructure props via toRefs
const {
    attribute_quality_values,
    selected_attribute_quality_characteristic,
    is_matched_concept
} = toRefs(props)

// 3) Column definitions
const columns = ref([
    { field: 'friendly_name', header: 'Quality Characteristic' },
    { field: 'quality_value', header: 'Value' }
])

// 4) Log on mount (replicates your `mounted()` in Options API)
onMounted(() => {
    console.log("attribute_quality_values.value");
    console.log(attribute_quality_values.value);
    console.log("selected_attribute_quality_characteristic.value");
    console.log(selected_attribute_quality_characteristic.value);
    console.log("is_matched_concept.value");
    console.log(is_matched_concept.value);
})
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
