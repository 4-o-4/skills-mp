<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProjectsStore } from '@/stores/projects'
import { useSkillsStore } from '@/stores/skills'
import {
  skillsApi, testSetsApi, environmentsApi, releasesApi, gitApi
} from '@/api'
import type {
  TestSet, Environment, Release, GitStatus, Skill, TestCase
} from '@/types'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import StatusBadge from '@/components/StatusBadge.vue'
import TagList from '@/components/TagList.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const projectStore = useProjectsStore()
const skillsStore = useSkillsStore()

const projectId = Number(route.params.id)
const activeTab = ref('skills')
const loading = ref(false)

const testSets = ref<TestSet[]>([])
const environments = ref<Environment[]>([])
const releases = ref<Release[]>([])
const gitStatus = ref<GitStatus | null>(null)

// ── Dialogs ──
const showEditProject = ref(false)
const editForm = ref({ name: '', description: '' })

const showCreateSkill = ref(false)
const skillFormRef = ref<FormInstance>()
const skillForm = ref({ name: '', description: '' })
const skillRules: FormRules = {
  name: [{ required: true, message: 'Name is required', trigger: 'blur' }]
}

const showCreateTestSet = ref(false)
const tsFormRef = ref<FormInstance>()
const tsForm = ref({ name: '', description: '' })
const tsCases = ref<{ input: string; expectedOutput: string }[]>([])
const tsRules: FormRules = {
  name: [{ required: true, message: 'Name is required', trigger: 'blur' }]
}

const showCreateEnv = ref(false)
const envFormRef = ref<FormInstance>()
const envForm = ref({ name: '', description: '' })
const envRules: FormRules = {
  name: [{ required: true, message: 'Name is required', trigger: 'blur' }]
}

const showDeployDialog = ref(false)
const deployEnvId = ref<number | null>(null)
const deployVersionId = ref<number | null>(null)

const gitForm = ref({ repoUrl: '', branch: 'main' })

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([
      projectStore.fetchById(projectId),
      skillsStore.fetchByProject(projectId)
    ])
    editForm.value = {
      name: projectStore.currentProject?.name || '',
      description: projectStore.currentProject?.description || ''
    }
  } finally {
    loading.value = false
  }
})

async function loadTabData(tab: string) {
  if (tab === 'testsets' && testSets.value.length === 0) {
    try {
      const { data } = await testSetsApi.getByProject(projectId)
      testSets.value = data
    } catch { /* handled by interceptor */ }
  } else if (tab === 'environments' && environments.value.length === 0) {
    try {
      const { data } = await environmentsApi.getAll(projectId)
      environments.value = data
    } catch { /* handled by interceptor */ }
  } else if (tab === 'releases' && releases.value.length === 0) {
    try {
      const { data } = await releasesApi.getByProject(projectId)
      releases.value = data
    } catch { /* handled by interceptor */ }
  } else if (tab === 'settings' && !gitStatus.value) {
    try {
      const { data } = await gitApi.getStatus(projectId)
      gitStatus.value = data
    } catch { /* handled by interceptor */ }
  }
}

function onTabChange(tab: string) {
  loadTabData(tab)
}

async function saveProject() {
  try {
    await projectStore.update(projectId, editForm.value)
    showEditProject.value = false
  } catch { /* handled */ }
}

async function createSkill() {
  const valid = await skillFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    const skill = await skillsStore.create(projectId, skillForm.value)
    showCreateSkill.value = false
    skillForm.value = { name: '', description: '' }
    router.push(`/skills/${skill.id}`)
  } catch { /* handled */ }
}

async function deleteSkill(skill: Skill) {
  await ElMessageBox.confirm(`Delete skill "${skill.name}"?`, 'Confirm', { type: 'warning' })
  await skillsStore.remove(skill.id)
}

