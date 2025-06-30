const helperFunctions = require('../helper/helper-functions')
const collectionDB = require('./collectionsDatabase')
const cdmDB = require('./cdm')

cdmDB.connect()

class ContentDAO {

    async getConcept(searchString) {
        // console.log('search request: '+ searchString)
        let searchByCode = helperFunctions.isNumeric(searchString)
        const query = (searchByCode)?
            `select concept_id, concept_name from cdm.concept where concept_id = ${searchString}`:
            `select concept_id, concept_name from cdm.concept where concept_name LIKE '%${searchString}%'`;

        const res = await cdmDB.query(query)
        // console.log('rows: '+res.rows)
        return res.rows
    }

    eliminateTrailingComma(stringToModify){
        return stringToModify.slice(0, -1)
    }

    async dropTable(tableName){
         let query = `DROP TABLE "${tableName}"`
        // console.log(query)
        return await collectionDB.query(query)
    }

    async truncateTable(tableName){
        let truncateQuery = `TRUNCATE TABLE "${tableName}";`
        return await collectionDB.query(truncateQuery)
    }

    async createTable(data, tableName){
        let columns = ''
        for (let el of data.columns) {
            columns += `"${el.conceptFrom}"` + ' ' + 'varchar(99)' + ','
            columns += `"${el.conceptTo}"` + ' ' + 'varchar(99)' + ','
        }

        columns = this.eliminateTrailingComma(columns)
        console.log(columns)

        //IMPORTANT: the code to DROP the table is used only for testing purposes so that postgres does not throw when we try to create the same table, in the future this can be removed and we can directly create the table!
        //await this.dropTable(tableName)

        let createTableQuery = `CREATE TABLE "${tableName}"
                                    (
                                        id             serial NOT NULL PRIMARY KEY,
                                        collection_id  varchar(99),
                                        biobank_id     varchar(99),
                                        ${columns},
                                        number_of_rows integer
                                    )`
        console.log(createTableQuery)
        await collectionDB.query(createTableQuery)
        if(data.definition_id){
            let addDefinitionIdQuery = `ALTER TABLE "${tableName}"
                                  ADD COLUMN definition_id varchar(99)`;
            await collectionDB.query(addDefinitionIdQuery)
        }

    }

    async deleteExistingRows(tableName, biobank_id, collection_id){
        //select the rows that come from the same biobank and same collection as we only need to clear (delete) those rows!
        let selectQuery = `SELECT id
                                   from "${tableName}"
                                   WHERE biobank_id = '${biobank_id}'
                                     AND collection_id = '${collection_id}'`
        selectQuery = await collectionDB.query(selectQuery)

        let ids = ''
        for (let row of selectQuery?.rows) {
            ids += `'${row.id}',`
        }
        ids = this.eliminateTrailingComma(ids)

        // only delete the rows that come from the same biobank and the same collection
        if(ids !== ''){
            let deleteQuery = `DELETE
                                   from "${tableName}"
                                   WHERE id IN (${ids})`
            // console.log(deleteQuery)
            await collectionDB.query(deleteQuery)
        }
    }

    async calculateColumnsToInsert(columns){
        let columnsToInsert = ''
        for (let el of columns) {
            columnsToInsert += `"${el.conceptFrom}",`
            columnsToInsert += `"${el.conceptTo}",`
        }

        return this.eliminateTrailingComma(columnsToInsert)
    }

    async updateDefinition(data, tableName){
        // This removes all the rows from the table and is more efficient than DELETE statement! - use this if the table needs to be cleaned before adding new data to it!
        //await this.truncate(tableName);

       await this.deleteExistingRows(tableName, data.biobank_id, data.collection_id)

       let columnsToInsert = await this.calculateColumnsToInsert(data.columns)
        for (let el of data.mergedColumnsAndData) {
            let valuesForColumn = ''
            let numOfRows = 0
            for (let val of el) {
                numOfRows = val.numberOfRows
                valuesForColumn += `'${val.fromValue}',`
                valuesForColumn += `'${val.toValue}',`
            }
            valuesForColumn = this.eliminateTrailingComma(valuesForColumn)
            let query = `INSERT INTO "${tableName}" (collection_id, definition_id, biobank_id, ${columnsToInsert},
                                                     number_of_rows)
                         values ('${data.collection_id}', '${data.definition_id}', '${data.biobank_id}',
                                 ${valuesForColumn}, ${numOfRows})`
            // console.log(query)
            await collectionDB.query(query)
        }
    }

    async definitionExists(tableName){
        let query = `SELECT EXISTS (SELECT
                                        FROM information_schema.tables
                                        WHERE table_schema = 'public' AND table_name = '${tableName}');`
        const definitionExists = await collectionDB.query(query)
        return definitionExists?.rows[0]?.exists
    }

