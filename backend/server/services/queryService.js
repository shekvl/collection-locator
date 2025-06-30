const contentDao = require('../dao/content-dao')
const helperFunctions = require('../helper/helper-functions')
const chalk = require('chalk');


class QueryService {
    async getData(data) {
        try {
            const res = await contentDao.getData(data)
            let tableNames = res.tableNames;
            let filteredResult = []
            if (res) {
                // for each index/collection that matched our query (res is an array of matched collections/indexes)
                let goodHits = []
                for (let arr of res) {
                    // for each row on this collection/index that matched
                    for (let el of arr) {

                        let filteredResult2 = filteredResult.push({
                            biobankId: el.biobank_id,
                            collectionId: el.collection_id,
                            definitionId: el.definition_id,
                            numberOfRows: el.number_of_rows,
                            goodHits: [],
                        })

                        goodHits = []
                        // for each concept searched from the frontend
                        for (let frontendRow of el.frontendQuery) {
                            // we need this length to populate the filteredResults properly and dynamically based on the number of concepts searched from the frontend
                            // -> as the first n objects in the goodHits array are for the 1st matched row, the 2nd n objects are for the 2nd matched row and so on...
                            // when we have a range from the frontend (searched concepts) that is bigger (includes) both "from" and "to" values from the DB, then we just return all of the rows (100%)
                            if (parseFloat(frontendRow.fromValue) <= parseFloat(el[`${frontendRow.concept}_from`]) && parseFloat(frontendRow.toValue) >= parseFloat(el[`${frontendRow.concept}_to`])) {

                                // get column_name for this concept
                                // console.log("case 1:");
                                // console.log(res.tableNames);

                                goodHits.push({code: frontendRow.concept, goodValuesInPercentage: 1, revisedNumberOfRows: el.number_of_rows})
                                continue
                            }
                            // find biggest and smallest number from front end query concept id values and db row values for that concept
                            const maxNumber = Math.max(frontendRow.fromValue, frontendRow.toValue, el[`${frontendRow.concept}_from`], el[`${frontendRow.concept}_to`])
                            const minNumber = Math.min(frontendRow.fromValue, frontendRow.toValue, el[`${frontendRow.concept}_from`], el[`${frontendRow.concept}_to`])

                            // find the other two numbers that are in between the range of min and max numbers
                            let arrayOfFour = [frontendRow.fromValue, frontendRow.toValue, el[`${frontendRow.concept}_from`], el[`${frontendRow.concept}_to`]]
                            let otherTwoNumbers = arrayOfFour.filter(nr => nr != maxNumber && nr != minNumber)

                            const minNumberInsideRange = Math.min(...otherTwoNumbers)
                            const maxNumberInsideRange = Math.max(...otherTwoNumbers)

                            // find good values (matching values) - insideDistance
                            let goodValues = maxNumberInsideRange - minNumberInsideRange
                            // if we have only one matching number inside the range then when we subtract that number by itself the distance will be 0, but this needs to map to 1 because there is only that number that matches in the range
                            goodValues = goodValues > 0 ? goodValues : 1
                            // find all values (min to max) - outsideDistance
                            const allValues = maxNumber - minNumber
                            // find ratio between good values vs all values
                            const distanceRatio = goodValues / allValues
                            const revisedNumberOfRows = distanceRatio * el.number_of_rows

                            // find the percentage of matching values (good values) -> this means that the percentage of good values in the number of rows returned is that much
                            let goodValuesInPercentage = revisedNumberOfRows / el.number_of_rows
                            // take only the first two values after the decimal point

                            // for multiple concepts we need to send the goodValuesInPercentage as an array of objects with the concept id as a key and the matching percentage for that concept, for all matching concepts
                            var concept = frontendRow.concept;

                            // get column_name for this concept
                            // console.log("case 2:");
                            // console.log(res.tableNames);

                            //goodHits.push({[concept]: goodValuesInPercentage, revisedRows: revisedNumberOfRows})
                            goodHits.push({code: concept, goodValuesInPercentage: goodValuesInPercentage, revisedNumberOfRows: revisedNumberOfRows})
                        }
                        // the push returns the length of the new array, and we want the index (so length - 1 as index starts from 0)
                        filteredResult[filteredResult2 - 1].goodHits = goodHits
                    }
                    // matching percentages for every concept from the frontend (exL two concepts from frontend both match rows in db with two concepts -> one good percentage for each concept matched = two percentages for element. HOW TO HANDLE THIS?)
                    // goodHits says -> if we have two concepts searched from the frontend, and they match collections that have both those concepts, you have an array where the first two objects are for the percentage/revised rows matched for the first concept and the 2nd object is for the 2nd concept matched for the same row, then you have object 3 and 4 which is the same for the 2nd row matched and so on... How to send and show this properly to the frontend?
                }

                // combine percentages
                for (let res of filteredResult) {
                    let overallPercentage = 1
                    for (let hit of res.goodHits) {
                        overallPercentage = overallPercentage * hit.goodValuesInPercentage
                    }
                    res['overallPercentage'] = overallPercentage
                    res['overallExpectedRows'] = res.numberOfRows * overallPercentage
                }

                let overallResult = []
                // group by collection ID and biobank ID
                for (let res of filteredResult) {
                    // if it's an index, we need to take into account also the definition id
                    // so it's a combination of biobank ID, collection ID and definition ID
                    let found = false
                    for (let row of overallResult) {
                        // Group by biobankId, collectionId and definitionId
                        if (res.biobankId === row.biobankId && res.collectionId === row.collectionId && res.definitionId === row.definitionId) {
                            // SUM up numberOfRows
                            row.numberOfRows += res.numberOfRows
                            // SUM up the overallExpectedRows // transfer the overall expected rows from filteredResult to overallResult
                            row.overallExpectedRows += res.overallExpectedRows
                            // overall hits in percentage
                            row.overallPercentage = row.overallExpectedRows / row.numberOfRows

                            for (let goodHit of row.goodHits) {
                                for (let resGoodHit of res.goodHits) {

                                    let conceptIdForName = resGoodHit.code;
                                    // console.log(res.tableNames);
                                    // for (let tableInfo of res.tableNames) {
                                    //     if ('collection_'+res.collectionId === tableInfo.tableName ||
                                    //         'definition_'+res.definitionId === tableInfo.tableName) {
                                    //         for (let columnInfo of tableInfo.columnNames) {
                                    //             if (columnInfo.conceptId === resGoodHit.code) {
                                    //                 resGoodHit.columnName = columnInfo.columnName;
                                    //                 break;
                                    //             }
                                    //             break;
                                    //         }
                                    //     }
                                    // }

                                    if (goodHit.code === resGoodHit.code) {
                                        // Question: this sum of the revisedNumberOfRows (expected hits per concept) does not match the "Number of rows matched (numberOfRows). What does this number really show?"
                                        // Answer (check with Patrick): this nr means: from the full number of rows matched (numberOfRows) concept 1 is expected to have x nr of hits and concept 2 is expected to have y number of hits. Meaning, concept 1 is in x rows of the number of rows matched, and concept 2 is in y rows from number of rows matched
                                        // example: numberOfRows=3168; expected hits per concept: 1-8=1779; 39243-1=2629. This means that from those 3168 rows matched, 1779 match the concept 1-8 for the values you searched (and that is 56.17%) whereas 2629 rows from those 1368 rows match the values you searched for concept 39243-1.
                                        // Keep in mind: goodHit.revisedNumberOfRows is just an expected number of rows per concept
                                        goodHit.revisedNumberOfRows += resGoodHit.revisedNumberOfRows
                                    }
                                }
                                goodHit.goodValuesInPercentage = goodHit.revisedNumberOfRows / row.numberOfRows

                            }
                            found = true
                        }
                    }
                    if (!found) {
                        for (let tableInfo of tableNames) {
                            if ('collection_'+res.collectionId === tableInfo.tableName ||
                                'definition_'+res.definitionId === tableInfo.tableName) {
                                for (let resGoodHit of res.goodHits) {
                                    let finished = false;
                                    for (let columnInfo of tableInfo.columnNames) {
                                        if (columnInfo.conceptId === resGoodHit.code) {
                                            resGoodHit.columnName = columnInfo.columnName;
                                            finished = true;
                                            break;
                                        }
                                        if (finished) break;
                                    }
                                }
                            }
                        }

                        overallResult.push(res)
                    }
                }
                // send overallResults now instead of filteredResults where we have the results grouped by collection id and biobank id instead of all rows, and then in frontend when u click the row u should get the percentages per concept (found in goodHits)
                overallResult.sort((a, b) => parseFloat(b.overallExpectedRows) - parseFloat(a.overallExpectedRows));
                return overallResult
            }
            else throw 'Service: There is no data containing these concepts!'
        }
        catch (e) {
            console.error(e)
            throw new Error("Service: There is no data containing these concepts!")
        }
    }

    async saveData() {
        try {
            const data = await helperFunctions.simulateModule2SendingData()
            for (let testData of data) {
                await contentDao.saveData(testData)
            }
        }
        catch (e) {
            console.error(e)
        }
    }

    async getConcpets(){
        const res = await contentDao.getAnyCDM()
        console.log(res)
        return res
    }

    async getConcept(conceptIdOrName) {
        try {
            const res = await contentDao.getConcept(conceptIdOrName)
            console.log(res)
            return res
        }
        catch (error) {
            console.error('Service: concept does not exist!')
            throw new Error('Service: concept does not exist!')
        }
    }
}
let queryService = new QueryService()

module.exports = queryService