async function createTestSet() {
  const valid = await tsFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    const { data } = await testSetsApi.create(projectId, tsForm.value)
    for (const c of tsCases.value) {
      await testSetsApi.addCase(data.id, { input: c.input, expectedOutput: c.expectedOutput } as Partial<TestCase>)
    }
    testSets.value.unshift({ ...data, caseCount: tsCases.value.length })
    showCreateTestSet.value = false
    tsForm.value = { name: '', description: '' }
    tsCases.value = []
    ElMessage.success('Test set created')
  } catch { /* handled */ }
}

function addTestCase() {
  tsCases.value.push({ input: '', expectedOutput: '' })
}

function removeTestCase(idx: number) {
  tsCases.value.splice(idx, 1)
}

async function createEnvironment() {
  const valid = await envFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    const { data } = await environmentsApi.create(projectId, envForm.value)
    environments.value.push(data)
    showCreateEnv.value = false
    envForm.value = { name: '', description: '' }
    ElMessage.success('Environment created')
  } catch { /* handled */ }
}

async function deploy() {
  if (!deployEnvId.value || !deployVersionId.value) return
  try {
    await environmentsApi.deploy(deployEnvId.value, { versionId: deployVersionId.value })
    ElMessage.success('Deployed successfully')
    showDeployDialog.value = false
    const { data } = await environmentsApi.getAll(projectId)
    environments.value = data
  } catch { /* handled */ }
}

async function connectGit() {
  try {
    await gitApi.connect(projectId, gitForm.value)
    const { data } = await gitApi.getStatus(projectId)
    gitStatus.value = data
    ElMessage.success('Git repository connected')
  } catch { /* handled */ }
}

async function syncGit() {
  try {
    await gitApi.sync(projectId)
    ElMessage.success('Git sync triggered')
  } catch { /* handled */ }
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}
</script>

