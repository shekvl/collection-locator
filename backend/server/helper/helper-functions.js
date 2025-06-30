var _ = require('lodash');
const dataSample1 = require('./data-structure-samples/dataStructureSample1.json')
const dataSample2 = require('../helper/data-structure-samples/dataStructureSample2.json')
const dataSample3 = require('../helper/data-structure-samples/dataStructureSample3.json')
const dataSample4 = require('../helper/data-structure-samples/dataStructureSample4.json')
const dataSample5 = require('../helper/data-structure-samples/dataStructureSample5.json')
const dataSample6 = require('../helper/data-structure-samples/dataStructureSample6.json')
class HelperFunctions {

    isNumeric(num) {
        return !isNaN(num)
    };

    simulateModule2SendingData() {
        let examples = []
        examples.push(dataSample1.anonymizations[0])
        examples.push(dataSample2.anonymizations[0])
        examples.push(dataSample3.anonymizations[0])
        examples.push(dataSample4.anonymizations[0])
        examples.push(dataSample5.anonymizations[0])
        examples.push(dataSample6.anonymizations[0])
        examples.push(dataSample6.anonymizations[1])
        examples.push(dataSample6.anonymizations[2])

        for (let i = 0; i < examples.length; i++) {
            examples[i] = this.parseData(examples[i])
        }
        return examples
    };

    parseData(data) {
        for (let el of data.columns) {
            //we add these as that's how the column names in the DB tables will be
            let conceptFrom = el.concept + "_from"
            let conceptTo = el.concept + "_to"
            el['conceptFrom'] = conceptFrom
            el['conceptTo'] = conceptTo
        }

        let finalCleanedValues = []
        for (let row of data.data) {
            let cleanedRow = row.row.replace(/(\r\n|\n|\r)/gm, "")
            let conceptValuesForColumn = cleanedRow.split(";")

            var cleanedValues = conceptValuesForColumn.map((str) => {
                let val = str.split(':')
                return {fromValue: val[0], toValue: val[1]}
              })

              let position = 1
              for (let val of cleanedValues) {
                val['numberOfRows'] = row.rowAmount
                val['position'] = position
                position += 1
                }
                finalCleanedValues.push(cleanedValues)
            }
        
            // merge the data and columns arrays based on the position
            let mergedColumnsAndData = []
            for (let arr of finalCleanedValues) {
                var mergedList = _.map(arr, function(item){
                    return _.extend(item, _.find(data.columns, { position: item.position }))
                })
                mergedColumnsAndData.push(mergedList)
            }

        let filteredObject = {
            anonymizationType: data.anonymizationTyp,
            biobank_id: data.biobankUid,
            collection_id: data.collectionUid,
            definition_id: data.definitionUid,
            name: data.name,
            columns: data.columns,
            codesFrom: data.columns.map(el => el.conceptFrom),
            codesTo: data.columns.map(el => el.conceptTo),
            mergedColumnsAndData: mergedColumnsAndData
        }
        return filteredObject
    };
}

let helperFunctions = new HelperFunctions()

module.exports = helperFunctions
