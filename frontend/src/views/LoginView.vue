<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import type { FormInstance, FormRules } from 'element-plus'

const auth = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules: FormRules = {
  username: [{ required: true, message: 'Username is required', trigger: 'blur' }],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { min: 3, message: 'At least 3 characters', trigger: 'blur' }
  ]
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.login(form.username, form.password)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h1>Skills MP</h1>
        <p>Sign in to your account</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="Username" prop="username">
          <el-input
            v-model="form.username"
            prefix-icon="User"
            placeholder="Enter username"
            size="large"
          />
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            prefix-icon="Lock"
            placeholder="Enter password"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            native-type="submit"
          >
            Sign In
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        Don't have an account?
        <router-link to="/register">Create one</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(ellipse 90% 70% at 50% -15%, rgba(234, 88, 12, 0.35), transparent 52%),
    linear-gradient(165deg, #0a0a0a 0%, #171717 45%, #0f0f0f 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.97);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-subtle);
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.45);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h1 {
  margin: 0 0 4px;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.login-header p {
  color: var(--text-secondary);
  margin: 0;
  font-size: 15px;
}

.login-btn {
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  color: var(--text-muted);
  font-size: 14px;
}

.login-footer a {
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
}

.login-footer a:hover {
  color: var(--el-color-primary-dark-2);
}
</style>