<template>
  <div v-loading="loading" class="project-detail">
    <template v-if="projectStore.currentProject">
      <div class="page-header">
        <div>
          <p class="section-kicker">Workspace</p>
          <h1>{{ projectStore.currentProject.name }}</h1>
          <p class="desc">{{ projectStore.currentProject.description || 'No description' }}</p>
        </div>
        <el-button @click="showEditProject = true">
          <el-icon><Edit /></el-icon>
          Edit
        </el-button>
      </div>

      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <!-- Skills Tab -->
        <el-tab-pane label="Skills" name="skills">
          <div class="tab-header">
            <span class="tab-count">{{ skillsStore.skills.length }} skills</span>
            <el-button type="primary" size="small" @click="showCreateSkill = true">
              <el-icon><Plus /></el-icon> New Skill
            </el-button>
          </div>
          <div v-if="skillsStore.skills.length" class="skill-list">
            <el-card v-for="skill in skillsStore.skills" :key="skill.id" shadow="hover" class="skill-card">
              <div class="skill-card-content">
                <div class="skill-info" @click="router.push(`/skills/${skill.id}`)">
                  <h4>{{ skill.name }}</h4>
                  <p>{{ skill.description || 'No description' }}</p>
                  <div class="skill-meta">
                    <StatusBadge :status="skill.status" />
                    <TagList v-if="skill.tags.length" :tags="skill.tags" />
                  </div>
                </div>
                <div class="skill-actions">
                  <el-button text type="primary" @click="router.push(`/skills/${skill.id}`)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button text type="danger" @click="deleteSkill(skill)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
          <EmptyState v-else title="No skills" description="Create your first skill to get started." icon="Document">
            <template #action>
              <el-button type="primary" @click="showCreateSkill = true">Create Skill</el-button>
            </template>
          </EmptyState>
        </el-tab-pane>

        <!-- Test Sets Tab -->
        <el-tab-pane label="Test Sets" name="testsets">
          <div class="tab-header">
            <span class="tab-count">{{ testSets.length }} test sets</span>
            <el-button type="primary" size="small" @click="showCreateTestSet = true">
              <el-icon><Plus /></el-icon> New Test Set
            </el-button>
          </div>
          <el-table v-if="testSets.length" :data="testSets" stripe>
            <el-table-column prop="name" label="Name" />
            <el-table-column prop="description" label="Description" />
            <el-table-column prop="caseCount" label="Cases" width="100" />
            <el-table-column label="Created" width="140">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
          </el-table>
          <EmptyState v-else title="No test sets" description="Create test sets to evaluate your skills." icon="List" />
        </el-tab-pane>

        <!-- Environments Tab -->
        <el-tab-pane label="Environments" name="environments">
          <div class="tab-header">
            <span class="tab-count">{{ environments.length }} environments</span>
            <el-button type="primary" size="small" @click="showCreateEnv = true">
              <el-icon><Plus /></el-icon> New Environment
            </el-button>
          </div>
          <div v-if="environments.length" class="env-grid">
            <el-card v-for="env in environments" :key="env.id" shadow="hover">
              <div class="env-header">
                <h4>{{ env.name }}</h4>
                <el-tag v-if="env.activeVersionNumber" type="success" size="small">
                  v{{ env.activeVersionNumber }}
                </el-tag>
                <el-tag v-else type="info" size="small">No deployment</el-tag>
              </div>
              <p class="env-desc">{{ env.description || 'No description' }}</p>
              <el-button
                size="small"
                type="primary"
                @click="deployEnvId = env.id; showDeployDialog = true"
              >
                Deploy
              </el-button>
            </el-card>
          </div>
          <EmptyState v-else title="No environments" description="Create environments for staging and production." icon="Monitor" />
        </el-tab-pane>

        <!-- Releases Tab -->
        <el-tab-pane label="Releases" name="releases">
          <el-table v-if="releases.length" :data="releases" stripe>
            <el-table-column prop="versionNumber" label="Version" width="120" />
            <el-table-column prop="environmentName" label="Environment" width="140" />
            <el-table-column prop="notes" label="Notes" />
            <el-table-column prop="gitTag" label="Git Tag" width="120" />
            <el-table-column label="Released By" width="140">
              <template #default="{ row }">{{ row.createdBy?.username }}</template>
            </el-table-column>
            <el-table-column label="Date" width="140">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
          </el-table>
          <EmptyState v-else title="No releases" description="Releases will appear here once created." icon="Promotion" />
        </el-tab-pane>

        <!-- Settings Tab -->
        <el-tab-pane label="Settings" name="settings">
          <el-card>
            <h3>Git Integration</h3>
            <div v-if="gitStatus?.connected" class="git-connected">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="Status">
                  <el-tag type="success" size="small">Connected</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="Repository">{{ gitStatus.repoUrl }}</el-descriptions-item>
                <el-descriptions-item label="Branch">{{ gitStatus.branch }}</el-descriptions-item>
                <el-descriptions-item label="Last Sync">{{ gitStatus.lastSync || 'Never' }}</el-descriptions-item>
              </el-descriptions>
              <el-button type="primary" style="margin-top: 16px" @click="syncGit">
                <el-icon><Refresh /></el-icon> Sync Now
              </el-button>
            </div>
            <div v-else>
              <p style="color: #909399; margin-bottom: 16px">Connect a Git repository to enable version syncing.</p>
              <el-form label-position="top">
                <el-form-item label="Repository URL">
                  <el-input v-model="gitForm.repoUrl" placeholder="https://github.com/org/repo.git" />
                </el-form-item>
                <el-form-item label="Branch">
                  <el-input v-model="gitForm.branch" placeholder="main" />
                </el-form-item>
                <el-button type="primary" @click="connectGit">Connect</el-button>
              </el-form>
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </template>

    <!-- Edit Project Dialog -->
    <el-dialog v-model="showEditProject" title="Edit Project" width="480px">
      <el-form label-position="top">
        <el-form-item label="Name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditProject = false">Cancel</el-button>
        <el-button type="primary" @click="saveProject">Save</el-button>
      </template>
    </el-dialog>

    <!-- Create Skill Dialog -->
    <el-dialog v-model="showCreateSkill" title="Create Skill" width="480px" destroy-on-close>
      <el-form ref="skillFormRef" :model="skillForm" :rules="skillRules" label-position="top" @submit.prevent="createSkill">
        <el-form-item label="Name" prop="name">
          <el-input v-model="skillForm.name" placeholder="Skill name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="skillForm.description" type="textarea" :rows="3" placeholder="Optional description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateSkill = false">Cancel</el-button>
        <el-button type="primary" @click="createSkill">Create</el-button>
      </template>
    </el-dialog>

    <!-- Create Test Set Dialog -->
    <el-dialog v-model="showCreateTestSet" title="Create Test Set" width="640px" destroy-on-close>
      <el-form ref="tsFormRef" :model="tsForm" :rules="tsRules" label-position="top" @submit.prevent="createTestSet">
        <el-form-item label="Name" prop="name">
          <el-input v-model="tsForm.name" placeholder="Test set name" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="tsForm.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <div style="margin-top: 16px">
        <div class="section-header">
          <h4>Test Cases</h4>
          <el-button size="small" @click="addTestCase">
            <el-icon><Plus /></el-icon> Add Case
          </el-button>
        </div>
        <div v-for="(tc, idx) in tsCases" :key="idx" class="test-case-row">
          <el-input v-model="tc.input" placeholder="Input" type="textarea" :rows="2" />
          <el-input v-model="tc.expectedOutput" placeholder="Expected output" type="textarea" :rows="2" />
          <el-button text type="danger" @click="removeTestCase(idx)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="showCreateTestSet = false">Cancel</el-button>
        <el-button type="primary" @click="createTestSet">Create</el-button>
      </template>
    </el-dialog>

    <!-- Create Environment Dialog -->
    <el-dialog v-model="showCreateEnv" title="Create Environment" width="480px" destroy-on-close>
      <el-form ref="envFormRef" :model="envForm" :rules="envRules" label-position="top" @submit.prevent="createEnvironment">
        <el-form-item label="Name" prop="name">
          <el-input v-model="envForm.name" placeholder="e.g., staging, production" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="envForm.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateEnv = false">Cancel</el-button>
        <el-button type="primary" @click="createEnvironment">Create</el-button>
      </template>
    </el-dialog>

    <!-- Deploy Dialog -->
    <el-dialog v-model="showDeployDialog" title="Deploy Version" width="400px" destroy-on-close>
      <el-form label-position="top">
        <el-form-item label="Version ID">
          <el-input-number v-model="deployVersionId" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDeployDialog = false">Cancel</el-button>
        <el-button type="primary" @click="deploy">Deploy</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.project-detail {
  max-width: 1120px;
  margin: 0 auto;
}

.section-kicker {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--text-muted);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
  gap: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: clamp(1.5rem, 2.2vw, 1.875rem);
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.desc {
  color: var(--text-secondary);
  margin: 8px 0 0;
  font-size: 15px;
  line-height: 1.5;
  max-width: 56ch;
}

.tab-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.tab-count {
  color: var(--text-muted);
  font-size: 13px;
  font-weight: 500;
}

.skill-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skill-card {
  border-radius: var(--radius-lg) !important;
}

.skill-card-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.skill-info {
  flex: 1;
  cursor: pointer;
}

.skill-info h4 {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--text-primary);
}

.skill-info p {
  margin: 0 0 8px;
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.5;
}

.skill-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.skill-actions {
  display: flex;
  gap: 4px;
}

.env-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.env-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.env-header h4 {
  margin: 0;
}

.env-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0 0 12px;
  line-height: 1.5;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.section-header h4 {
  margin: 0;
}

.test-case-row {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 8px;
  margin-bottom: 8px;
  align-items: start;
}

.git-connected {
  margin-top: 12px;
}
</style>
