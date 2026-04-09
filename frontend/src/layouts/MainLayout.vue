<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

onMounted(() => {
  auth.fetchMe()
})

function handleMenuSelect(index: string) {
  router.push(index)
}

function logout() {
  auth.logout()
}

const roleColors: Record<string, string> = {
  ADMIN: '#f59e0b',
  EDITOR: '#ea580c',
  REVIEWER: '#16a34a',
  VIEWER: '#737373'
}
</script>

<template>
  <el-container class="layout-container">
    <el-aside width="232px" class="sidebar layout-sidebar">
      <div class="logo">
        <span class="logo-kicker">Skills directory</span>
        <span class="logo-text">Skills MP</span>
      </div>
      <el-menu
        :default-active="route.path"
        :router="false"
        class="sidebar-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="/marketplace">
          <el-icon><Shop /></el-icon>
          <span>Marketplace</span>
        </el-menu-item>
        <el-menu-item v-if="auth.isAdmin" index="/admin">
          <el-icon><Setting /></el-icon>
          <span>Admin</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <el-breadcrumb class="header-breadcrumb" separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
          <el-breadcrumb-item v-if="route.name && route.name !== 'Dashboard'">
            {{ route.name }}
          </el-breadcrumb-item>
        </el-breadcrumb>

        <div class="header-right">
          <template v-if="auth.user">
            <el-tag
              :color="roleColors[auth.user.role]"
              effect="dark"
              size="small"
              round
              class="role-badge"
            >
              {{ auth.user.role }}
            </el-tag>
            <span class="username">{{ auth.user.username }}</span>
          </template>
          <el-button text @click="logout">
            <el-icon><SwitchButton /></el-icon>
            Logout
          </el-button>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
  background: var(--app-bg);
}

.sidebar {
  background: var(--sidebar-bg);
  border-right: 1px solid var(--sidebar-border);
  overflow-y: auto;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  padding: 22px 20px 18px;
  border-bottom: 1px solid var(--sidebar-border);
}

.logo-kicker {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #737373;
}

.logo-text {
  font-size: 21px;
  font-weight: 700;
  color: #fafafa;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.sidebar-menu {
  border-right: none;
  padding: 12px 0 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border-bottom: 1px solid var(--border-subtle);
  padding: 0 28px;
  height: 58px;
}

.header-breadcrumb :deep(.el-breadcrumb__inner) {
  font-weight: 500;
  color: var(--text-secondary);
}

.header-breadcrumb :deep(.el-breadcrumb__inner.is-link:hover) {
  color: var(--accent);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.role-badge {
  border: none;
  font-weight: 600;
}

.main-content {
  background: transparent;
  overflow-y: auto;
  padding: 28px 32px 40px;
}
</style>
