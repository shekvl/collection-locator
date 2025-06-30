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
                    v-for="(input, index) in inputs" :key="index"
                    style="margin: 1.5em;"
                    max-width="200%"
                    min-width="200%"
                    type="heading"
                    fluid
                )
        div(v-else v-for="(input, index) in inputs" :key="index")
            v-form(:class="rangeQuery ? 'formExpand' : 'form'" ref="form" lazy-validation v-model="valid")
                v-container.container
                    v-row(style="margin-left:-2em; align-self:auto")
                        v-col.mt-2
                            v-combobox(
                                @blur="checkConceptInput(index)"
                                @change="searchConcept($event)"
                                clearable v-model="input.concept"
                                :items="currentConceptList"
                                :item-text="item => item.concept_id +' ('+ item.concept_name + ')'"
                                :item-value="item => item.concept_id"
                                label="Search concept IDs"
                                solo prepend-inner-icon="mdi-magnify"
                                return-object required
                                :rules="rules"
                                :error-messages="conceptErrorTexts"
                            )
                                template(v-slot:selection="{ attrs, item, selected }")
                                    v-chip(v-if="item === Object(item)" v-bind="attrs" color="lighten-3" :input-value="selected" label small)
                                        span {{ item.concept_id }} ({{item.concept_name}})
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
                                v-show="rangeQuery"
                                v-model="input.value.toValue"
                                label="TO value"
                                solo :rules="rules"
                                :error-messages="errorTexts"
                                required
                            )
                            hr(style="height:10px; visibility:hidden;")
                            span.helperIcons(v-show="!rangeQuery")
                                i.round(@click="remove(index)" v-show="index || (!index && inputs.length > 1)")
                                    v-icon(style="margin-right:0px") mdi-minus
                                i.round(@click="add(index)" v-show="index == inputs.length - 1")
                                    v-icon(style="margin-right:0px") mdi-plus
                        v-col.mt-2(col="6" md="2" v-show="rangeQuery")
                            hr(style="height:10px; visibility:hidden;")
                            span.helperIcons
                                i.round(@click="remove(index)" v-show="index || (!index && inputs.length > 1)")
                                    v-icon(style="margin-right:0px") mdi-minus
                                i.round(@click="add(index)" v-show="index == inputs.length - 1")
                                    v-icon(style="margin-right:0px") mdi-plus

    br

    v-sheet(color="#78909Cad" elevation="2" height="120" rounded style="margin-left:200px;margin-right:200px;padding:1.5em;")
        div(v-if="isLoading")
            div
                v-skeleton-loader(class="fancyButtonSkeleton" style="display:flex;flex-wrap:wrap;flex-direction:row;" max-width="100%" min-width="100%" type="button@5" fluid)
        div.center-screen(v-else)
            v-btn(elevation="2" raised class="fancyButton" @click="submit()")
                v-icon(style="margin-right:0px") mdi-magnify
                | Search
            v-btn(elevation="2" raised class="fancyButton" @click="clearQueryData()")
                v-icon(style="margin-right:0px") mdi-table-row-remove
                | Clear Query Data
            v-btn(elevation="2" raised class="fancyButton" @click="simulateSendingData()")
                v-icon(style="margin-right:0px") mdi-lan-pending
                | Simulate biobanks sending data
            v-btn(elevation="2" raised class="fancyButton" @click="toggleThreshold")
                v-icon(style="margin-right:0px") mdi-tune
                | Choose Thresholds
            v-switch.fancySwitch(dark v-model="rangeQuery" inset label="Range Query")

    v-dialog(v-model="showThreshold" max-width="600" transition="dialog-bottom-transition")
        v-card(v-show="showThreshold" style="width:600px;margin:auto;" elevation="2")
            v-toolbar(color="primary" dark flat height="50px") Choose your desired thresholds below
            div(style="padding:60px 50px 30px 50px")
                v-label Overall expected hit ratio
                br
                v-btn(style="margin:10px 0px 40px 0px;" rounded outlined @click="overallExpectedHitThreshold = !overallExpectedHitThreshold")
                    v-icon(left) mdi-pencil
                    | {{overallExpectedHitThreshold ? 'Hide thresholds' : 'Show thresholds'}}
                v-slider(v-show="overallExpectedHitThreshold" v-model="thresholdGoodValues" label="😄 Good values" thumb-label="always" max="100" min="0" persistent-hint hint="Values over this threshold would be considered good results." color="green" track-color="green" thumb-color="green")
                    template(v-slot:thumb-label="{ value }") {{ value }}
                v-slider(v-show="overallExpectedHitThreshold" v-model="thresholdOkayValues" label="🙂 Okay values" thumb-label="always" max="100" min="0" persistent-hint hint="Values between this threshold and the threshold above would be considered okay-ish results." color="orange" track-color="orange" thumb-color="orange")
                    template(v-slot:thumb-label="{ value }") {{ value }}
                br
                v-label Hit ratio per concept
                br
                v-btn(style="margin:10px 0px 40px 0px;" rounded outlined @click="overallExpectedHitsPerConceptThreshold = !overallExpectedHitsPerConceptThreshold")
                    v-icon(left) mdi-pencil
                    | {{overallExpectedHitsPerConceptThreshold ? 'Hide thresholds' : 'Show thresholds'}}
                v-slider(v-show="overallExpectedHitsPerConceptThreshold" v-model="thresholdGoodValuesPerConcept" label="😄 Good values" thumb-label="always" max="100" min="0" persistent-hint hint="Values over this threshold would be considered good results." color="green" track-color="green" thumb-color="green")
                    template(v-slot:thumb-label="{ value }") {{ value }}
                v-slider(v-show="overallExpectedHitsPerConceptThreshold" v-model="thresholdOkayValuesPerConcept" label="🙂 Okay values" thumb-label="always" max="100" min="0" persistent-hint hint="Values between this threshold and the threshold above would be considered okay-ish results." color="orange" track-color="orange" thumb-color="orange")
                    template(v-slot:thumb-label="{ value }") {{ value }}
                v-text-field(required color="green" v-model="thresholdExpectedHits" label="Overall expected hits" hint="Values over this threshold will appear green." persistent-hint type="number")
                v-btn(text color="primary" @click="toggleThreshold") Close

    div(style="margin:2em; z-index:0")
        h3(style="color:white;" v-show="datasetsLoaded && !isLoading") Time: {{ queryTime }} seconds ({{ (queryTime / 60).toFixed(2)}} minutes)
        div(v-if="isLoading")
            v-skeleton-loader(class="mx-auto" max-width="100%" type="heading,table-row-divider@15,table-tfoot" fluid)
        v-data-table(v-if="datasetsLoaded && !isLoading" id="rankingTable" :headers="headers2" :items="datasets" :single-expand="false" :expanded.sync="expanded" item-key="collectionId" show-expand disable-sort style="margin-top:10px;")
            template(v-slot:top)
                v-toolbar(flat color="white")
                    v-toolbar-title Ranked Results
                    v-spacer
            template(#item.overallPercentage="{value}")
                td.d-flex.justify-center
                    v-chip(:color="getColor(value * 100)" dark) {{ parseFloat(value * 100).toFixed(2) }}


            template(#item.overallExpectedRows="{item}")
                td.d-flex.justify-center
                    v-chip(:color="getColorForExpectedHits(item.overallExpectedRows)" dark) {{ Math.round(item.overallExpectedRows) }}
            template(#expanded-item="{ headers, item }")
                td(:colspan="headers.length")
                    v-row
                        v-col(cols="12" md="12")
                            h4.text(style="display:inline;") Expected hits per concept:
                            v-chip(style="margin:10px;" v-for="(row,index) in item.goodHits" :key="index" dark)
                                v-chip.ma-2(small color="white" outlined) {{ row.code }}:
                                | {{ Math.round(row.revisedNumberOfRows) }}
                    v-divider
                    v-row
                        v-col(cols="12" md="12")
                            h4.text(style="display:inline; margin-right:50px;") Hit ratio per concept:
                            v-chip(style="margin:10px;" v-for="(row,index) in item.goodHits" :key="index" :color="getColorPerConcept(row.goodValuesInPercentage * 100)" dark)
                                v-chip.ma-2(small color="white" outlined) {{ row.code }}:
                                | {{ parseFloat(row.goodValuesInPercentage * 100).toFixed(2) }}
                    v-divider(style="margin-bottom:10px;")
                    v-row
                        v-col(cols="12" md="6" style="margin-bottom:10px;")
                            h4.text(style="display:inline;") Full collection?
                            v-icon(color="green" v-show="!item.definitionId") mdi-checkbox-marked-circle
                            v-icon(color="red" v-show="item.definitionId") mdi-close-circle

    v-footer(ref="footer" :absolute="true" :padless="true" class="footer")
        h6(style="margin-left:10px;font-family:'Arial';") University of Klagenfurt
        v-spacer
        h6 Authors: Blend Dehari (blendde@edu.aau.at), Nermin Topalovic (nerminto@edu.aau.at)
        v-spacer
        h6(style="margin-right:10px;font-family:'Arial';") All rights reserved.
</template>

<script>
import axios from 'axios'
import ErrorAlert from './ErrorAlert.vue'

export default {
    name: 'QueryComponent',
    components: { ErrorAlert },

      data() {
          return {
              url: 'http://localhost:5002',
              valid: true,
              isLoading: false,
              errorTitle: '',
              errorDetail: '',
              successTitle: null,
              successDetail: null,
              rules: [
                v => !!v || 'Required',
              ],
              errorTexts:'',
              conceptErrorTexts: '',
              inputs: [{
                  concept: '',
                  value: {
                      fromValue: '',
                      toValue: ''
                  },
              }],
              rangeQuery: true,
              fullPage: true,
              concepts: [],
              conceptsLoaded: false,
              currentConceptList: [],
              datasetsLoaded: false,
              datasets: [],

              showThreshold: false,
              thresholdGoodValues: 50,
              thresholdOkayValues: 25,
              thresholdGoodValuesPerConcept: 50,
              thresholdOkayValuesPerConcept: 25,
              detailedView: false,
              thresholdExpectedHits: 0,
              expanded: [],
              overallExpectedHitThreshold: false,
              overallExpectedHitsPerConceptThreshold: false,
              queryTime: '',
              temporaryFakeDatasets: [
                    {
                        "biobankId": "biobank_6",
                        "collectionId": "collection_6",
                        "numberOfRows": 46,
                        "goodHits": [
                            {
                                "code": "10156-8",
                                "goodValuesInPercentage": 0.0625,
                                "revisedNumberOfRows": 2.875
                            }
                        ],
                        "overallPercentage": 0.0625,
                        "overallExpectedRows": 2.875
                    },
                    {
                        "biobankId": "biobank_1",
                        "collectionId": "collection_1",
                        "numberOfRows": 15,
                        "goodHits": [
                            {
                                "code": "10156-8",
                                "goodValuesInPercentage": 0.1111111111111111,
                                "revisedNumberOfRows": 1.6666666666666665
                            }
                        ],
                        "overallPercentage": 0.1111111111111111,
                        "overallExpectedRows": 1.6666666666666665
                    }
                ],

              headers: [
                  {
                      text: 'Collection ID',
                      value: "collectionId",
                      align: 'center',
                       width: '200',
                  },
                  {
                      text: 'Biobank name',
                      value: "biobankId",
                      align: 'center',
                       width: '200',
                  },
                  {
                      text: 'Number of rows matched',
                      value: "numberOfRows",
                      align: 'center',
                      width: '300',
                  },
                {
                    text: 'Overall hit ratio',
                    value: 'overallPercentage',
                    align: 'center',
                    width: '300',
                },
                {
                    text: 'Overall expected hits',
                    value: 'overallExpectedRows',
                    align: 'center',
                    width: '300',
                },
                // Support for multiple LOINC codes
                {
                    // this means the "Rows matched within the Number of rows returned"
                    text: 'Expected hits per concept',
                    value: 'revisedRows',
                    align: 'center',
                    width: '400',
                },
                {
                    text: 'Hit ratio per concept',
                    value: "probability",
                    width: '400',
                    align: 'center',

                },

            ],
              headers2: [
                   {
                      text: 'Collection ID',
                      value: "collectionId",
                      align: 'center',
                    //    width: '200',
                  },
                  {
                      text: 'Biobank name',
                      value: "biobankId",
                      align: 'center',
                    //    width: '200',
                  },
                  {
                      text: 'Number of rows matched',
                      value: "numberOfRows",
                      align: 'center',
                    //   width: '300',
                  },
                {
                    text: 'Overall hit ratio',
                    value: 'overallPercentage',
                    align: 'center',
                    // width: '300',
                },
                {
                    text: 'Overall expected hits',
                    value: 'overallExpectedRows',
                    align: 'center',
                    // width: '300',
                },
              ],

          }
      },
      methods: {
          add() {
              this.inputs.push({
                  concept: '',
                  value: {
                      fromValue: '',
                      toValue: ''
                  },
              })
          },
          remove(index) {
              this.inputs.splice(index, 1)
          },

          async simulateSendingData() {
              await axios.post(`${this.url}/api/saveData`)
          },
          consoleLog() {
            //   this.validateForm()
              for (let input of this.inputs) {
                  if (!input.value.toValue) {
                      input.value['toValue'] = input.value['fromValue']
                  }
                  else if (!this.rangeQuery) {
                     input.value['toValue'] = input.value['fromValue']
                  }
              }
              console.log(this.inputs)

          },
          toggleQueryType() {
              this.rangeQuery = !this.rangeQuery
          },
          validateForm() {
               for (let input of this.inputs) {
                      if (input.concept === "" || input.value.fromValue === "" || input.value.toValue === "") {
                           this.valid = false
                           this.errorTexts = "Required"
                           this.conceptErrorTexts = "Required"
                           setTimeout(() => this.errorTexts = "", 2000)
                           setTimeout(() => this.conceptErrorTexts = "", 2000)
                           throw 'Please fill in all the fields!'
                      }
               }
          },

          async submit() {
              try {
                this.isLoading = true
                this.queryTime = new Date()
                for (let input of this.inputs) {
                    if (!input.value.toValue) {
                        input.value['toValue'] = input.value['fromValue']
                    }
                    else if (!this.rangeQuery) {
                        input.value['toValue'] = input.value['fromValue']
                    }
                }
                //   console.log(this.inputs)
                this.validateForm()
                const data = [... this.inputs]
                console.log("DATA TO BACKEND", data)
                const res = await axios.post(`${this.url}/api/data`, data)
                this.datasetsLoaded = true
                this.datasets = res.data
                console.log('RESULTS FROM BACKEND', this.datasets)
                this.successTitle = 'Query matched successfully!'
                this.successDetail = 'See ranked results in the table below'
                setTimeout(() => this.successTitle = "", 4000)
                setTimeout(() => this.successDetail = "", 4000)
                this.isLoading = false
                this.queryTime = (new Date() - this.queryTime) / 1000
              } catch (error) {
                  this.isLoading = false
                  this.datasetsLoaded = false

                  error === 'Please fill in all the fields!' ? this.errorTitle = 'Something went wrong!' : this.errorTitle = 'Could not find matches for your query!'

                  console.log("ERROR HERE", error)

                  if (error.response) {
                      this.errorDetail = error.response.data.error;
                  } else {
                      this.errorDetail = error;
                  }
              }
          },
          async searchConcept(event) {
              try {
                  console.log('search concept:', event)
                //   console.log('THIS', this.inputs)
                  let concept = event
                  if (typeof concept == 'string') {
                      const res = await axios.get(`${this.url}/api/concept?concept=${concept}`)
                      if (res?.status === 200) {
                          this.currentConceptList = res.data
                      }
                  }
              }
              catch(error) {
                  console.log('comes here?')
                  console.log(error)
                  this.currentConceptList = []
                  this.conceptErrorTexts = "concept does not exist! Please try another one."
                  setTimeout(() => this.conceptErrorTexts = "", 4000)
              }
          },
          checkConceptInput(index) {
              if (typeof this.inputs[index].concept != "object") {
                  //this will force a selection on the combobox
                  this.inputs[index].concept = ''
              }
          },
          clearQueryData() {
            this.inputs = [{
                  concept: '',
                  value: {
                      fromValue: '',
                      toValue: ''
                  },
              }],
              this.datasetsLoaded = false
          },
          getColor(hits) {
              if (hits >= this.thresholdGoodValues) return 'green'
              else if (hits >= this.thresholdOkayValues) return 'orange'
              else return 'red'
          },
          getColorForExpectedHits(hits) {
              if (hits >= this.thresholdExpectedHits) return 'green'
              else return 'red'
          },
          getColorPerConcept(hits) {
              if (hits >= this.thresholdGoodValuesPerConcept) return 'green'
              else if (hits >= this.thresholdOkayValuesPerConcept) return 'orange'
              else return 'red'
          },
          toggleThreshold() {
              this.showThreshold = !this.showThreshold
              this.overallExpectedHitThreshold = false
              this.overallExpectedHitsPerConceptThreshold = false
          },
      },
      computed: {
          errorMessages() {
              return ' <span style="font-size:14pt;font-weight:bold;"> ' + this.errorTitle + ' </span><br> ' + this.errorDetail;
          },
      }
  }
</script>

<style>
.container {
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: center;
    align-items: flex-start;
    align-content: flex-start;
    gap: 10px 20px;
}
.button {
    margin-left: 2em
}
.gradientButton {
    min-height: 48px !important;
    background: linear-gradient(0deg, #efefef 0%, #ffffff 100%) !important;
    border: thin #dddddd solid !important;
    display: flex !important;
    /*justify-content: space-around !important;*/
    align-items: center !important;
    border-radius: 5px !important;
    font-family: "Inter";
    font-size: 12pt !important;
    text-transform: none !important;
    letter-spacing: 0px !important;
}
.combobox {
    width: 50%;
    margin-left: 2em;
    position: relative;
}

.helperIcons {
    cursor: pointer;
}
.round {
  -webkit-border-top-left-radius: 1px;
  -webkit-border-top-right-radius: 2px;
  -webkit-border-bottom-right-radius: 3px;
  -webkit-border-bottom-left-radius: 4px;

  -moz-border-radius-topleft: 1px;
  -moz-border-radius-topright: 2px;
  -moz-border-radius-bottomright: 3px;
  -moz-border-radius-bottomleft: 4px;

  border-top-left-radius: 1px;
  border-top-right-radius: 2px;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 4px;
  background-color:  #D3D3D3;
  border-radius: 100%;
  /* width: 100%; */
  padding: 0.2em;
  padding-bottom: 0.3em;
  margin: 0.2em;
  position: relative;
}
.round:hover {
    background-color: #A9A9A9;
}
.formExpand {
    margin-top: 2em;
    margin-left: 10em;
}
.form {
    margin-top: 2em;
    margin-left: 10em;
}
.pointQuerySheet {
    margin-left:200px;margin-right:200px; padding: 10px; margin-top: 1.5em;
}
.rangeQuerySheet {
    margin-left:100px;margin-right:100px; padding: 10px; margin-top: 1.5em;
}
.text {
    font-size: 1.1em;
    font-family: "Arial";
    font-weight: normal ;
    letter-spacing: 0 ;
    text-transform: none ;
}
#rankingTable table thead tr th {
    /* background-color: rgba(182, 183, 187); */
    background-color: #78909C;
    color: white;
    font-size: 1.3em;
    font-family: "Arial";
    font-weight: normal ;
    letter-spacing: 0 ;
    text-transform: none ;
}
#rankingTable table tr td {
    font-size: 1.1em;
    font-family: "Arial";
    font-weight: normal ;
    letter-spacing: 0 ;
    text-transform: none ;
}
.fancyButton {
  height: 48px !important;
  background: linear-gradient(0deg, #efefef 0%, #ffffff 100%) !important;
  border: thin #dddddd solid !important;
  display: flex row !important;
  justify-content: space-around !important;
  align-items: center !important;
  border-radius: 5px !important;
  font-family: "Arial";
  font-size: 12pt !important;
  font-weight: normal !important;
  letter-spacing: 0 !important;
  text-transform: none !important;
  margin-left: 2em
}

.fancySwitch {
    height: 48px !important;
    display: flex row !important;
    justify-content: space-around !important;
    align-items: center !important;
    border-radius: 5px !important;
    font-family: "Arial";
    font-size: 12pt !important;
    font-weight: normal !important;
    letter-spacing: 0 !important;
    text-transform: none !important;
    margin-left: 2em ;
}

.fancyButtonSkeleton {
    height: 48px !important;
    display: flex row !important;
    justify-content: space-around !important;
    align-items: center !important;
    border-radius: 5px !important;
    font-family: "Arial";
    font-size: 12pt !important;
    font-weight: normal !important;
    letter-spacing: 0 !important;
    text-transform: none !important;
    margin-left: 0.5em
}

.testClass {
    border-color: red;
    color: red;
    background-color: red;
    background: red;
}
.v-enter-from {
    opacity: 0;
    transform: translateY(-30px)
    /* transform: dialog-bottom-transition; */
}
.v-enter-active {
    transition: all 0.3s ease-out
}
.v-enter-to {
    opacity: 1;
    transform: translateY(0);
}
.v-leave-from {
    opacity: 1;
    transform: translateY(0);
}
.v-leave-active {
    transition: all 0.3s ease-out
}
.v-leave-to {
    opacity: 0;
    transform: translateY(30px)
}
.center-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin-bottom: 5em;
  /* min-height: 100vh; */
}
.center-somewhere {
    display:flex row;
    float: left;
    margin-right:2em;
    margin-top: 8px;
    margin-left: 19.5em;
}

#app {
  background: url('https://images.unsplash.com/photo-1628595351029-c2bf17511435?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2064&q=80')
    no-repeat center center fixed !important;
  background-size: cover;
}

</style>
