<!--<template lang="pug">-->
<!--DataTable.p-datatable-sm(-->
<!--    :value="attributes",-->
<!--    :dataKey="attributes.concept_id",-->
<!--    v-model:expandedRows="expandedRows",-->
<!--    responsiveLayout="scroll",-->
<!--    rowHover,-->
<!--    showGridlines,-->
<!--    scrollable,-->
<!--    scrollHeight="380px",-->
<!--    removableSort,-->
<!--    stripedRows-->
<!--)-->
<!--    template(#header)-->
<!--        .table-header Collection Attributes and Related Concepts-->

<!--    template(#expansion="slotProps")-->
<!--        .v-container-->
<!--            .v-row-->
<!--                AttributeQualityValuesTable.v-col-12(-->
<!--                  :attribute_quality_values="attribute_quality_values.filter((a)=> a.attribute_name == slotProps.data.attribute_name)"-->
<!--                  :selected_attribute_quality_characteristic="selected_attribute_quality_characteristic_name"-->
<!--                  :is_matched_concept="matchConcepts(slotProps.data.concept_id)"-->
<!--                )-->

<!--    Column(:expander="true", headerStyle="width: 3rem", :reorderableColumns="false")-->
<!--    Column(-->
<!--        v-for="col of columns",-->
<!--        :field="col.field",-->
<!--        :header="col.header",-->
<!--        :key="col.field"-->
<!--        sortable-->
<!--    )-->
<!--        template(#body="slotProps")-->
<!--            .d-flex.concept_id(v-if="col.field === 'concept_id'")-->
<!--                button.mr-2(-->
<!--                    @click="$emit('conceptIdSelected', slotProps.data.concept_id)",-->
<!--                    v-tooltip="'Add to searchbar'"-->
<!--                )-->
<!--                    i.pi.pi-plus-->
<!--                div.strong(v-if="matchConcepts(slotProps.data.concept_id)") {{ slotProps.data.concept_id }}-->
<!--                div(v-else) {{ slotProps.data.concept_id }}-->

<!--            div(v-else)-->
<!--                span.strong(v-if="matchConcepts(slotProps.data.concept_id)") {{ slotProps.data[col.field] }}-->
<!--                span(v-else) {{ slotProps.data[col.field] }}-->
<!--</template>-->


<!--<script setup lang="ts">-->
<!--import { ref } from "vue";-->
<!--import DataTable from "primevue/datatable";-->
<!--import Column from "primevue/column";-->
<!--import ColumnGroup from "primevue/columngroup";-->
<!--import Row from "primevue/row";-->
<!--import Button from "primevue/button";-->
<!--import AttributeQualityValuesTable from "@/components/AttributeQualityValuesTable.vue";-->
<!--</script>-->

<!--<script lang="ts">-->
<!--import { defineComponent } from "vue";-->
<!--import {real_id} from "@/requests/dbRequestFunctions";-->

<!--const enum COLUMNTYPE {-->
<!--    TEXT = "text",-->
<!--    NUMERIC = "numeric",-->
<!--    DATE = "date",-->
<!--}-->

<!--export default defineComponent({-->
<!--    props: {-->
<!--        attributes: [],-->
<!--        attribute_quality_values: [],-->
<!--        selected_concepts: [],-->
<!--        selected_attribute_quality_characteristic_name: String-->
<!--    },-->
<!--    async mounted() {-->
<!--      console.log("at:"+this.selected_attribute_quality_characteristic_name)-->
<!--    },-->
<!--    data() {-->
<!--        return {-->
<!--            columns: [-->
<!--                {-->
<!--                    field: "attribute_name",-->
<!--                    header: "Attribute Name",-->
<!--                    type: COLUMNTYPE.TEXT,-->
<!--                },-->
<!--                {-->
<!--                  field: "concept_id",-->
<!--                  header: "Concept ID",-->
<!--                  type: COLUMNTYPE.NUMERIC,-->
<!--                },-->
<!--                {-->
<!--                    field: "vocabulary_id",-->
<!--                    header: "Vocabulary",-->
<!--                    type: COLUMNTYPE.TEXT,-->
<!--                },-->
<!--                { field: "code", header: "Code", type: COLUMNTYPE.TEXT },-->
<!--            ],-->
<!--          expandedRows: [],-->
<!--        };-->
<!--    },-->
<!--    methods: {-->
<!--      matchConcepts: function (concept_id: any) {-->
<!--        for (let c of this.selected_concepts || []) {-->
<!--          if (real_id(c) == concept_id) return true;-->
<!--        }-->
<!--        return false;-->
<!--      },-->
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
    :value="attributes"
    dataKey="concept_id"
    v-model:expandedRows="expandedRows"
    responsiveLayout="scroll"
    rowHover
    showGridlines
    scrollable
    scrollHeight="380px"
    removableSort
    stripedRows
)
    // table header
    template(#header)
        .table-header Collection Attributes and Related Concepts

    // expansion panel
    template(#expansion="slotProps")
        .v-container
            .v-row
                AttributeQualityValuesTable.v-col-12(
                    :attribute_quality_values="attribute_quality_values.filter(a => a.attribute_name === slotProps.data.attribute_name)"
                    :selected_attribute_quality_characteristic="selected_attribute_quality_characteristic_name"
                    :is_matched_concept="matchConcepts(slotProps.data.concept_id)"
                )

    // expander control
    Column(:expander="true" headerStyle="width:3rem" :reorderableColumns="false")

    // your data columns
    Column(
        v-for="col in columns"
        :field="col.field"
        :header="col.header"
        :key="col.field"
        sortable
    )
        template(#body="slotProps")
            .d-flex.concept_id(v-if="col.field === 'concept_id'")
                button.mr-2(
                    @click="onConceptClick(slotProps.data.concept_id)"
                    v-tooltip="'Add to searchbar'"
                )
                    i.pi.pi-plus
                // bold if matched
                div.strong(v-if="matchConcepts(slotProps.data.concept_id)") {{ slotProps.data.concept_id }}
                div(v-else) {{ slotProps.data.concept_id }}
            // all other fields
            div(v-else)
                span.strong(v-if="matchConcepts(slotProps.data.concept_id)") {{ slotProps.data[col.field] }}
                span(v-else) {{ slotProps.data[col.field] }}
</template>

<script setup lang="ts">
import { ref, toRefs, onMounted } from 'vue'
import DataTable from 'primevue/datatable'
import Column   from 'primevue/column'
import AttributeQualityValuesTable from '@/components/AttributeQualityValuesTable.vue'
import { real_id } from '@/requests/dbRequestFunctions'

// 1) Props
interface Attribute {
    attribute_name: string
    concept_id:      number
    vocabulary_id:   string
    code:            string
}
interface AttributeQualityValue { attribute_name: string; /*…*/ }

const props = defineProps<{
    attributes: Attribute[]
    attribute_quality_values: AttributeQualityValue[]
    selected_concepts: string[]
    selected_attribute_quality_characteristic_name: string
}>()

// 2) Destructure props to refs
const {
    attributes,
    attribute_quality_values,
    selected_concepts,
    selected_attribute_quality_characteristic_name
} = toRefs(props)

// 3) Emit declaration
const emit = defineEmits<{
    (e: 'conceptIdSelected', value: number): void
}>()

// 4) Local reactive state
const columns = ref([
    { field: 'attribute_name', header: 'Attribute Name' },
    { field: 'concept_id',      header: 'Concept ID'       },
    { field: 'vocabulary_id',   header: 'Vocabulary'       },
    { field: 'code',            header: 'Code'             }
])
const expandedRows = ref<any[]>([])

// 5) Methods
function matchConcepts(id: number) {
    // console.log("id=",id);

    return selected_concepts.value.some(c => { console.log("c="+c); return real_id(c) === id})
}

function onConceptClick(id: number) {
    emit('conceptIdSelected', id)
}

// 6) Mounted hook for logging
onMounted(() => {
    console.log('selected attribute quality:', selected_attribute_quality_characteristic_name.value)
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
