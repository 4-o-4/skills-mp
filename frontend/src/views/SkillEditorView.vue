<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSkillsStore } from '@/stores/skills'
import {
  versionsApi, variantsApi, testRunsApi, reviewsApi
} from '@/api'
import type {
  Version, Variant, TestRun, Review, EntityStatus
} from '@/types'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import StatusBadge from '@/components/StatusBadge.vue'
import RatingStars from '@/components/RatingStars.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const skillsStore = useSkillsStore()

const skillId = Number(route.params.id)
const activeTab = ref('prompt')
const loading = ref(false)
const saving = ref(false)

const editName = ref('')
const editDesc = ref('')
const editPrompt = ref('')

const versions = ref<Version[]>([])
const variants = ref<Variant[]>([])
const testRuns = ref<TestRun[]>([])
const reviews = ref<Review[]>([])

const showVersionDialog = ref(false)
const versionForm = ref({ content: '', changelog: '' })

const showVariantDialog = ref(false)
const variantFormRef = ref<FormInstance>()
const variantForm = ref({
  name: '', modelProvider: 'openai', modelName: 'gpt-4',
  systemPrompt: '', temperature: 0.7, maxTokens: 2048, config: '{}'
})
const variantRules: FormRules = {
  name: [{ required: true, message: 'Name is required', trigger: 'blur' }],
  modelName: [{ required: true, message: 'Model is required', trigger: 'blur' }]
}

const showReviewDialog = ref(false)
const reviewForm = ref({ rating: 0, comment: '', status: 'PENDING' as string })

const statusFlow: Record<string, EntityStatus> = {
  DRAFT: 'VERIFIED',
  VERIFIED: 'PUBLISHED',
  PUBLISHED: 'ARCHIVED'
}

const nextStatus = computed(() => {
  const current = skillsStore.currentSkill?.status
  return current ? statusFlow[current] : null
})

onMounted(async () => {
  loading.value = true
  try {
    await skillsStore.fetchById(skillId)
    const skill = skillsStore.currentSkill
    if (skill) {
      editName.value = skill.name
      editDesc.value = skill.description
      editPrompt.value = skill.promptBody || ''
    }
  } finally {
    loading.value = false
  }
})

async function loadTabData(tab: string) {
  if (tab === 'versions' && versions.value.length === 0) {
    try {
      const { data } = await versionsApi.getBySkill(skillId)
      versions.value = data
    } catch {}
  } else if (tab === 'variants' && variants.value.length === 0) {
    try {
      const { data } = await variantsApi.getBySkill(skillId)
      variants.value = data
    } catch {}
  } else if (tab === 'testruns' && testRuns.value.length === 0) {
    try {
      const { data } = await testRunsApi.getAll({ skillId })
      testRuns.value = data
    } catch {}
  } else if (tab === 'reviews' && reviews.value.length === 0) {
    try {
      const latestVersion = skillsStore.currentSkill?.latestVersion
      if (latestVersion) {
        const { data } = await reviewsApi.getByVersion(latestVersion.id)
        reviews.value = data
      }
    } catch {}
  }
}

function onTabChange(tab: string) {
  loadTabData(tab)
}

async function saveSkill() {
  saving.value = true
  try {
    await skillsStore.update(skillId, {
      name: editName.value,
      description: editDesc.value,
      promptBody: editPrompt.value
    })
  } finally {
    saving.value = false
  }
}

async function changeStatus() {
  if (!nextStatus.value) return
  await skillsStore.updateStatus(skillId, nextStatus.value)
}

async function createVersion() {
  try {
    const { data } = await versionsApi.create(skillId, versionForm.value)
    versions.value.unshift(data)
    showVersionDialog.value = false
    versionForm.value = { content: '', changelog: '' }
    ElMessage.success('Version created')
  } catch {}
}

