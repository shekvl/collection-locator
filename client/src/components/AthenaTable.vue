<!--<template lang="pug">-->
<!--DataTable.p-datatable-sm(-->
<!--    :value="concepts",-->
<!--    :dataKey="concepts.concept_id",-->
<!--    responsiveLayout="scroll",-->
<!--    rowHover,-->
<!--    showGridlines,-->
<!--    scrollable,-->
<!--    scrollHeight="170px"-->
<!--    removableSort,-->
<!--    stripedRows,-->
<!--    :loading="isLoading"-->
<!--)-->
<!--    Column(-->
<!--        v-for="col of columns",-->
<!--        :field="col.field",-->
<!--        :header="col.header",-->
<!--        :key="col.field",-->
<!--        sortable-->
<!--    )-->
<!--        template(#body="slotProps")-->
<!--            .d-flex.concept_id(v-if="col.field === 'id'")-->
<!--                button.mr-2(-->
<!--                    @click="emit('conceptIdSelected', slotProps.data.id+ ': '+slotProps.data.name + ' ('+ slotProps.data.vocabulary + ')')"-->
<!--                    v-tooltip="'Add to searchbar'"-->
<!--                )-->
<!--                    i.pi.pi-plus-->
<!--                div {{ slotProps.data.id }}-->

<!--            div(v-else) {{ slotProps.data[col.field] }}-->
<!--</template>-->


<!--<script setup lang="ts">-->
<!--import { ref } from "vue";-->
<!--import DataTable from "primevue/datatable";-->
<!--import Column from "primevue/column";-->
<!--import ColumnGroup from "primevue/columngroup";-->
<!--import Row from "primevue/row";-->
<!--import Button from "primevue/button";-->
<!--import { defineEmits } from 'vue'-->

<!--const emit = defineEmits<{-->
<!--    (e: 'conceptIdSelected', value: string): void-->
<!--}>()-->
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
<!--        concepts: [],-->
<!--        isLoading: Boolean,-->
<!--    },-->
<!--    data() {-->
<!--        return {-->
<!--            columns: [-->
<!--                {-->
<!--                    field: "id",-->
<!--                    header: "Concept ID",-->
<!--                    type: COLUMNTYPE.NUMERIC,-->
<!--                },-->
<!--                { field: "code", header: "Code", type: COLUMNTYPE.TEXT },-->
<!--                {-->
<!--                    field: "name",-->
<!--                    header: "Name",-->
<!--                    type: COLUMNTYPE.TEXT,-->
<!--                },-->
<!--                {-->
<!--                    field: "vocabulary",-->
<!--                    header: "Vocabulary",-->
<!--                    type: COLUMNTYPE.TEXT,-->
<!--                },-->
<!--                // {-->
<!--                //     field: "className",-->
<!--                //     header: "Class",-->
<!--                //     type: COLUMNTYPE.TEXT,-->
<!--                // },-->
<!--                {-->
<!--                    field: "standardConcept",-->
<!--                    header: "Concept",-->
<!--                    type: COLUMNTYPE.TEXT,-->
<!--                },-->
<!--                {-->
<!--                    field: "invalidReason",-->
<!--                    header: "Validity",-->
<!--                    type: COLUMNTYPE.TEXT,-->
<!--                },-->
<!--                // {-->
<!--                //     field: "domain",-->
<!--                //     header: "Domain",-->
<!--                //     type: COLUMNTYPE.TEXT,-->
<!--                // },-->
<!--            ],-->
<!--        };-->
<!--    },-->
<!--});-->
<!--</script>-->


