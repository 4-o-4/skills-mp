<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { variantsApi, testSetsApi } from '@/api'
import type { Variant, TestSet, CompareResult } from '@/types'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const skillId = Number(route.params.id)

const variants = ref<Variant[]>([])
const testSets = ref<TestSet[]>([])
const selectedTestSet = ref<number | null>(null)
const selectedVariants = ref<number[]>([])
const result = ref<CompareResult | null>(null)
const loading = ref(false)

onMounted(async () => {
  try {
    const { data: v } = await variantsApi.getBySkill(skillId)
    variants.value = v
    selectedVariants.value = v.map((x) => x.id)
  } catch { /* interceptor */ }
})

async function loadTestSets() {
  if (testSets.value.length) return
  try {
    const { data } = await testSetsApi.getByProject(1)
    testSets.value = data
  } catch { /* interceptor */ }
}

async function runComparison() {
  if (!selectedTestSet.value || selectedVariants.value.length < 2) {
    ElMessage.warning('Select a test set and at least 2 variants')
    return
  }
  loading.value = true
  try {
    const { data } = await variantsApi.compare({
      variantIds: selectedVariants.value,
      testSetId: selectedTestSet.value
    })
    result.value = data
  } catch { /* interceptor */ } finally {
    loading.value = false
  }
}

function getResult(variantId: number, testCaseId: number) {
  return result.value?.results.find(
    (r) => r.variantId === variantId && r.testCaseId === testCaseId
  )
}

function uniqueTestCaseIds() {
  if (!result.value) return []
  return [...new Set(result.value.results.map((r) => r.testCaseId))]
}

function avgScore(variantId: number) {
  if (!result.value) return 0
  const items = result.value.results.filter((r) => r.variantId === variantId)
  if (!items.length) return 0
  return (items.reduce((s, r) => s + r.score, 0) / items.length).toFixed(2)
}

function avgLatency(variantId: number) {
  if (!result.value) return 0
  const items = result.value.results.filter((r) => r.variantId === variantId)
  if (!items.length) return 0
  return (items.reduce((s, r) => s + r.latency, 0) / items.length).toFixed(0)
}
</script>

<template>
  <div class="compare-view">
    <div class="page-header">
      <el-button text @click="router.push(`/skills/${skillId}`)">
        <el-icon><ArrowLeft /></el-icon> Back to Skill
      </el-button>
      <h1>Variant Comparison</h1>
    </div>

    <el-card class="controls-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="8">
          <el-select
            v-model="selectedTestSet"
            placeholder="Select test set"
            style="width: 100%"
            @focus="loadTestSets"
          >
            <el-option
              v-for="ts in testSets"
              :key="ts.id"
              :label="ts.name"
              :value="ts.id"
            />
          </el-select>
        </el-col>
        <el-col :span="12">
          <el-checkbox-group v-model="selectedVariants">
            <el-checkbox
              v-for="v in variants"
              :key="v.id"
              :value="v.id"
              :label="v.name"
            />
          </el-checkbox-group>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" :loading="loading" @click="runComparison">
            <el-icon><DataAnalysis /></el-icon> Run
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card v-if="result" class="results-card">
      <el-table :data="uniqueTestCaseIds()" stripe border>
        <el-table-column label="Test Case" width="120">
          <template #default="{ row }">Case #{{ row }}</template>
        </el-table-column>
        <el-table-column
          v-for="v in result.variants"
          :key="v.id"
          :label="v.name"
          min-width="200"
        >
          <template #default="{ row }">
            <div v-if="getResult(v.id, row)" class="result-cell">
              <div class="result-output">{{ getResult(v.id, row)!.output }}</div>
              <div class="result-meta">
                <el-tag :type="getResult(v.id, row)!.passed ? 'success' : 'danger'" size="small">
                  {{ getResult(v.id, row)!.passed ? 'PASS' : 'FAIL' }}
                </el-tag>
                <span>Score: {{ getResult(v.id, row)!.score.toFixed(2) }}</span>
                <span>{{ getResult(v.id, row)!.latency }}ms</span>
              </div>
            </div>
            <span v-else class="no-result">-</span>
          </template>
        </el-table-column>
      </el-table>

      <el-divider />

      <h4>Summary</h4>
      <el-table :data="result.variants" stripe>
        <el-table-column prop="name" label="Variant" />
        <el-table-column label="Avg Score" width="120">
          <template #default="{ row }">{{ avgScore(row.id) }}</template>
        </el-table-column>
        <el-table-column label="Avg Latency" width="140">
          <template #default="{ row }">{{ avgLatency(row.id) }}ms</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.compare-view {
  max-width: 1200px;
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

.controls-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.results-card {
  border-radius: 12px;
}

.result-cell {
  font-size: 13px;
}

.result-output {
  margin-bottom: 6px;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 100px;
  overflow-y: auto;
}

.result-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.no-result {
  color: #c0c4cc;
}
</style>
