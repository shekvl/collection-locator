<template lang="pug">
div
    ErrorAlert(v-model="errorTitle") {{ errorDetail }}
    ErrorAlert(v-model="successTitle" type="success") {{ successDetail }}

    v-overlay(:value="isLoading")
        v-progress-circular(indeterminate size="64" color="primary")

    v-sheet(
        color="#78909Cad"
        elevation="2"
        rounded
        :class="rangeQuery ? 'rangeQuerySheet' : 'pointQuerySheet'"
    )
        div(v-if="isLoading")
            div
                v-skeleton-loader(
                    v-for="(input, index) in inputs"
                    :key="index"
                    style="margin: 1.5em;"
                    max-width="200%"
                    min-width="200%"
                    type="heading"
                    fluid
                )
        div(v-else v-for="(input, index) in inputs" :key="index")
            v-form(:class="rangeQuery ? 'formExpand' : 'form'" lazy-validation v-model="valid")
                v-container.container
                    v-row(style="margin-left:-2em; align-self: auto")
                        v-col.mt-2
                            v-combobox(
                                @blur="checkConceptInput(index)"
                                @change="searchConcept"
                                clearable
                                v-model="input.concept"
                                :items="currentConceptList"
                                :item-text="item => item.concept_id +' ('+ item.concept_name + ')'"
                                :item-value="item => item.concept_id"
                                label="Search concept IDs"
                                solo
                                prepend-inner-icon="mdi-magnify"
                                return-object
                                required
                                :rules="rules"
                                :error-messages="conceptErrorTexts"
                            )
                                template(v-slot:selection="{ item }")
                                    v-chip(v-if="item" color="lighten-3" label small)
                                        span {{ item.concept_id }} ({{ item.concept_name }})
                        v-col.mt-2
                            v-text-field(
                                v-model="input.value.fromValue"
                                :label="rangeQuery ? 'FROM value' : 'Value'"
                                solo :rules="rules"
                                :error-messages="errorTexts"
                                required
                            )
                        v-col.mt-2
                            v-text-field(
                                v-if="rangeQuery"
                                v-model="input.value.toValue"
                                label="TO value"
                                solo :rules="rules"
                                :error-messages="errorTexts"
                                required
                            )
                            hr(style="height:10px; visibility:hidden;")
                            span.helperIcons(v-if="!rangeQuery")
                                i.round(@click="remove(index)" v-if="index || inputs.length > 1")
                                    v-icon mdi-minus
                                i.round(@click="add" v-if="index === inputs.length - 1")
                                    v-icon mdi-plus
                        v-col.mt-2(md="2" v-if="rangeQuery")
                            hr(style="height:10px; visibility:hidden;")
                            span.helperIcons
                                i.round(@click="remove(index)" v-if="index || inputs.length > 1")
                                    v-icon mdi-minus
                                i.round(@click="add" v-if="index === inputs.length - 1")
                                    v-icon mdi-plus

    br

    v-sheet(
        color="#78909Cad"
        elevation="2"
        height="120"
        rounded
        style="margin-left:200px;margin-right:200px;padding:1.5em;"
    )
        div(v-if="isLoading")
            v-skeleton-loader(class="fancyButtonSkeleton" max-width="100%" type="button@5" fluid)
        div.center-screen(v-else)
            v-btn(elevation="2" class="fancyButton" @click="submit")
                v-icon mdi-magnify
                | Search
            v-btn(elevation="2" class="fancyButton" @click="clearQueryData")
                v-icon mdi-table-row-remove
                | Clear Query Data
            v-btn(elevation="2" class="fancyButton" @click="simulateSendingData")
                v-icon mdi-lan-pending
                | Simulate biobanks sending data
            v-btn(elevation="2" class="fancyButton" @click="toggleThreshold")
                v-icon mdi-tune
                | Choose Thresholds
            v-switch.fancySwitch(v-model="rangeQuery" inset label="Range Query")

</template>

<script setup>
import { ref } from 'vue'
import ErrorAlert from './ErrorAlert.vue'
import {node2Axios} from "@/requests/axios";

const errorTitle = ref('')
const errorDetail = ref('')
const successTitle = ref('')
const successDetail = ref('')
const isLoading = ref(false)
const valid = ref(true)
const inputs = ref([{ concept: '', value: { fromValue: '', toValue: '' } }])
const rangeQuery = ref(false)
const currentConceptList = ref([])
const datasets = ref([])
const datasetsLoaded = ref(false)
const rules = [v => !!v || 'Required']
const conceptErrorTexts = ref('')
const errorTexts = ref('')
const url = 'http://localhost:5002'

function add() {
    inputs.value.push({ concept: '', value: { fromValue: '', toValue: '' } })
}

function remove(index) {
    inputs.value.splice(index, 1)
}

async function searchConcept(event) {
    if (typeof event === 'string') {
        try {
            const res = await node2Axios.get(`/api/concept?concept=${event}`)
            currentConceptList.value = res.data
        } catch {
            conceptErrorTexts.value = "Concept not found"
            setTimeout(() => conceptErrorTexts.value = '', 4000)
        }
    }
}

function checkConceptInput(index) {
    if (typeof inputs.value[index].concept !== 'object') {
        inputs.value[index].concept = ''
    }
}

async function submit() {
    isLoading.value = true
    try {
        const preparedData = inputs.value.map(input => ({
            concept: input.concept,
            value: {
                fromValue: input.value.fromValue,
                toValue: rangeQuery.value ? input.value.toValue : input.value.fromValue
            }
        }))
        const res = await node2Axios.post(`/api/data`, preparedData)
        datasets.value = res.data
        datasetsLoaded.value = true
        successTitle.value = 'Query matched successfully!'
        successDetail.value = 'Results loaded.'
        setTimeout(() => { successTitle.value = ''; successDetail.value = '' }, 4000)
    } catch (e) {
        errorTitle.value = 'Query Error'
        errorDetail.value = e.message || 'No matches found'
    } finally {
        isLoading.value = false
    }
}

async function simulateSendingData() {
    await node2Axios.post(`/api/saveData`)
}

function clearQueryData() {
    inputs.value = [{ concept: '', value: { fromValue: '', toValue: '' } }]
    datasetsLoaded.value = false
}

function toggleThreshold() {
    // implement as needed
}
</script>

<style scoped>
.container {
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: center;
    align-items: flex-start;
}
.helperIcons { cursor: pointer; }
.round {
    background-color: #D3D3D3;
    border-radius: 50%;
    padding: 0.2em;
}
.round:hover { background-color: #A9A9A9; }
.formExpand, .form {
    margin-top: 2em;
    margin-left: 10em;
}
.pointQuerySheet {
    margin: 1.5em 200px;
    padding: 10px;
}
.rangeQuerySheet {
    margin: 1.5em 100px;
    padding: 10px;
}
.fancyButton {
    margin: 0 1em;
    font-size: 12pt;
}
.center-screen {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 5em;
}
</style>
