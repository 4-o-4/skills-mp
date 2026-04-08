<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import type { FormInstance, FormRules } from 'element-plus'

const auth = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  role: 'EDITOR' as string
})

const rules: FormRules = {
  username: [
    { required: true, message: 'Username is required', trigger: 'blur' },
    { min: 3, message: 'At least 3 characters', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'Email is required', trigger: 'blur' },
    { type: 'email', message: 'Invalid email', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' },
    { min: 6, message: 'At least 6 characters', trigger: 'blur' }
  ],
  role: [{ required: true, message: 'Role is required', trigger: 'change' }]
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await auth.register(form)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <h1>Create Account</h1>
        <p>Join Skills MP</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="Username" prop="username">
          <el-input v-model="form.username" prefix-icon="User" placeholder="Choose a username" size="large" />
        </el-form-item>

        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" prefix-icon="Message" placeholder="Enter your email" size="large" />
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            prefix-icon="Lock"
            placeholder="Create a password"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item label="Role" prop="role">
          <el-select v-model="form.role" size="large" style="width: 100%">
            <el-option label="Editor" value="EDITOR" />
            <el-option label="Reviewer" value="REVIEWER" />
            <el-option label="Viewer" value="VIEWER" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-btn"
            native-type="submit"
          >
            Create Account
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        Already have an account?
        <router-link to="/login">Sign in</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(ellipse 90% 70% at 50% -15%, rgba(234, 88, 12, 0.35), transparent 52%),
    linear-gradient(165deg, #0a0a0a 0%, #171717 45%, #0f0f0f 100%);
}

.register-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.97);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-subtle);
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.45);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-header h1 {
  margin: 0 0 4px;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.register-header p {
  color: var(--text-secondary);
  margin: 0;
  font-size: 15px;
}

.register-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
  color: var(--text-muted);
  font-size: 14px;
}

.register-footer a {
  color: var(--accent);
  text-decoration: none;
  font-weight: 600;
}

.register-footer a:hover {
  color: var(--el-color-primary-dark-2);
}
</style>
