// src/services/dataTransformService.ts

/**
 * The shape of each item your backend returns.
 */
export interface ServerRawItem {
    biobankId:              string
    collectionId:           string
    numberOfRows:           number
    goodHits?: Array<{
        code:                   number
        columnName:            string
        goodValuesInPercentage: number
        revisedNumberOfRows:    number
    }>
    overallPercentage:      number
    overallExpectedRows:    number
}

/**
 * The shape you want for your “collections” array.
 */
export interface TransformedCollection {
    id:                    string
    name:                  string
    institution_id:        string
    number_of_records:     number
    overallPercentage:     number
    overallExpectedRows:   number
}

/**
 * The shape you want for your “attributes” array.
 */
export interface TransformedAttribute {
    collection_id:            string
    concept_id:               number
    attribute_name:           string
    goodValuesInPercentage:   number
    revisedNumberOfRows:      number
}

/**
 * Take the array your server returns and spit back an object
 * with `{ collections: TransformedCollection[], attributes: TransformedAttribute[] }`.
 */
export function transformServerData(
    raw: ServerRawItem[]
): {
    collections: TransformedCollection[]
    attributes:  TransformedAttribute[]
} {
    // map raw → collections
    const collections: TransformedCollection[] = raw.map(item => ({
        id:                   item.collectionId,
        name:                 `collection_${item.collectionId}`,
        institution_id:       item.biobankId,
        number_of_records:    item.numberOfRows,
        overallPercentage:    item.overallPercentage,
        overallExpectedRows:  item.overallExpectedRows
    }))

    // flatten all goodHits → attributes
    const attributes: TransformedAttribute[] = raw.flatMap(item => {
        return (item.goodHits || []).map(hit => ({
            collection_id:           item.collectionId,
            concept_id:              hit.code,
            attribute_name:          hit.columnName,
            goodValuesInPercentage:  hit.goodValuesInPercentage,
            revisedNumberOfRows:     hit.revisedNumberOfRows
        }))
    })

    return { collections, attributes }
}