    async fromDef(data, tableName){
        if (!await this.definitionExists(tableName)) {
            await this.createTable(data, tableName)
        }
            await this.updateDefinition(data, tableName)
    }

    async updateSpeedUpTable(data, tableName){
        // console.log('updateSpeedUpTable');
        let minValue
        let maxValue
        let minQuery = ''
        let maxQuery = ''
        let columnName = ''
        for (let column of data.columns) {
            minQuery = `SELECT MIN("${column.concept}_from") from "${tableName}"`
            maxQuery = `SELECT MAX("${column.concept}_to") from "${tableName}"`
            minValue = await collectionDB.query(minQuery)
            maxValue = await collectionDB.query(maxQuery)
            minValue = parseFloat(minValue.rows[0].min)
            maxValue = parseFloat(maxValue.rows[0].max)
            columnName = column.name;

            let speedUpQuery = `SELECT * from speed_up_tables_5 where "table_name" = '${tableName}' AND \"conceptId\"::INTEGER = ${column.concept}`
            // console.log('speedUpQuery:'+speedUpQuery)
            speedUpQuery = await collectionDB.query(speedUpQuery)

            if (speedUpQuery.rows.length === 0) {
                speedUpQuery = `INSERT INTO speed_up_tables_5 ("table_name", "conceptId", "Min", "Max", "column_name") values ('${tableName}', '${column.concept}', ${minValue}, ${maxValue}, '${columnName}')`
                let runQuery = await collectionDB.query(speedUpQuery)
                // console.log('runQuery='+runQuery)
            } else {
                // console.log('speedUpQuery='+speedUpQuery.rows[0])
                speedUpQuery = `UPDATE speed_up_tables_5 SET "Min"=${minValue}, "Max"=${maxValue}, "column_name"='${columnName}' WHERE "table_name"='${tableName}' and "conceptId"='${column.concept}'`
                await collectionDB.query(speedUpQuery)
            }
        }
        // console.log('end updateSpeedUpTable');
    }

    async fillCollection(data, tableName){
        let columnsToInsert = await this.calculateColumnsToInsert(data.columns)

        for (let el of data.mergedColumnsAndData) {
            let valuesForColumn = ''
            let numOfRows = 0
            for (let val of el) {
                numOfRows = val.numberOfRows
                // console.log(val)
                valuesForColumn += `'${val.fromValue}',`
                valuesForColumn += `'${val.toValue}',`
            }
            valuesForColumn = this.eliminateTrailingComma(valuesForColumn)
            // populate the DB
            let query = `INSERT INTO "${tableName}" (collection_id, biobank_id, ${columnsToInsert}, number_of_rows) values ('${data.collection_id}', '${data.biobank_id}', ${valuesForColumn}, ${numOfRows})`
            // console.log(query)
            await collectionDB.query(query)
        }
    }

    async saveData(data) {
        // console.log(data)
        let tableName
        if (data.anonymizationType === 'from_col') {
            tableName = 'collection_' + data.collection_id
            await this.createTable(data, tableName)
            await this.fillCollection(data, tableName)
        } else if (data.anonymizationType === 'from_def') {
            tableName = 'definition_' + data.definition_id;
            await this.fromDef(data, tableName)
        }
        await this.updateSpeedUpTable(data, tableName)
    }

    getConceptIdsAsSQL(setOfConcepts){
        let resultingQuery = '('
        let counter = setOfConcepts.size;
        // console.log(setOfConcepts);
        // console.log(counter);
        setOfConcepts.forEach((concept)=>{
            resultingQuery += `\"conceptId\"::INTEGER = ${concept}`
            if(--counter){
                resultingQuery += ' OR '
            }
        });
        resultingQuery += ')'
        return resultingQuery
    }

