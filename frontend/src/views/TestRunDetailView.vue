<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { testRunsApi } from '@/api'
import type { TestRunDetail } from '@/types'

const route = useRoute()
const router = useRouter()
const runId = Number(route.params.id)

const testRun = ref<TestRunDetail | null>(null)
const loading = ref(false)
let pollTimer: ReturnType<typeof setInterval> | null = null

const passRate = computed(() => {
  if (!testRun.value || !testRun.value.totalCases) return 0
  return Math.round((testRun.value.passedCases / testRun.value.totalCases) * 100)
})

const statusType = computed(() => {
  const map: Record<string, string> = {
    QUEUED: 'info', RUNNING: 'warning', COMPLETED: 'success', FAILED: 'danger', CANCELLED: 'info'
  }
  return map[testRun.value?.status || ''] || 'info'
})

onMounted(async () => {
  await fetchRun()
  if (testRun.value?.status === 'RUNNING' || testRun.value?.status === 'QUEUED') {
    pollTimer = setInterval(fetchRun, 3000)
  }
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})

async function fetchRun() {
  loading.value = !testRun.value
  try {
    const { data } = await testRunsApi.getById(runId)
    testRun.value = data
    if (data.status !== 'RUNNING' && data.status !== 'QUEUED' && pollTimer) {
      clearInterval(pollTimer)
      pollTimer = null
    }
  } catch { /* interceptor */ } finally {
    loading.value = false
  }
}

function formatDate(d: string | null) {
  if (!d) return '-'
  return new Date(d).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}
</script>

<template>
  <div v-loading="loading" class="test-run-detail">
    <template v-if="testRun">
      <div class="page-header">
        <el-button text @click="router.back()">
          <el-icon><ArrowLeft /></el-icon> Back
        </el-button>
        <h1>Test Run #{{ testRun.id }}</h1>
        <el-tag :type="statusType" size="large" effect="dark">
          {{ testRun.status }}
        </el-tag>
      </div>

      <el-card class="info-card">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="Test Set">{{ testRun.testSetName }}</el-descriptions-item>
          <el-descriptions-item label="Version ID">{{ testRun.versionId }}</el-descriptions-item>
          <el-descriptions-item label="Variant ID">{{ testRun.variantId ?? 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="Created By">{{ testRun.createdBy?.username }}</el-descriptions-item>
          <el-descriptions-item label="Started">{{ formatDate(testRun.startedAt) }}</el-descriptions-item>
          <el-descriptions-item label="Completed">{{ formatDate(testRun.completedAt) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="progress-card">
        <div class="progress-header">
          <h3>Results</h3>
          <div class="progress-stats">
            <span class="stat pass">{{ testRun.passedCases }} passed</span>
            <span class="stat fail">{{ testRun.failedCases }} failed</span>
            <span class="stat total">{{ testRun.totalCases }} total</span>
          </div>
        </div>
        <el-progress
          :percentage="passRate"
          :color="passRate >= 80 ? '#67c23a' : passRate >= 50 ? '#e6a23c' : '#f56c6c'"
          :stroke-width="20"
          :text-inside="true"
        />
      </el-card>

      <el-card v-if="testRun.results?.length" class="results-table-card">
        <el-table :data="testRun.results" stripe>
          <el-table-column label="Test Case" width="100">
            <template #default="{ row }">
              #{{ row.testCaseId }}
            </template>
          </el-table-column>
          <el-table-column prop="evaluatorName" label="Evaluator" width="160" />
          <el-table-column label="Result" width="100">
            <template #default="{ row }">
              <el-tag :type="row.passed ? 'success' : 'danger'" size="small" effect="dark">
                {{ row.passed ? 'PASS' : 'FAIL' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Score" width="100">
            <template #default="{ row }">
              {{ row.score.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="rawOutput" label="Output" show-overflow-tooltip />
          <el-table-column prop="explanation" label="Explanation" show-overflow-tooltip />
        </el-table>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.test-run-detail {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: var(--text-primary);
}

.info-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.progress-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.progress-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.progress-header h3 {
  margin: 0;
}

.progress-stats {
  display: flex;
  gap: 16px;
}

.stat {
  font-size: 14px;
  font-weight: 600;
}

.stat.pass {
  color: #67c23a;
}

.stat.fail {
  color: #f56c6c;
}

.stat.total {
  color: #909399;
}

.results-table-card {
  border-radius: 12px;
}
</style>
