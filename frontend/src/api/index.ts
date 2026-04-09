import client from './client'
import type {
  AuthResponse, User, Project, Skill, SkillDetail,
  Version, DiffResult, Variant, TestSet, TestCase, TestRun, TestRunDetail,
  Review, Environment, Deployment, Release, GitConfig, GitStatus,
  MarketplaceSkill, CompareResult
} from '@/types'

export const authApi = {
  login(username: string, password: string) {
    return client.post<AuthResponse>('/api/auth/login', { username, password })
  },
  register(data: { username: string; email: string; password: string; role: string }) {
    return client.post<AuthResponse>('/api/auth/register', data)
  },
  getMe() {
    return client.get<User>('/api/auth/me')
  }
}

export const projectsApi = {
  getAll() {
    return client.get<Project[]>('/api/projects')
  },
  getMy() {
    return client.get<Project[]>('/api/projects/my')
  },
  create(data: { name: string; description: string }) {
    return client.post<Project>('/api/projects', data)
  },
  getById(id: number) {
    return client.get<Project>(`/api/projects/${id}`)
  },
  update(id: number, data: { name: string; description: string }) {
    return client.put<Project>(`/api/projects/${id}`, data)
  }
}

export const skillsApi = {
  getByProject(projectId: number) {
    return client.get<Skill[]>(`/api/projects/${projectId}/skills`)
  },
  create(projectId: number, data: Partial<Skill>) {
    return client.post<Skill>(`/api/projects/${projectId}/skills`, data)
  },
  getById(id: number) {
    return client.get<SkillDetail>(`/api/skills/${id}`)
  },
  update(id: number, data: Partial<SkillDetail>) {
    return client.put<SkillDetail>(`/api/skills/${id}`, data)
  },
  delete(id: number) {
    return client.delete(`/api/skills/${id}`)
  },
  updateStatus(id: number, status: string) {
    return client.patch<Skill>(`/api/skills/${id}/status`, { status })
  }
}

export const versionsApi = {
  getBySkill(skillId: number) {
    return client.get<Version[]>(`/api/skills/${skillId}/versions`)
  },
  create(skillId: number, data: { content: string; changelog: string }) {
    return client.post<Version>(`/api/skills/${skillId}/versions`, data)
  },
  getById(versionId: number) {
    return client.get<Version>(`/api/versions/${versionId}`)
  },
  getDiff(vid1: number, vid2: number) {
    return client.get<DiffResult>(`/api/versions/${vid1}/diff/${vid2}`)
  }
}

export const variantsApi = {
  getBySkill(skillId: number) {
    return client.get<Variant[]>(`/api/skills/${skillId}/variants`)
  },
  create(skillId: number, data: Partial<Variant>) {
    return client.post<Variant>(`/api/skills/${skillId}/variants`, data)
  },
  delete(id: number) {
    return client.delete(`/api/variants/${id}`)
  },
  compare(data: { variantIds: number[]; testSetId: number }) {
    return client.post<CompareResult>('/api/variants/compare', data)
  }
}

export const testSetsApi = {
  getByProject(projectId: number) {
    return client.get<TestSet[]>('/api/test-sets', { params: { projectId } })
  },
  create(projectId: number, data: { name: string; description: string }) {
    return client.post<TestSet>(`/api/test-sets`, { ...data, projectId })
  },
  addCase(testSetId: number, data: Partial<TestCase>) {
    return client.post<TestCase>(`/api/test-sets/${testSetId}/cases`, data)
  }
}

export const testRunsApi = {
  getAll(query?: { skillId?: number; testSetId?: number; status?: string }) {
    return client.get<TestRun[]>('/api/test-runs', { params: query })
  },
  getById(id: number) {
    return client.get<TestRunDetail>(`/api/test-runs/${id}`)
  }
}

export const reviewsApi = {
  getByVersion(versionId: number) {
    return client.get<Review[]>(`/api/versions/${versionId}/reviews`)
  },
  create(versionId: number, data: { rating?: number; comment: string; status: string }) {
    return client.post<Review>(`/api/versions/${versionId}/reviews`, data)
  }
}

export const environmentsApi = {
  getAll(projectId: number) {
    return client.get<Environment[]>('/api/environments', { params: { projectId } })
  },
  create(projectId: number, data: { name: string; description: string }) {
    return client.post<Environment>('/api/environments', { ...data, projectId })
  },
  deploy(envId: number, data: { versionId: number }) {
    return client.post<Deployment>(`/api/environments/${envId}/deploy`, data)
  }
}

export const releasesApi = {
  getByProject(projectId: number) {
    return client.get<Release[]>(`/api/projects/${projectId}/releases`)
  },
  create(projectId: number, data: { versionId: number; environmentId: number; notes: string }) {
    return client.post<Release>(`/api/projects/${projectId}/releases`, data)
  }
}

export const gitApi = {
  connect(projectId: number, data: { repoUrl: string; branch: string }) {
    return client.post<GitConfig>(`/api/git/connect/${projectId}`, data)
  },
  getStatus(projectId: number) {
    return client.get<GitStatus>(`/api/git/status/${projectId}`)
  },
  sync(versionId: number) {
    return client.post(`/api/git/sync/${versionId}`)
  }
}

export const adminApi = {
  getUsers() {
    return client.get<User[]>('/api/admin/users')
  }
}

export const marketplaceApi = {
  getSkills(params?: { search?: string; tag?: string; sort?: string }) {
    return client.get<MarketplaceSkill[]>('/api/marketplace/skills', { params })
  }
}