    async getTableNamesFromSpeedUpTable(frontendQuery, allRelatedConcepts){
        let tableNames = []
        let query = `SELECT "table_name", "conceptId", "column_name" from speed_up_tables_5 where ((`
        let counter = 0
        for (let data of frontendQuery) {
            query += `(${this.getConceptIdsAsSQL(allRelatedConcepts[counter])}`+` AND (`
            query += `(${data.value.fromValue} BETWEEN "Min" and "Max")`
            query += ` OR `
            query += `(${data.value.toValue} BETWEEN "Min" and "Max")`
            query += ` OR `
            query += `("Min" BETWEEN ${data.value.fromValue} AND ${data.value.toValue})`
            query += ` OR `
            query += `("Max" BETWEEN ${data.value.fromValue} AND ${data.value.toValue})`
            query += `)`
            if (++counter >= frontendQuery.length) {
                break
            }
            else {
                query += ') OR '
            }
        }
        query += `)) AND ("table_name" IN (SELECT "table_name" FROM speed_up_tables_5 GROUP BY "table_name" HAVING COUNT(*) >= ${frontendQuery.length})))`

        // console.log(query)
        const speedUpResult = await collectionDB.query(query)
        for (let res of speedUpResult.rows) {
            // console.log(res)
            tableNames.push({tableName: res.table_name, conceptId: res.conceptId, columnName: res.column_name })
        }
        //group tablenames to have an array of found concepts for each table
        //this cache is useful so that later on, when looking at the actual tables, the CDM databased doesn't have to be accessed again
        const tableNameGrouped = tableNames.reduce((acc, curr) => {
            const existingEntry = acc.find(entry => entry.tableName === curr.tableName);
            if (existingEntry) {
                existingEntry.conceptIds.push(curr.conceptId);
                existingEntry.columnNames.push({"conceptId": curr.conceptId, "columnName": curr.columnName});
            } else {
                acc.push({ tableName: curr.tableName, conceptIds: [curr.conceptId], columnNames: [{"conceptId": curr.conceptId, "columnName": curr.columnName}] });
            }
            return acc;
        }, []);
        return tableNameGrouped
    }

    async getWhereClauseForTable(frontendQuery, alreadyKnownConceptIds, allRelatedConcepts){
        console.log(frontendQuery);
        console.log(alreadyKnownConceptIds);
        console.log(allRelatedConcepts);
        let counter = 0;
        let whereClauseParts = [];
        for (let searchedConcept of frontendQuery) {
            let whereClauseCurrent = '';
            let innerCounter = alreadyKnownConceptIds.length
            //Since multiple concepts from alreadyKnownConceptIds could belong to a single searchedConcept, we check all possibilities here
            for (let conceptId of alreadyKnownConceptIds) {
                //if this concept is unrelated to the searched concept, then skip it
                if(!Array.from(allRelatedConcepts[counter]).map(String).includes(conceptId.toString())){
                    continue
                }
                if(innerCounter-- < alreadyKnownConceptIds.length){
                    whereClauseCurrent += ' or '
                }
                if (!isNaN(searchedConcept.value.fromValue) && !isNaN(searchedConcept.value.toValue)) {
                    whereClauseCurrent += `(${searchedConcept.value.fromValue} BETWEEN "${conceptId}_from"::numeric and "${conceptId}_to"::numeric) 
                    OR ( ${searchedConcept.value.toValue} BETWEEN "${conceptId}_from"::numeric and "${conceptId}_to"::numeric) 
                    OR ("${conceptId}_from"::numeric BETWEEN ${searchedConcept.value.fromValue} and ${searchedConcept.value.toValue}) 
                    OR ("${conceptId}_to"::numeric BETWEEN  ${searchedConcept.value.fromValue} and ${searchedConcept.value.toValue})`
                }
                // if we search by string
                else {
                    whereClauseCurrent += `"${conceptId}_from" LIKE '%${searchedConcept.value.fromValue}%' OR "${conceptId}_to" LIKE '%${searchedConcept.value.toValue}%'`
                }
            }
            if (whereClauseCurrent !== '')
                whereClauseParts.push('('+whereClauseCurrent+')');
            ++counter;
        }

        let whereClause = 'where (' + whereClauseParts.join(' AND ') + ')';
        return whereClause;
    }

