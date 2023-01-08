<template lang="pug">
DeferredContent
    //- .home(@click="toast()") {{ $t('a.b') }}

    #content
        section#locator.d-flex.flex-column
            h1 Collection Locator
            p
                span(v-html="$t('locator.p1a')")
                ExternalLink(
                    href="https://ohdsi.github.io/CommonDataModel/index.html",
                    text="CDM"
                )
                span(v-html="$t('locator.p1b')")
            p
                span(v-html="$t('locator.p2a')")
                ExternalLink(href="https://bbmri.at/", text="BBMRI.at")
                span(v-html="$t('locator.p2b')")
                RouterLink(href="", text="feedback")
                span(v-html="$t('locator.p2c')")
            p(v-html="$t('locator.p3')")
            ul.my-2
                li
                    span(v-html="$t('features.f1a')")
                    ExternalLink(
                        href="https://athena.ohdsi.org/search-terms/start",
                        text="Athena"
                    )
                    span(v-html="$t('features.f1b')")
                li(v-html="$t('features.f2')")
                li(v-html="$t('features.f3')")
                li(v-html="$t('features.f4')")
                li(v-html="$t('features.f5')")
                li(v-html="$t('features.f6')")
                li(v-html="$t('features.f7')")

            Button.p-button-text.p-button-lg.mx-2(
                icon="pi  pi-angle-double-right",
                label="BROWSE",
                @click="$router.push('/browser')"
            )
            Button.p-button-text.p-button-lg.mx-2(
                icon="pi  pi-angle-double-right",
                label="UPLOAD",
                @click="$router.push('/fileupload')"
            )

        section#cdm.d-flex.flex-column
            h1 OMOP CDM

            p(v-html="$t('cdm.p1')")
            p(v-html="$t('cdm.p2')")
            p(v-html="$t('cdm.p3')")
            p(v-html="$t('cdm.p4')")
            p(v-html="$t('cdm.p5')")
            p
                span(v-html="$t('cdm.p6a')")
                ExternalLink(href="https://www.ohdsi.org/", text="OHDSI")
                span(v-html="$t('cdm.p6b')")

            .links.d-flex.flex-column.my-1
                ExternalLink(
                    href="https://ohdsi.github.io/TheBookOfOhdsi/",
                    text="The Book of OHDSI"
                )
                ExternalLink(
                    href="https://ohdsi.github.io/CommonDataModel/cdm54.html#CONCEPT",
                    text="OMOP CDM v5.4"
                )

        section#bbmri.d-flex.flex-column
            h1 BBMRI-ERIC

            p
                span(v-html="$t('bbmri.p1a')")
                ExternalLink(
                    href="https://www.bbmri-eric.eu/",
                    text="BBMRI-ERIC"
                )
                span(v-html="$t('bbmri.p1b')")
                ExternalLink(
                    href="https://directory.bbmri-eric.eu/#/",
                    text="directory"
                )
                span(v-html="$t('bbmri.p1c')")
            p(v-html="$t('bbmri.p2')")
</template>

