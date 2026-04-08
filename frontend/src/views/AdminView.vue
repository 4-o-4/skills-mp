<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { authApi } from '@/api'
import type { User, UserRole } from '@/types'
import { ElMessage } from 'element-plus'

const activeTab = ref('users')
const users = ref<User[]>([])
const loading = ref(false)

const roleOptions: UserRole[] = ['ADMIN', 'EDITOR', 'REVIEWER', 'VIEWER']

const roleColors: Record<UserRole, string> = {
  ADMIN: 'warning',
  EDITOR: '',
  REVIEWER: 'success',
  VIEWER: 'info'
}

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await authApi.getMe()
    users.value = [data]
  } catch { /* interceptor */ } finally {
    loading.value = false
  }
})

function onRoleChange(user: User, newRole: UserRole) {
  user.role = newRole
  ElMessage.success(`Role for ${user.username} changed to ${newRole} (frontend only)`)
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric'
  })
}
</script>

<template>
  <div class="admin">
    <div class="page-header">
      <p class="section-kicker">03 / Admin</p>
      <h1>Administration</h1>
      <p class="subtitle">Manage users and integrations</p>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="Users" name="users">
        <el-table v-loading="loading" :data="users" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="Username" />
          <el-table-column prop="email" label="Email" />
          <el-table-column label="Role" width="200">
            <template #default="{ row }">
              <el-select
                :model-value="row.role"
                size="small"
                @update:model-value="(v: UserRole) => onRoleChange(row, v)"
              >
                <el-option
                  v-for="r in roleOptions"
                  :key="r"
                  :label="r"
                  :value="r"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="Role Badge" width="120">
            <template #default="{ row }">
              <el-tag :type="roleColors[row.role as UserRole]" size="small" effect="dark">
                {{ row.role }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Joined" width="140">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="Roles" name="roles">
        <el-card>
          <h3>Role Definitions</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="ADMIN">Full system access, user management, all operations</el-descriptions-item>
            <el-descriptions-item label="EDITOR">Create and edit skills, versions, variants, test sets</el-descriptions-item>
            <el-descriptions-item label="REVIEWER">Review skills, approve/reject versions</el-descriptions-item>
            <el-descriptions-item label="VIEWER">Read-only access to all resources</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="Integrations" name="integrations">
        <el-card>
          <h3>API Integrations</h3>
          <p style="color: #909399">Configure external integrations and API keys here.</p>
          <el-empty description="No integrations configured" />
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.admin {
  max-width: 1000px;
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
  margin-bottom: 28px;
}

.page-header h1 {
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
  line-height: 1.5;
}
</style>
