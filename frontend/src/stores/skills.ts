import { defineStore } from 'pinia'
import { ref } from 'vue'
import { skillsApi } from '@/api'
import type { Skill, SkillDetail } from '@/types'
import { ElMessage } from 'element-plus'

export const useSkillsStore = defineStore('skills', () => {
  const skills = ref<Skill[]>([])
  const currentSkill = ref<SkillDetail | null>(null)
  const loading = ref(false)

  async function fetchByProject(projectId: number) {
    loading.value = true
    try {
      const { data } = await skillsApi.getByProject(projectId)
      skills.value = data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to load skills')
    } finally {
      loading.value = false
    }
  }

  async function create(projectId: number, payload: Partial<Skill>) {
    try {
      const { data } = await skillsApi.create(projectId, payload)
      skills.value.unshift(data)
      ElMessage.success('Skill created')
      return data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to create skill')
      throw e
    }
  }

  async function fetchById(id: number) {
    loading.value = true
    try {
      const { data } = await skillsApi.getById(id)
      currentSkill.value = data
      return data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to load skill')
      throw e
    } finally {
      loading.value = false
    }
  }

  async function update(id: number, payload: Partial<SkillDetail>) {
    try {
      const { data } = await skillsApi.update(id, payload)
      currentSkill.value = data
      ElMessage.success('Skill saved')
      return data
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to save skill')
      throw e
    }
  }

  async function remove(id: number) {
    try {
      await skillsApi.delete(id)
      skills.value = skills.value.filter((s) => s.id !== id)
      ElMessage.success('Skill deleted')
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to delete skill')
      throw e
    }
  }

  async function updateStatus(id: number, status: string) {
    try {
      const { data } = await skillsApi.updateStatus(id, status)
      if (currentSkill.value?.id === id) {
        currentSkill.value.status = data.status
      }
      const idx = skills.value.findIndex((s) => s.id === id)
      if (idx !== -1) skills.value[idx].status = data.status
      ElMessage.success(`Status changed to ${status}`)
    } catch (e: any) {
      ElMessage.error(e.response?.data?.message || 'Failed to update status')
      throw e
    }
  }

  return { skills, currentSkill, loading, fetchByProject, create, fetchById, update, remove, updateStatus }
})
