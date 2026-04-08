<script setup lang="ts">
withDefaults(defineProps<{
  title?: string
  description?: string
  icon?: string
}>(), {
  title: 'No data',
  description: 'Nothing here yet.',
  icon: 'Folder'
})
</script>

<template>
  <div class="empty-state">
    <el-icon :size="64" class="empty-icon">
      <component :is="icon" />
    </el-icon>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <slot name="action" />
  </div>
</template>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}
.empty-icon {
  color: var(--text-muted);
  opacity: 0.45;
}

.empty-state h3 {
  margin: 16px 0 8px;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--text-primary);
}
.empty-state p {
  margin: 0 0 20px;
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.55;
  max-width: 32ch;
}
</style>
