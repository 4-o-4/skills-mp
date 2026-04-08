<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProjectsStore } from '@/stores/projects'
import EmptyState from '@/components/EmptyState.vue'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const store = useProjectsStore()

const search = ref('')
const showCreate = ref(false)
const createFormRef = ref<FormInstance>()
const createLoading = ref(false)
const createForm = ref({ name: '', description: '' })

const createRules: FormRules = {
  name: [{ required: true, message: 'Project name is required', trigger: 'blur' }]
}

const filtered = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return store.projects
  return store.projects.filter(
    (p) => p.name.toLowerCase().includes(q) || p.description.toLowerCase().includes(q)
  )
})

onMounted(() => {
  store.fetchAll()
})

function goToProject(id: number) {
  router.push(`/projects/${id}`)
}

async function handleCreate() {
  const valid = await createFormRef.value?.validate().catch(() => false)
  if (!valid) return
  createLoading.value = true
  try {
    const p = await store.create(createForm.value)
    showCreate.value = false
    createForm.value = { name: '', description: '' }
    router.push(`/projects/${p.id}`)
  } finally {
    createLoading.value = false
  }
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}
</script>

<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <div>
        <p class="section-kicker">01 / Workspace</p>
        <h1>My Projects</h1>
        <p class="subtitle">Manage your skill projects</p>
      </div>
      <el-button type="primary" @click="showCreate = true">
        <el-icon><Plus /></el-icon>
        Create Project
      </el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="search"
        placeholder="Search projects..."
        prefix-icon="Search"
        size="large"
        clearable
      />
    </div>

    <div v-loading="store.loading">
      <div v-if="filtered.length" class="project-grid">
        <el-card
          v-for="project in filtered"
          :key="project.id"
          shadow="hover"
          class="project-card"
          @click="goToProject(project.id)"
        >
          <div class="card-header">
            <h3>{{ project.name }}</h3>
            <el-tag size="small" type="info">{{ project.skillCount }} skills</el-tag>
          </div>
          <p class="card-desc">{{ project.description || 'No description' }}</p>
          <div class="card-footer">
            <span class="card-owner">
              <el-icon><User /></el-icon>
              {{ project.owner?.username || 'Unknown' }}
            </span>
            <span class="card-date">{{ formatDate(project.updatedAt) }}</span>
          </div>
        </el-card>
      </div>

      <EmptyState
        v-else-if="!store.loading"
        title="No projects yet"
        description="Create your first project to get started."
        icon="FolderAdd"
      >
        <template #action>
          <el-button type="primary" @click="showCreate = true">Create Project</el-button>
        </template>
      </EmptyState>
    </div>

    <el-dialog v-model="showCreate" title="Create Project" width="480px" destroy-on-close>
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-position="top"
        @submit.prevent="handleCreate"
      >
        <el-form-item label="Name" prop="name">
          <el-input v-model="createForm.name" placeholder="Project name" />
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="Optional description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">Cancel</el-button>
        <el-button type="primary" :loading="createLoading" @click="handleCreate">Create</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.dashboard {
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

.dashboard-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 28px;
  gap: 20px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: clamp(1.75rem, 2.5vw, 2.125rem);
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.subtitle {
  margin: 6px 0 0;
  font-size: 15px;
  color: var(--text-secondary);
  max-width: 36ch;
  line-height: 1.5;
}

.search-bar {
  margin-bottom: 28px;
  max-width: 420px;
}

.search-bar :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border-subtle) inset !important;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 18px;
}

.project-card {
  cursor: pointer;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
  border-radius: var(--radius-lg) !important;
}

.project-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card-hover) !important;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  gap: 12px;
}

.card-header h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--text-primary);
}

.card-desc {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.55;
  margin: 0 0 18px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: var(--text-muted);
}

.card-owner {
  display: flex;
  align-items: center;
  gap: 6px;
}

.card-date {
  font-size: 12px;
  font-weight: 500;
  opacity: 0.9;
}
</style>