<script setup lang="ts">
import DeferredContent from "primevue/deferredcontent";
import ExternalLink from "../components/ExternalLink.vue";
import RouterLink from "../components/RouterLink.vue";
import Button from "primevue/button";
</script>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
    name: "HomeView",
    components: {},
    data() {
        return {
            a: "asdf",
        };
    },
    methods: {
        toast() {
            this.$toast.add({
                severity: "info",
                summary: "Success Message",
                detail: "Message Content",
                life: 30000,
            });
        },
    },
    i18n: {
        messages: {
            en: {
                locator: {
                    p1a: "The <strong>Collection Locator</strong> was developed to facilitate searching for collections in a distributed database system which includes a wide variety of different collection schemas and attribute ontologies. A collection can be included into the Collection Locator by semantically annotating its fields with codes of one of the standardised onthologies of the <strong>OMOP Common Data Model</strong> (",

                    p1b: "). This approach avoids privacy concerns, as it only utilizes content and quality metadata of collections stored within the database system, not the personal data within the collections. This way, the Collection Locator collects semantical metadata representations of collection schemas in a repository and can therefore find collections containing information about specific semantical concepts.",

                    p2a: "The Collection Locator was developed as part of the WP2 of the <strong>Austrian Node</strong> of the <strong>Biobanking and BioMolecular resources Research Infrastructure</strong> (",

                    p2b: ") and is based on the OMOP CDM as common standard for semantical concepts. The locator is meant to complement the BBMRI-ERIC <strong>Sample Locator</strong> for distributed federated search with a more generic metadata search functionallity that allows to locate collections throughout the biobank network and contact the corresponding facilities. This project is still part of ongoing research and relies on contributions within the biobank community. We are happy about any ",

                    p2c: " you might have.",

                    p3: "This website provides a user interface that allows to upload collection annotations to the Collection Locator and search through its repository. In particular, the following <strong>features</strong> are supported:",
                },
                features: {
                    f1a: "search by concepts (",
                    f1b: ")",
                    f2: "search by relationships (e.g. LOINC axes)",
                    f3: "upload collection annotations",
                    f4: "supports multiple attribute annotation",
                    f5: "supports hierarchical search (searches for child concepts of parent)",
                    f6: "provides content and quality metadata about collections",
                    f7: "locate biobank facilities and obtain contact data",
                },
                cdm: {
                    p1: "The <strong>Omop CDM</strong> is a person-centric concept based common data model for healthcare related reasearch. It includes several biomedical <strong>code systems</strong> (e.g. LOINC, SNOMED, ICD) as a source for descriptive semantical concepts that help to map data from external sources to common standardised ontologies within the CDM.",
                    p2: "As other CDMs, it offers a <strong>generic database model</strong> that allows to bring data from various sources into a syntactical and semantical consistant form. The aim is to promote <strong>interoperability</strong> between data systems by homogenising and standardising available data collections.",
                    p3: "Everything within the CDM is represented by <strong>concepts</strong> (e.g. blood types, diseases, measurement units, vocabularies, relationships). Each concept can be connected to other concepts. These semantical connections are stored as (lateral and vertical) <strong>relationships</strong>.",
                    p4: "The CDM is separated into several table groups. In this project, we are only interested in the <strong>standardized vocabularies</strong> containing concept and relationship information about the various code systems as the backbone of our Collection Locator. These tables can be populated with a collection of standardized vocabularies that can be downloaded form the OHDSI Athena website. After setting up the OMOP CDM, concepts and relationships can be queried within the database.",
                    p5: "Biobank collections can be annotated with codes of the available code systems within the OMOP CDM. The Collection Locator is used to query collections containing any of these codes, after the collections have been uploaded to the locator by submitting a <strong>spreadsheet with mapped table fields semantically describing the attribute content</strong> of each collection.",
                    p6a: "Currently, the Collection Locator runs with OMOP CDM <strong>version 5.4</strong>. The CDM is a open community data standard developed by the international ",
                    p6b: " community. It is an evolving community engaging with the people using their open source tools for large-scale health data analytics. If you find any mistakes within the concept schema or have suggestions for extending the CDM, you might consider contributing to the odyssey.",
                },
                bbmri: {
                    p1a: "The European Biobanking Research Infrastructure (",
                    p1b: ") is a international network of biobanks and research facilities with the goal to connect and coordinate available biomedical research resources. BBMRI-ERIC not only provides services and software solutions, but also maintains a ",
                    p1c: " of published biobank collections that can be searched. Each Member State operates a national node that mediates between the national facilities and BBMRI-ERIC.",
                    p2: "BBMRI.at is the Austrian national node of the BBMRI-ERIC. This Collection Locator was developed as part of the Work Package 2 (WP2) with the focus on improvements regarding quality management and database search.",
                },
            },
            de: {},
        },
    },
});
</script>

<style scoped>
section {
    align-items: flex-start;
    margin-block: 30px;
    padding-inline: 10px;

    text-align: justify;
}

p {
    margin-bottom: 15px;
}

#content {
    margin-inline: 70px;
    margin-block: 50px;
}

h1 {
    margin-bottom: 7px;
}

ul {
    list-style-position: inside;
}

li {
    margin-bottom: 5px;
}

.links {
    line-height: 2em;
}
</style>
