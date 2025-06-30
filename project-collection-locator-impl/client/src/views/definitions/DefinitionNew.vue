<template lang="pug">
.px-5.w-75
    Card(:key="definition.id" class="mb-3")
        template(#title)
            h3
                | New Anonymization Definition
        template(#content)
            form(@submit.prevent="save")
                .row
                    label.mr-3(for="name") Name:
                    InputText#nameinput.form-control(v-model="definition.name")
                h3.mt-5
                    | Anonymization settings
                .row.mb-2
                    label.mr-2(for="batchEdit") Batch Size:
                    InputText.form-control(v-model="definition.batch")
                    label.mr-2.ml-5(for="targetK") TargetK:
                    InputText.form-control(v-model="definition.targetK")
                .row.mb-2
                    label.mr-2(for="fastEdit") Fast:
                    Checkbox(inputId="fastEdit" binary="true" v-model="definition.fast")
                h3.mt-5
                    | Properties to be anonymized
                Button(icon="pi pi-plus" class="p-button-text me-2 mb-4" label="Add Property" @click.prevent="addColumn")
                div.mb-2(v-for="(col, idx) in definition.columns" :key="idx")
                    label.mr-2(for="") Property Name:
                    InputText.w-25.form-control(placeholder="Column Name" v-model="col.name")
                    label.mr-2.ml-5(for="") CDM Concept:
                    CDMSelect(v-model="col.cdm" @change="test")
                    Button.p-button-text.p-button-danger(icon="pi pi-trash" @click.prevent="removeColumn(idx)")
                .row.mt-3
                    Button.btn.btn-primary(type="submit" label="Save" icon="pi pi-save")
                    Button(class="p-button-text me-2" label="Back to definitions list" @click.prevent="router.push('/definitions-home')")
                    //.alert.alert-success.mt-2(v-if="saved")
                        | Changes have been saved
                    span.success.mx-sm-3(v-if="saved") Changes have been saved!
</template>

<script setup>
import { reactive, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
    createDefinitionDirect,
    getDefinition,
    getDefinitionForId,
    getJsonForId,
    updateDefinition,
    updateDefinitionDirect
} from "@/requests/dbRequestFunctions";
import InputText from "primevue/inputtext";
import Checkbox from "primevue/checkbox";
import InputNumber from "primevue/inputnumber";
import Button from "primevue/button";

import Card from "primevue/card";
import CDMSelect from "@/views/definitions/CDMSelect.vue";

const route = useRoute();
const router = useRouter();
const definition = reactive({ name: '', targetK: "", fast: false, batch: "", columns: [] });
const loaded = ref(false);
const saved = ref(false);
var codelabel = "";

function getSaved() {
    return saved.value;
}

function addColumn() {
    definition.columns.push({ name: '', code: '' });
}
function removeColumn(idx) {
    definition.columns.splice(idx, 1);
}

function test(e) {
    console.log(e.target.id);
}

async function save() {
    await createDefinitionDirect(definition);
    saved.value = true;
    router.push('/definitions-home')
} </script>
<style>
form {
    text-align: left!important;
    font-size: 11pt!important;
}
.p-tabview-panel {
    display: flex;
    justify-content: flex-start;
}

.p-dropdown {
    min-width: 12rem;
    width: fit-content;
}

.p-dropdown-label {
    text-align: start;
}
.p-card {
    text-align: left!important;
    font-size: 11pt!important;
}
.p-card .p-card-title {
    font-size: 14pt!important;
}
.p-component {
    font-family: Avenir, Helvetica, Arial, sans-serif;
}
h3 {
    margin-bottom: 10px;
    color: gray;
    text-align: left!important;
}
.success {
    color: green!important;
}
.v-select {
    display: inline-block;
    width: 55%!important;
    min-width: 11em;
}
.p-button {
    vertical-align: middle!important;
}
#nameinput {
    width: 95%!important;
}
</style>
