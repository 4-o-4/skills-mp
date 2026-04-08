<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { versionsApi } from '@/api'
import type { DiffResult } from '@/types'
import { diffLines, type Change } from 'diff'
import StatusBadge from '@/components/StatusBadge.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)

const skillId = Number(route.params.id)
const vid1 = Number(route.params.vid1)
const vid2 = Number(route.params.vid2)

const diffResult = ref<DiffResult | null>(null)

const changes = computed<Change[]>(() => {
  if (!diffResult.value) return []
  return diffLines(
    diffResult.value.versionFrom.content || '',
    diffResult.value.versionTo.content || ''
  )
})

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await versionsApi.getDiff(vid1, vid2)
    diffResult.value = data
  } catch {
    const [v1Res, v2Res] = await Promise.all([
      versionsApi.getById(vid1),
      versionsApi.getById(vid2)
    ])
    diffResult.value = {
      versionFrom: v1Res.data,
      versionTo: v2Res.data,
      changes: ''
    }
  } finally {
    loading.value = false
  }
})

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric'
  })
}
</script>

<template>
  <div v-loading="loading" class="diff-view">
    <div class="diff-header">
      <el-button text @click="router.push(`/skills/${skillId}`)">
        <el-icon><ArrowLeft /></el-icon> Back to Skill
      </el-button>
      <h1>Version Diff</h1>
    </div>

    <template v-if="diffResult">
      <div class="version-info-row">
        <el-card class="version-info">
          <div class="version-label">From</div>
          <h3>v{{ diffResult.versionFrom.versionNumber }}</h3>
          <StatusBadge :status="diffResult.versionFrom.status" />
          <p class="version-meta">
            {{ diffResult.versionFrom.author?.username }} &middot;
            {{ formatDate(diffResult.versionFrom.createdAt) }}
          </p>
        </el-card>
        <el-icon :size="24" color="#909399"><Right /></el-icon>
        <el-card class="version-info">
          <div class="version-label">To</div>
          <h3>v{{ diffResult.versionTo.versionNumber }}</h3>
          <StatusBadge :status="diffResult.versionTo.status" />
          <p class="version-meta">
            {{ diffResult.versionTo.author?.username }} &middot;
            {{ formatDate(diffResult.versionTo.createdAt) }}
          </p>
        </el-card>
      </div>

      <el-card class="diff-content">
        <div class="diff-lines">
          <div
            v-for="(change, idx) in changes"
            :key="idx"
            :class="['diff-block', { added: change.added, removed: change.removed }]"
          >
            <div v-for="(line, li) in change.value.split('\n').filter((l, i, a) => i < a.length - 1 || l)" :key="li" class="diff-line">
              <span class="diff-marker">{{ change.added ? '+' : change.removed ? '-' : ' ' }}</span>
              <span class="diff-text">{{ line }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.diff-view {
  max-width: 1000px;
  margin: 0 auto;
}

.diff-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.diff-header h1 {
  margin: 0;
  font-size: 24px;
  color: var(--text-primary);
}

.version-info-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.version-info {
  flex: 1;
  border-radius: 12px;
}

.version-label {
  font-size: 12px;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 4px;
}

.version-info h3 {
  margin: 0 0 8px;
  font-size: 20px;
}

.version-meta {
  margin: 8px 0 0;
  font-size: 13px;
  color: #909399;
}

.diff-content {
  border-radius: 12px;
}

.diff-lines {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.diff-block.added {
  background-color: #f0fff4;
}

.diff-block.removed {
  background-color: #fff5f5;
}

.diff-line {
  display: flex;
  padding: 1px 12px;
}

.diff-marker {
  width: 20px;
  flex-shrink: 0;
  color: #909399;
  user-select: none;
}

.diff-block.added .diff-marker {
  color: #67c23a;
}

.diff-block.removed .diff-marker {
  color: #f56c6c;
}

.diff-text {
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
