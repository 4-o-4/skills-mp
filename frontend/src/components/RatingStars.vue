<script setup lang="ts">
const props = withDefaults(defineProps<{
  modelValue?: number
  readonly?: boolean
}>(), {
  modelValue: 0,
  readonly: false
})

const emit = defineEmits<{ 'update:modelValue': [value: number] }>()
</script>

<template>
  <el-rate
    :model-value="props.modelValue"
    :disabled="props.readonly"
    allow-half
    @update:model-value="(v: number) => emit('update:modelValue', v)"
  />
</template>