function viewDiff(v1: Version, v2: Version) {
  router.push(`/skills/${skillId}/versions/${v1.id}/diff/${v2.id}`)
}

async function createVariant() {
  const valid = await variantFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    const { data } = await variantsApi.create(skillId, variantForm.value)
    variants.value.push(data)
    showVariantDialog.value = false
    variantForm.value = {
      name: '', modelProvider: 'openai', modelName: 'gpt-4',
      systemPrompt: '', temperature: 0.7, maxTokens: 2048, config: '{}'
    }
    ElMessage.success('Variant created')
  } catch {}
}

async function deleteVariant(id: number) {
  try {
    await variantsApi.delete(id)
    variants.value = variants.value.filter((v) => v.id !== id)
    ElMessage.success('Variant deleted')
  } catch {}
}

async function submitReview() {
  const latestVersion = skillsStore.currentSkill?.latestVersion
  if (!latestVersion) {
    ElMessage.warning('No version to review')
    return
  }
  try {
    const { data } = await reviewsApi.create(latestVersion.id, reviewForm.value)
    reviews.value.unshift(data)
    showReviewDialog.value = false
    reviewForm.value = { rating: 0, comment: '', status: 'PENDING' }
    ElMessage.success('Review submitted')
  } catch {}
}

function goToCompare() {
  router.push(`/skills/${skillId}/variants/compare`)
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

const runStatusType = (s: string) => {
  const map: Record<string, string> = {
    QUEUED: 'info', RUNNING: 'warning', COMPLETED: 'success', FAILED: 'danger', CANCELLED: 'info'
  }
  return map[s] || 'info'
}
</script>

<template>
  <div v-loading="loading" class="skill-editor">
    <template v-if="skillsStore.currentSkill">
      <div class="editor-header">
        <div class="header-left">
          <el-button text @click="router.back()">
            <el-icon><ArrowLeft /></el-icon> Back
          </el-button>
          <StatusBadge :status="skillsStore.currentSkill.status" />
          <h1>{{ skillsStore.currentSkill.name }}</h1>
        </div>
        <div class="header-actions">
          <el-button
            v-if="nextStatus"
            type="warning"
            plain
            @click="changeStatus"
          >
            Move to {{ nextStatus }}
          </el-button>
          <el-button type="primary" :loading="saving" @click="saveSkill">
            <el-icon><Check /></el-icon> Save
          </el-button>
        </div>
      </div>

      <div class="editor-body">
        <aside class="meta-panel">
          <el-form label-position="top">
            <el-form-item label="Name">
              <el-input v-model="editName" />
            </el-form-item>
            <el-form-item label="Description">
              <el-input v-model="editDesc" type="textarea" :rows="4" />
            </el-form-item>
          </el-form>
          <el-divider />
          <div class="meta-info">
            <div class="meta-row">
              <span class="meta-label">Owner</span>
              <span>{{ skillsStore.currentSkill.owner?.username }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">Variants</span>
              <span>{{ skillsStore.currentSkill.variantCount }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">Test Runs</span>
              <span>{{ skillsStore.currentSkill.testRunCount }}</span>
            </div>
            <div class="meta-row">
              <span class="meta-label">Latest Version</span>
              <span>{{ skillsStore.currentSkill.latestVersion?.versionNumber || 'None' }}</span>
            </div>
          </div>
        </aside>

        <main class="content-panel">
          <el-tabs v-model="activeTab" @tab-change="onTabChange">
            <el-tab-pane label="Prompt" name="prompt">
              <div class="prompt-editor">
                <el-input
                  v-model="editPrompt"
                  type="textarea"
                  :rows="20"
                  placeholder="Enter the prompt body here..."
                  class="prompt-textarea"
                />
              </div>
            </el-tab-pane>

            <el-tab-pane label="Versions" name="versions">
              <div class="tab-header">
                <span class="tab-count">{{ versions.length }} versions</span>
                <el-button type="primary" size="small" @click="showVersionDialog = true">
                  <el-icon><Plus /></el-icon> New Version
                </el-button>
              </div>
              <el-table v-if="versions.length" :data="versions" stripe>
                <el-table-column prop="versionNumber" label="Version" width="120" />
                <el-table-column label="Status" width="120">
                  <template #default="{ row }">
                    <StatusBadge :status="row.status" />
                  </template>
                </el-table-column>
                <el-table-column prop="changelog" label="Changelog" />
                <el-table-column prop="author.username" label="Author" width="120" />
                <el-table-column label="Date" width="140">
                  <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
                </el-table-column>
                <el-table-column label="Actions" width="100">
                  <template #default="{ row, $index }">
                    <el-button
                      v-if="$index < versions.length - 1"
                      text
                      type="primary"
                      size="small"
                      @click="viewDiff(versions[$index + 1], row)"
                    >
                      Diff
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="Variants" name="variants">
              <div class="tab-header">
                <span class="tab-count">{{ variants.length }} variants</span>
                <div>
                  <el-button size="small" @click="goToCompare">
                    <el-icon><DataAnalysis /></el-icon> Compare
                  </el-button>
                  <el-button type="primary" size="small" @click="showVariantDialog = true">
                    <el-icon><Plus /></el-icon> New Variant
                  </el-button>
                </div>
              </div>
              <div v-if="variants.length" class="variant-grid">
                <el-card v-for="v in variants" :key="v.id" shadow="hover" class="variant-card">
                  <div class="variant-header">
                    <h4>{{ v.name }}</h4>
                    <el-button text type="danger" size="small" @click="deleteVariant(v.id)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                  <el-descriptions :column="1" size="small">
                    <el-descriptions-item label="Provider">{{ v.modelProvider }}</el-descriptions-item>
                    <el-descriptions-item label="Model">{{ v.modelName }}</el-descriptions-item>
                    <el-descriptions-item label="Temp">{{ v.temperature }}</el-descriptions-item>
                    <el-descriptions-item label="Max Tokens">{{ v.maxTokens }}</el-descriptions-item>
                  </el-descriptions>
                </el-card>
              </div>
            </el-tab-pane>

            <el-tab-pane label="Test Runs" name="testruns">
              <el-table v-if="testRuns.length" :data="testRuns" stripe>
                <el-table-column label="Status" width="120">
                  <template #default="{ row }">
                    <el-tag :type="runStatusType(row.status)" size="small" effect="dark">
                      {{ row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="testSetName" label="Test Set" />
                <el-table-column label="Results" width="160">
                  <template #default="{ row }">
                    <span style="color: #67c23a">{{ row.passedCases }} passed</span> /
                    <span style="color: #f56c6c">{{ row.failedCases }} failed</span>
                  </template>
                </el-table-column>
                <el-table-column label="Date" width="140">
                  <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
                </el-table-column>
                <el-table-column label="" width="80">
                  <template #default="{ row }">
                    <el-button text type="primary" size="small" @click="router.push(`/test-runs/${row.id}`)">
                      View
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <EmptyState v-else title="No test runs" description="Run tests to see results here." icon="Histogram" />
            </el-tab-pane>

            <el-tab-pane label="Reviews" name="reviews">
              <div class="tab-header">
                <span class="tab-count">{{ reviews.length }} reviews</span>
                <el-button type="primary" size="small" @click="showReviewDialog = true">
                  <el-icon><ChatDotRound /></el-icon> Add Review
                </el-button>
              </div>
              <div v-if="reviews.length" class="review-list">
                <el-card v-for="r in reviews" :key="r.id" shadow="never" class="review-card">
                  <div class="review-header">
                    <span class="review-author">{{ r.author?.username }}</span>
                    <el-tag
                      :type="r.status === 'APPROVED' ? 'success' : r.status === 'REJECTED' ? 'danger' : 'info'"
                      size="small"
                    >
                      {{ r.status }}
                    </el-tag>
                    <span class="review-date">{{ formatDate(r.createdAt) }}</span>
                  </div>
                  <RatingStars v-if="r.rating" :model-value="r.rating" readonly />
                  <p class="review-comment">{{ r.comment }}</p>
                </el-card>
              </div>
              <EmptyState v-else title="No reviews" description="Reviews will appear here." icon="ChatDotRound" />
            </el-tab-pane>
          </el-tabs>
        </main>
      </div>
    </template>

    <el-dialog v-model="showVersionDialog" title="Create Version" width="560px" destroy-on-close>
      <el-form label-position="top">
        <el-form-item label="Content">
          <el-input v-model="versionForm.content" type="textarea" :rows="8" placeholder="Version content (snapshot of the prompt)" />
        </el-form-item>
        <el-form-item label="Changelog">
          <el-input v-model="versionForm.changelog" type="textarea" :rows="3" placeholder="What changed?" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showVersionDialog = false">Cancel</el-button>
        <el-button type="primary" @click="createVersion">Create</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showVariantDialog" title="Create Variant" width="560px" destroy-on-close>
      <el-form ref="variantFormRef" :model="variantForm" :rules="variantRules" label-position="top">
        <el-form-item label="Name" prop="name">
          <el-input v-model="variantForm.name" placeholder="Variant name" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="Provider">
              <el-select v-model="variantForm.modelProvider" style="width: 100%">
                <el-option label="OpenAI" value="openai" />
                <el-option label="Anthropic" value="anthropic" />
                <el-option label="Google" value="google" />
                <el-option label="Other" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Model" prop="modelName">
              <el-input v-model="variantForm.modelName" placeholder="e.g., gpt-4" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="System Prompt">
          <el-input v-model="variantForm.systemPrompt" type="textarea" :rows="4" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="Temperature">
              <el-slider v-model="variantForm.temperature" :min="0" :max="2" :step="0.1" show-input />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Max Tokens">
              <el-input-number v-model="variantForm.maxTokens" :min="1" :max="128000" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showVariantDialog = false">Cancel</el-button>
        <el-button type="primary" @click="createVariant">Create</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showReviewDialog" title="Add Review" width="480px" destroy-on-close>
      <el-form label-position="top">
        <el-form-item label="Rating">
          <RatingStars v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="Status">
          <el-radio-group v-model="reviewForm.status">
            <el-radio-button value="APPROVED">Approve</el-radio-button>
            <el-radio-button value="PENDING">Pending</el-radio-button>
            <el-radio-button value="REJECTED">Reject</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Comment">
          <el-input v-model="reviewForm.comment" type="textarea" :rows="4" placeholder="Your review..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">Cancel</el-button>
        <el-button type="primary" @click="submitReview">Submit</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.skill-editor {
  max-width: 1400px;
  margin: 0 auto;
}

.editor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  gap: 8px;
}

.editor-body {
  display: flex;
  gap: 24px;
}

.meta-panel {
  width: 280px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #ebeef5;
  height: fit-content;
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.meta-label {
  color: #909399;
}

.content-panel {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #ebeef5;
}

.prompt-editor {
  width: 100%;
}

.prompt-textarea :deep(.el-textarea__inner) {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 14px;
  line-height: 1.6;
  background: #fafafa;
  border-radius: 8px;
}

.tab-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.tab-count {
  color: #909399;
  font-size: 14px;
}

.variant-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.variant-card {
  border-radius: 10px;
}

.variant-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.variant-header h4 {
  margin: 0;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-card {
  border-radius: 10px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.review-author {
  font-weight: 600;
  color: var(--text-primary);
}

.review-date {
  color: #909399;
  font-size: 13px;
  margin-left: auto;
}

.review-comment {
  margin: 8px 0 0;
  color: #606266;
  font-size: 14px;
}
</style>
