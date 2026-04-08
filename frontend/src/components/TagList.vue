<script setup lang="ts">
import type { Tag } from '@/types'

defineProps<{ tags: Tag[] }>()
</script>

<template>
  <div class="tag-list">
    <el-tag
      v-for="tag in tags"
      :key="tag.id"
      :color="tag.color"
      size="small"
      effect="dark"
      class="tag-chip"
    >
      {{ tag.name }}
    </el-tag>
  </div>
</template>

<style scoped>
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
.tag-chip {
  border: none;
}
</style>
