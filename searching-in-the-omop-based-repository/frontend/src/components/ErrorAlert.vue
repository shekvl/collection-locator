<template lang="pug">
v-alert.prominent.flat.dismissible(
    v-if="visible"
    :value="true"
    :type="type"
    class="notification"
    style="border-radius: 0; position: sticky;"
    @input="close"
)
    .title {{ value }}
    .detail
        slot
</template>

<script>
export default {
    name: 'ErrorAlert',
    props: {
        type: { type: String, default: 'error' },
        value: { type: String, default: null },
    },
    data() {
        return {
            visible: !!this.value,
        };
    },
    watch: {
        value(newVal) {
            this.visible = !!newVal;
        },
        visible(newVal) {
            if (!newVal) return this.close();
            setTimeout(this.close, 5000);
        },
    },
    methods: {
        close() {
            this.visible = false;
            this.$emit('input', null);
        },
    },
    mounted() {
        this.visible = !!this.value;
    },
};
</script>

<style scoped>
.title { font-size: 14pt; font-weight: bold; }
</style>
