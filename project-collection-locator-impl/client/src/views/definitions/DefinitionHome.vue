<template lang="pug">
.px-5.w-50.text-start
    h3
        | Anonymization definitions
    p
        | The anonymization definitions provide a centralized way for managing data anonymization.
        | Each definition contains a set of property definitions annotated by OMOP CDM concepts.
        | The definitions are prepared by means of the Collection Locator instance running at a central location.
        | It is then possible to import them into K-Anonymizer either automatically or by means of file transfer,
        | then K-Anonymizer allows to apply them to annotated collection data to form anonymized datasets.
        | The resulting datasets will contain only the data of the properties annotated by matching concepts.
    Button#new.btn.btn-primary(class="p-button-text me-2" icon="pi pi-plus" label="New definition" @click.prevent="router.push('/definitions/new')")
    div(v-if="definitions.length")
        Card(v-for="def in definitions" :key="def.id" class="mb-3")
            template(#title)
                | Definition: {{ def.name }} ({{ def.id }})
            template(#content)
                h3
                    | Anonymization settings
                .p-3
                    p.mb-1
                        strong TargetK:
                        |  {{ def.targetK }}
                    p.mb-1
                        strong Fast:
                        |  {{ def.fast }}
                    p.mb-3
                        strong Batch:
                        |  {{ def.batch }}
                h3
                    | Properties to be anonymized
                DataTable.p-datatable-sm(
                    ref="dt",
                    :value="def.columns",
                    :dataKey="def.columns.name",
                    responsiveLayout="scroll",
                    rowHover,
                    showGridlines,
                    :rows="10",
                    removableSort,
                    sortMode="multiple",
                    stateStorage="local",
                    stateKey="collectionBrowserTableState",
                    stripedRows
                    :loading="loading"
                )
                    template(#empty) No properties specified
                    Column(field="name" header="Property Name")
                    Column(field="code" header="CDM Code")
                    Column(field="codeText" header="CDM Description")
                .d-flex.flex-wrap.mt-3
                    Button(icon="pi pi-pencil", label="Edit",
                        class="p-button-text me-2", type="button" @click="editDef(def.id)")
                    Button(icon="pi pi-download", label="Download as JSON",
                        class="p-button-text me-2", @click="getDefinitionJson(def.id, def.name)")
                    Button(icon="pi pi-download" label="Download as XML",
                        class="p-button-text me-2", @click="getDefinitionXml(def.id, def.name)")
                    Button(icon="pi pi-trash",
                        class="p-button-text p-button-danger", label="Delete",
                        @click="confirmDelete(def.id)")
    div.alert.alert-primary(v-else)
        | No Definitions found!
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Card from 'primevue/card';
import Button from 'primevue/button';
import TabView from "primevue/tabview";
import TabPanel from "primevue/tabpanel";
import {
    deleteDefinition,
    getJsonForId,
    getXmlForId,
    getXmlSchema,
    listDefinitions
} from "@/requests/dbRequestFunctions";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

const router = useRouter();
const definitions = ref([]);

async function fetchList() {
    const result = await listDefinitions();
    if (result.success) {
        definitions.value = result.data;
    } else {
        console.error('Error fetching definitions:', result.message);
    }
}
onMounted(fetchList);

function confirmDelete(id: string) {
    if (window.confirm('Are you sure you want to delete this definition?')) {
        deleteDef(id);
    }
}

function editDef(id: string) {
    router.push({ name: 'DefinitionEdit', params: { id } });
}

async function deleteDef(id: string) {
    const result = await deleteDefinition(id);
    if (result.success) {
        fetchList();
    } else {
        console.error('Error deleting definition:', result.message);
    }
}

async function getXml() {
    try {
        const result = await getXmlSchema();
        if (result.success) {
            // create a blob URL for the returned XSD
            const blob = new Blob([result.data], {type: 'application/xml'});
            const url = window.URL.createObjectURL(blob);

            // create a hidden anchor, click it, then clean up
            const a = document.createElement('a');
            a.href = url;
            a.download = 'schema.xsd';           // Suggest a filename
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        } else {
            console.error('Error fetching XML schema:', result.message);
        }
    } catch (e) {
        console.error('Unexpected error:', e);
    }
}

async function getDefinitionXml(id: string, name: string) {
    try {
        const result = await getXmlForId(id);
        if (result.success) {
            // create a blob URL for the returned XML
            const blob = new Blob([result.data], {type: 'application/xml'});
            const url = window.URL.createObjectURL(blob);

            // create a hidden anchor, click it, then clean up
            const a = document.createElement('a');
            a.href = url;
            a.download = name + '.xml';           // Suggest a filename
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        } else {
            console.error('Error fetching XML data:', result.message);
        }
    } catch (e) {
        console.error('Unexpected error:', e);
    }
}

async function getDefinitionJson(id: string, name: string) {
    try {
        const result = await getJsonForId(id);
        if (result.success) {
            // create a blob URL for the returned JSON
            const blob = new Blob([result.data], {type: 'application/json'});
            const url = window.URL.createObjectURL(blob);

            // create a hidden anchor, click it, then clean up
            const a = document.createElement('a');
            a.href = url;
            a.download = name + '.json';           // Suggest a filename
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        } else {
            console.error('Error fetching JSON data:', result.message);
        }
    } catch (e) {
        console.error('Unexpected error:', e);
    }

}

</script>

<style>
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
</style>

<style scoped>
a {
    margin-right: 20px;
}

#search-button,
#search-mode {
    margin: 2px;
    min-width: fit-content;
    height: 42px;
}

.p-autocomplete {
    margin: 2px;
    height: fit-content;
}

.selection-panel {
    width: auto;
    height: 200px;
    border: solid 1px #e6e9ec;
    border-radius: 8px;
    overflow-y: auto;
    min-width: fit-content;
}

#new {
    align: left;
}
</style>
