<template lang="pug">
v-select(
    :value="selectedCDMConcept"
    @input="oninput"
    @search="searchCDM"
    :options="options"
    label="id"
    :filterable="false"
)
</template>

<script>
import debounce from "lodash.debounce";
import map from "lodash.map";
import {forAutoComplete} from "@/requests/dbRequestFunctions";
import vSelect from "vue-select";

export default {
    components: {vSelect},
    props: ["value"],
    data() {
        return { options: [] }
    },
    computed: {
        selectedCDMConcept() {
            console.log("here:"+ this.value);
            if (this.value == null) {
                return null;
            }
            return this.value;
        }
    },
    methods: {
        searchCDM: debounce(function (query, loading){
            forAutoComplete(query)
                .then(response => {
                    this.options = response.data;
                })
                .finally(() => loading(false));
            loading(true);
        }, 350),
        oninput(value) {
            console.log(value);
            this.$emit("input", map(value, "id"));
        }
    }
}
</script>

<style scoped>

</style>
