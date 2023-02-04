# Client README

This Vue app provides a user interface that allows to upload collection annotations to the Collection Locator (`Upload View`) and search through its repository (`Browser View`).

In particular, the following features are supported:
- search by concepts (Athena)
- search by relationships (e.g. LOINC axes)
- upload collection annotations
- support of multiple attribute annotation
- support of lateral and hierarchical search (searches for mappable non-standard and child concepts)
- provides content and quality metadata about collections
- locate biobank facilities and obtain contact data

## Browser

The Browser provides different ways to search for collections within the repository.

### Athena Search
OHDSI provides a browser to look up concepts within the OMOP CDM. The Browser View uses the Athena API to embed this funcitonality directly into the website.

### OR-Query
Search for collections containing ANY of the concepts (or equivalent concepts, i.e. mappable or descendent concepts) from the searchbar.

### AND-Query
Search for collections containing ALL of the concepts (or ANY equivalent concept) from the searchbar.

### Relationship Query
Search for collections containing concepts that are associated to one of the specified values for each relationship. Currently, only LOINC Axes are supported as relationship set.

## Upload Files

To upload a collection, two different types of CSV files (`collection.csv, attribute.csv`) have to be provided in the specified format. It does not matter whether the collection records are separated into multiple files or gathered in just one file. Same goes for the attributes.

Annotations that contain codes from vocabularies that are not yet supported will non the less be stored internally, so they can be used when the missing vocabulary becomes available.

### Specification


`collection.csv` schema
```js
name; //name of collection
number_of_records; //number of records in original dataset
completeness; //qualtiy metadata attributes..
accuracy;
reliability;
timeliness;
consistancy
```
`attribute.csv` schema
```js
collection_name; //name of the associated collection
attribute_name; //name of the dataset column
code; //code of vocabulary that annotates this attribute
vocabulary_id; //OMOP CDM vocabulary for this attribute annotation
completeness;
accuracy;
reliability;
timeliness;
consistancy
```

- an attribute can be annotated with multiple codes
- the quality metadata fields (completeness, accuracy, reliability, timeliness, consistancy) are not required and may therefore be empty
- a collection may be annotated with codes of different vocabularies
- quality metadata values range between 0 and 1
- the collection names within the attribute.csv must match the collection name specified in the collection.csv
- the collections and attributes must have a unique name within the files uploaded together
- collections may be split up into multiple files or put together in a singel file (same goes for the attributes)
- collection.csv and attribute.csv files must be uploaded togther

### Examples
Check out the `upload examples` in the [./client/uploadExamples](./client/uploadExamples) directory



## General Information
`Pug` is a preprocessor for HTML templates to make HTML files more compact. It is used throughout this Vue project. You might want to check out the html to pug `converter` (=> [html2pug](https://html2pug.vercel.app/))

The UI makes great use of `Primevue`, a component libraray for Vue (=> [primeview.org](https://primevue.org/setup)).


`vue-i18n` is a internationalization plugin. Multiple languages can be supported by adding an i18n object to the corresponding vue component that contains text strings in different languages.

`Customized` Vue by adapting its configuration (=> [Configuration Reference](https://cli.vuejs.org/config/)).
