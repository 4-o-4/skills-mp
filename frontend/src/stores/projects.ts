import { defineStore } from 'pinia'
import { ref } from 'vue'
import { projectsApi } from '@/api'
import type { Project } from '@/types'
import { ElMessage } from 'element-plus'

export const useProjectsStore = defineStore('projects', () => {
  const projects = ref<Project[]>([])
  const currentProject = ref<Project | null>(null)
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await projectsApi.getAll()
      projects.value = data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to load projects')
    } finally {
      loading.value = false
    }
  }

  async function fetchMy() {
    loading.value = true
    try {
      const { data } = await projectsApi.getMy()
      projects.value = data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to load projects')
    } finally {
      loading.value = false
    }
  }

  async function create(payload: { name: string; description: string }) {
    try {
      const { data } = await projectsApi.create(payload)
      projects.value.unshift(data)
      ElMessage.success('Project created')
      return data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to create project')
      throw e
    }
  }

  async function fetchById(id: number) {
    loading.value = true
    try {
      const { data } = await projectsApi.getById(id)
      currentProject.value = data
      return data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to load project')
      throw e
    } finally {
      loading.value = false
    }
  }

  async function update(id: number, payload: { name: string; description: string }) {
    try {
      const { data } = await projectsApi.update(id, payload)
      currentProject.value = data
      const idx = projects.value.findIndex((p) => p.id === id)
      if (idx !== -1) projects.value[idx] = data
      ElMessage.success('Project updated')
      return data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to update project')
      throw e
    }
  }

  return { projects, currentProject, loading, fetchAll, fetchMy, create, fetchById, update }
})
