<script setup lang="ts">
import type { EntityStatus } from '@/types'

const props = defineProps<{ status: EntityStatus }>()

const typeMap: Record<EntityStatus, 'primary' | 'warning' | 'success' | 'info'> = {
  DRAFT: 'primary',
  VERIFIED: 'warning',
  PUBLISHED: 'success',
  ARCHIVED: 'info'
}
</script>

<template>
  <el-tag :type="typeMap[props.status]" size="small" effect="dark" round>
    {{ props.status }}
  </el-tag>
</template>