    async getData(frontendQuery) {
        console.log("frontendQuery: "+ JSON.stringify(frontendQuery))
        //frontendQuery: [{"name":"smoke","concept":{"concept_id":"39243-1","conceptName":"Second hand smoke exposure"},"value":{"fromValue":"23","toValue":"23"}},
        // {"name":"bmi","concept":{"concept_id":"65838-5","conceptName":"Date submitted"},"value":{"fromValue":"2","toValue":"2"}}]
        let result = []
        let allRelatedConcepts = [] //an array containing different sets, where each set represents the related concepts of a searched concept (with the same array index)
        for (let data of frontendQuery) {
            const relatedConcepts = await this.getAllConceptChildrenAndSiblingsRecursively(data.concept.concept_id, new Set());
            allRelatedConcepts.push(relatedConcepts);
        }
        console.log(allRelatedConcepts)
        //access speed up table to get the table names and associated concepts found inside these tables
        let tableNames = await this.getTableNamesFromSpeedUpTable(frontendQuery, allRelatedConcepts)
        for (let tableNameEntry of tableNames) {
            let whereClause = await this.getWhereClauseForTable(frontendQuery, tableNameEntry.conceptIds, allRelatedConcepts);
            let selectQueryConceptColumns = ''
            tableNameEntry.conceptIds.forEach(conceptId => {selectQueryConceptColumns += `"${conceptId}_from", "${conceptId}_to",`});

            let query
            if (tableNameEntry.tableName.startsWith('collection')) {
                query = `select biobank_id as biobank_id, collection_id as collection_id, ${selectQueryConceptColumns} number_of_rows as number_of_rows from "${tableNameEntry.tableName}" ${whereClause} order by number_of_rows desc`
            } else if (tableNameEntry.tableName.startsWith('definition')) {
                query = `select definition_id as definition_id, biobank_id as biobank_id, collection_id as collection_id, ${selectQueryConceptColumns} number_of_rows as number_of_rows from "${tableNameEntry.tableName}" ${whereClause} order by number_of_rows desc`
            }
            console.log(query)

            const res = await collectionDB.query(query)
            if (res) {
                result.push(res.rows)
            }
        }
        let frontendConcepts = frontendQuery.map(function(row) { return {concept: row.concept.concept_id, fromValue: row.value.fromValue, toValue: row.value.toValue }})
        //add frontendConcepts as well as rename the found concepts in the result to match the initially searched concepts (so that the ranking algorithm can work seamlessly since it will look for the searched concepts)
        for (let arr of result) {
            for (let i = 0; i < arr.length; i++) {
                const row = arr[i];
                let setOfAdditionalConceptsToAdd = new Set();
                let foundConcept
                for (let prop in row) {
                    // match '_from' or '_to'
                    if (/(_from)$/.test(prop) || /(_to)$/.test(prop)) {
                        foundConcept = prop.split('_')[0];
                        setOfAdditionalConceptsToAdd = this.matchFoundConceptToSearchedConcepts(allRelatedConcepts, frontendQuery, foundConcept)
                        break
                    }
                }
                row['frontendQuery'] = frontendConcepts
                //since a row in the result could be related to multiple equivalent searched concepts, we duplicate these rows for each searched concept
                let arrayOfAdditionalConcepts = Array.from(setOfAdditionalConceptsToAdd)
                for (let j = 0; j < arrayOfAdditionalConcepts.length; j++) {
                    //the first row to rename will simply be overwritten, all additional rows must be duplicated
                    let duplicatedRow = row
                    if(j>0){
                        duplicatedRow = Object.assign({}, row);
                        arr.splice(i, 0, duplicatedRow);
                        i++;
                    }
                    this.renameConceptInRow(duplicatedRow, foundConcept, arrayOfAdditionalConcepts[j], '_from', frontendConcepts)
                    this.renameConceptInRow(duplicatedRow, foundConcept, arrayOfAdditionalConcepts[j], '_to', frontendConcepts)
                }
            }
        }
        result.tableNames = tableNames;
        return result;
    }

    renameConceptInRow(row, oldConceptProp, renamedConcept, postfix, frontendConcepts){
        const oldProp = `${oldConceptProp}${postfix}`;
        const newProp = `${renamedConcept}${postfix}`;
        if(oldConceptProp !== newProp){
            row[newProp] = row[oldProp];
            //each searched concept needs an associated found concept in the results
            if(!frontendConcepts.some(entry => entry.concept.toString() === oldConceptProp.toString())){
                delete row[oldProp];
            }
        }
        return row;
    }

    matchFoundConceptToSearchedConcepts(allRelatedConcepts, frontendQuery, foundConcept){
        let searchedConcepts = new Set();
        foundConcept = foundConcept.toString();
        for (let i = 0; i < allRelatedConcepts.length; i++) {
            //cast each element of the set to a string to ensure proper comparison
            if (Array.from(allRelatedConcepts[i]).map(String).includes(foundConcept)) {
                searchedConcepts.add(frontendQuery[i].concept.concept_id);
            }
        }
        return searchedConcepts;
    }

    async getAllConceptChildrenAndSiblingsRecursively(conceptId, alreadyExploredConcepts){
        const concepts = new Set();
        const queryRelatedEntities = `SELECT * from cdm.concept_relationship where concept_id_1=${conceptId}
                                         AND (relationship_id='Maps to'OR relationship_id='Subsumes')`
        const relatedConceptsResult = await cdmDB.query(queryRelatedEntities)
        for (let res of relatedConceptsResult.rows) {
            if(!alreadyExploredConcepts.has(res.concept_id_2)){
                concepts.add(res.concept_id_2);
            }
        }
        alreadyExploredConcepts.add(conceptId);
        await Promise.all([...concepts].map(async (concept)=>{
            return this.getAllConceptChildrenAndSiblingsRecursively(concept, alreadyExploredConcepts);
        }))
            .then((relatedConcepts)=>{
                relatedConcepts.forEach(conceptSet => {
                    conceptSet.forEach(item => concepts.add(item));
        })})
        concepts.add(conceptId);
        return concepts;
    }
}

let contentDAO = new ContentDAO()

module.exports = contentDAO
