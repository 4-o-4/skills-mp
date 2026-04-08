export type UserRole = 'ADMIN' | 'EDITOR' | 'REVIEWER' | 'VIEWER'
export type EntityStatus = 'DRAFT' | 'VERIFIED' | 'PUBLISHED' | 'ARCHIVED'
export type TestRunStatus = 'QUEUED' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED'
export type EvaluatorType = 'EXACT_MATCH' | 'JSON_VALIDATION' | 'REGEXP' | 'CODE' | 'LLM_AS_JUDGE'
export type ReviewStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export interface User {
  id: number
  username: string
  email: string
  role: UserRole
  createdAt: string
}

export interface AuthResponse {
  token: string
  user: User
}

export interface Project {
  id: number
  name: string
  description: string
  owner: User
  status: string
  skillCount: number
  updatedAt: string
  createdAt: string
}

export interface Skill {
  id: number
  name: string
  description: string
  status: EntityStatus
  owner: User
  tags: Tag[]
  projectId: number
  updatedAt: string
  createdAt: string
}

export interface SkillDetail extends Skill {
  promptBody: string
  metadata: string
  latestVersion: Version | null
  variantCount: number
  testRunCount: number
}

export interface Version {
  id: number
  skillId: number
  versionNumber: string
  content: string
  changelog: string
  status: EntityStatus
  author: User
  gitCommitSha: string | null
  createdAt: string
}

export interface DiffResult {
  versionFrom: Version
  versionTo: Version
  changes: string
}

export interface Variant {
  id: number
  skillId: number
  name: string
  modelProvider: string
  modelName: string
  systemPrompt: string
  temperature: number
  maxTokens: number
  config: string
  createdAt: string
}

export interface Tag {
  id: number
  name: string
  color: string
}

export interface TestSet {
  id: number
  name: string
  description: string
  projectId: number
  caseCount: number
  createdAt: string
}

export interface TestCase {
  id: number
  testSetId: number
  input: string
  expectedOutput: string
  metadata: string
}

export interface TestSetDetail extends TestSet {
  cases: TestCase[]
}

export interface TestRun {
  id: number
  testSetId: number
  testSetName: string
  versionId: number
  variantId: number | null
  status: TestRunStatus
  totalCases: number
  passedCases: number
  failedCases: number
  startedAt: string | null
  completedAt: string | null
  createdBy: User
  createdAt: string
}

export interface EvaluatorResult {
  id: number
  testRunId: number
  testCaseId: number
  evaluatorId: number
  evaluatorName: string
  rawOutput: string
  score: number
  passed: boolean
  explanation: string
}

export interface TestRunDetail extends TestRun {
  results: EvaluatorResult[]
}

export interface Evaluator {
  id: number
  name: string
  type: EvaluatorType
  configuration: string
  projectId: number
  createdAt: string
}

export interface Review {
  id: number
  versionId: number
  author: User
  rating: number | null
  comment: string
  status: ReviewStatus
  createdAt: string
}

export interface Environment {
  id: number
  name: string
  description: string
  projectId: number
  activeVersionId: number | null
  activeVersionNumber: string | null
  createdAt: string
}

export interface Deployment {
  id: number
  environmentId: number
  versionId: number
  versionNumber: string
  deployedBy: User
  deployedAt: string
}

export interface Release {
  id: number
  projectId: number
  versionId: number
  versionNumber: string
  environmentId: number
  environmentName: string
  gitTag: string | null
  notes: string
  createdBy: User
  createdAt: string
}

export interface GitConfig {
  id: number
  projectId: number
  repoUrl: string
  branch: string
  connected: boolean
}

export interface GitStatus {
  connected: boolean
  lastSync: string | null
  repoUrl: string | null
  branch: string | null
}

export interface MarketplaceSkill {
  id: number
  name: string
  description: string
  author: User
  tags: Tag[]
  currentVersion: string
  rating: number
  downloadCount: number
  publishedAt: string
}

export interface CompareResult {
  variants: Variant[]
  results: {
    variantId: number
    testCaseId: number
    output: string
    latency: number
    score: number
    passed: boolean
  }[]
}