<!--<style scoped>-->
<!--button {-->
<!--    margin-left: 10px;-->
<!--    color: var(&#45;&#45;primary-color);-->
<!--}-->
<!--</style>-->

<!--<template lang="pug">-->
<!--DataTable.p-datatable-sm(-->
<!--    :value="concepts"-->
<!--    :dataKey="'concept_id'"-->
<!--    responsiveLayout="scroll"-->
<!--    rowHover-->
<!--    showGridlines-->
<!--    scrollable-->
<!--    scrollHeight="170px"-->
<!--    removableSort-->
<!--    stripedRows-->
<!--    :loading="isLoading"-->
<!--)-->
<!--    Column(-->
<!--        v-for="col in columns"-->
<!--        :field="col.field"-->
<!--        :header="col.header"-->
<!--        :key="col.field"-->
<!--        sortable-->
<!--    )-->
<!--        template(#body="slotProps")-->
<!--            .d-flex.concept_id(v-if="col.field === 'id'")-->
<!--                button.mr-2(-->
<!--                    @click="onConceptIdSelected(slotProps.data)"-->
<!--                    v-tooltip="'Add to searchbar'"-->
<!--                )-->
<!--                    i.pi.pi-plus-->
<!--                div {{ slotProps.data.id }}-->
<!--            div(v-else) {{ slotProps.data[col.field] }}-->
<!--</template>-->

<!--<script lang="ts">-->
<!--import { defineComponent, ref, PropType } from 'vue'-->
<!--import DataTable from 'primevue/datatable'-->
<!--import Column from 'primevue/column'-->

<!--type Concept = {-->
<!--    id: number-->
<!--    code: string-->
<!--    name: string-->
<!--    vocabulary: string-->
<!--    standardConcept: string-->
<!--    invalidReason: string-->
<!--}-->

<!--export default defineComponent({-->
<!--    name: 'ConceptTable',-->
<!--    components: { DataTable, Column },-->
<!--    props: {-->
<!--        concepts: {-->
<!--            type: Array as PropType<Concept[]>,-->
<!--            required: true-->
<!--        },-->
<!--        isLoading: {-->
<!--            type: Boolean,-->
<!--            default: false-->
<!--        }-->
<!--    },-->
<!--    emits: ['conceptIdSelected'],-->
<!--    setup(props, { emit }) {-->
<!--        const columns = ref([-->
<!--            { field: 'id',              header: 'Concept ID' },-->
<!--            { field: 'code',            header: 'Code'       },-->
<!--            { field: 'name',            header: 'Name'       },-->
<!--            { field: 'vocabulary',      header: 'Vocabulary' },-->
<!--            { field: 'standardConcept', header: 'Concept'    },-->
<!--            { field: 'invalidReason',   header: 'Validity'   }-->
<!--        ])-->

<!--        function onConceptIdSelected(row: Concept) {-->
<!--            // build the same string you had before:-->
<!--            const payload = `${row.id}: ${row.name} (${row.vocabulary})`-->
<!--            emit('conceptIdSelected', payload)-->
<!--        }-->

<!--        return {-->
<!--            columns,-->
<!--            onConceptIdSelected-->
<!--        }-->
<!--    }-->
<!--})-->
<!--</script>-->

<!--<style scoped>-->
<!--button {-->
<!--    margin-left: 10px;-->
<!--    color: var(&#45;&#45;primary-color);-->
<!--}-->
<!--</style>-->


<template lang="pug">
DataTable.p-datatable-sm(
    :value="concepts"
    dataKey="concept_id"
    responsiveLayout="scroll"
    rowHover
    showGridlines
    scrollable
    scrollHeight="170px"
    removableSort
    stripedRows
    :loading="isLoading"
)
    Column(
        v-for="col in columns"
        :field="col.field"
        :header="col.header"
        :key="col.field"
        sortable
    )
        template(#body="slotProps")
            .d-flex.concept_id(v-if="col.field === 'id'")
                button.mr-2(
                    @click="onClickPlus(slotProps.data)"
                    v-tooltip="'Add to searchbar'"
                )
                    i.pi.pi-plus
                div {{ slotProps.data.id }}
            div(v-else) {{ slotProps.data[col.field] }}
</template>

<script setup lang="ts">
    // 1) Pull in only the real imports
    import { ref, toRefs } from 'vue'
    import DataTable from 'primevue/datatable'
    import Column from 'primevue/column'

    // 2) Declare your props
    interface Concept {
    id: number
    code: string
    name: string
    vocabulary: string
    standardConcept: string
    invalidReason: string
}

    const props = defineProps<{
    concepts: Concept[]
    isLoading: boolean
    }>()

    // 3) Destructure them safely via toRefs
    const { concepts, isLoading } = toRefs(props)

    // 4) Declare your custom event
    const emit = defineEmits<{
    (e: 'conceptIdSelected', value: string): void
    }>()

    // 5) Column definitions as a reactive array
    const columns = ref([
    { field: 'id',              header: 'Concept ID' },
    { field: 'code',            header: 'Code'       },
    { field: 'name',            header: 'Name'       },
    { field: 'vocabulary',      header: 'Vocabulary' },
    { field: 'standardConcept', header: 'Concept'    },
    { field: 'invalidReason',   header: 'Validity'   }
    ])

    // 6) Handler that builds your payload string and emits it
    function onClickPlus(row: Concept) {
    const payload = `${row.id}: ${row.name} (${row.vocabulary})`
    emit('conceptIdSelected', payload)
}
</script>

<style scoped>
button {
    margin-left: 10px;
    color: var(--primary-color);
}
</style>
