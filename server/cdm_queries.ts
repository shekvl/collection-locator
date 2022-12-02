const cdm = process.env.PG_CDM_SCHEMA

export const test = {
    text: `
        SELECT *
        FROM ${cdm}.test
    `,
    // values: [],
}