<template lang="pug">
v-alert.prominent.flat.dismissible(
    v-if="visible"
    v-model="visible"
    :type="type"
    class="notification"
    style="border-radius: 0; position: sticky;"
    @input="close"
)
    .title {{ value }}
    .detail
        slot
</template>

<script setup>
import { ref, watch, defineProps, defineEmits, onMounted } from 'vue'

const props = defineProps({
    type: { type: String, default: 'error' },
    value: { type: String, default: null },
})

const emit = defineEmits(['update:value'])

const visible = ref(!!props.value)

function close() {
    visible.value = false
    emit('update:value', null)
}

watch(() => props.value, (val) => {
    visible.value = !!val
})

watch(visible, (val) => {
    if (val) setTimeout(close, 5000)
})

onMounted(() => {
    visible.value = !!props.value
})
</script>

<style scoped>
.title { font-size: 14pt; font-weight: bold; }
</style>
