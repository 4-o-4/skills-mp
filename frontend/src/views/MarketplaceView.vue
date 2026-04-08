<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { marketplaceApi } from '@/api'
import type { MarketplaceSkill } from '@/types'
import TagList from '@/components/TagList.vue'
import RatingStars from '@/components/RatingStars.vue'
import EmptyState from '@/components/EmptyState.vue'

const skills = ref<MarketplaceSkill[]>([])
const search = ref('')
const sortBy = ref('rating')
const loading = ref(false)
const selectedSkill = ref<MarketplaceSkill | null>(null)
const showDetail = ref(false)

const filtered = computed(() => {
  let list = [...skills.value]
  const q = search.value.toLowerCase()
  if (q) {
    list = list.filter(
      (s) => s.name.toLowerCase().includes(q) || s.description.toLowerCase().includes(q)
    )
  }
  if (sortBy.value === 'rating') {
    list.sort((a, b) => b.rating - a.rating)
  } else if (sortBy.value === 'date') {
    list.sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime())
  } else if (sortBy.value === 'downloads') {
    list.sort((a, b) => b.downloadCount - a.downloadCount)
  }
  return list
})

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await marketplaceApi.getSkills()
    skills.value = data
  } catch { /* interceptor */ } finally {
    loading.value = false
  }
})

function viewSkill(skill: MarketplaceSkill) {
  selectedSkill.value = skill
  showDetail.value = true
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric'
  })
}
</script>

<template>
  <div class="marketplace">
    <div class="page-header">
      <div>
        <p class="section-kicker">02 / Browse</p>
        <h1>Marketplace</h1>
        <p class="subtitle">Discover and reuse published skills from the community</p>
      </div>
    </div>

    <div class="filter-bar">
      <el-input
        v-model="search"
        placeholder="Search skills..."
        prefix-icon="Search"
        size="large"
        clearable
        style="max-width: 400px"
      />
      <el-select v-model="sortBy" size="large" style="width: 180px">
        <el-option label="Sort by Rating" value="rating" />
        <el-option label="Sort by Date" value="date" />
        <el-option label="Sort by Downloads" value="downloads" />
      </el-select>
    </div>

    <div v-loading="loading">
      <div v-if="filtered.length" class="skill-grid">
        <el-card
          v-for="skill in filtered"
          :key="skill.id"
          shadow="hover"
          class="skill-card"
          @click="viewSkill(skill)"
        >
          <h3>{{ skill.name }}</h3>
          <p class="card-desc">{{ skill.description }}</p>
          <div class="card-rating">
            <RatingStars :model-value="skill.rating" readonly />
            <span class="download-count">
              <el-icon><Download /></el-icon>
              {{ skill.downloadCount }}
            </span>
          </div>
          <div class="card-meta">
            <span>
              <el-icon><User /></el-icon>
              {{ skill.author?.username }}
            </span>
            <el-tag size="small" type="info">v{{ skill.currentVersion }}</el-tag>
          </div>
          <TagList v-if="skill.tags.length" :tags="skill.tags" />
        </el-card>
      </div>

      <EmptyState
        v-else-if="!loading"
        title="No skills found"
        description="Try a different search or check back later."
        icon="Search"
      />
    </div>

    <el-dialog v-model="showDetail" :title="selectedSkill?.name" width="600px">
      <template v-if="selectedSkill">
        <p>{{ selectedSkill.description }}</p>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Author">{{ selectedSkill.author?.username }}</el-descriptions-item>
          <el-descriptions-item label="Version">{{ selectedSkill.currentVersion }}</el-descriptions-item>
          <el-descriptions-item label="Rating">
            <RatingStars :model-value="selectedSkill.rating" readonly />
          </el-descriptions-item>
          <el-descriptions-item label="Downloads">{{ selectedSkill.downloadCount }}</el-descriptions-item>
          <el-descriptions-item label="Published">{{ formatDate(selectedSkill.publishedAt) }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="selectedSkill.tags.length" style="margin-top: 16px">
          <TagList :tags="selectedSkill.tags" />
        </div>
      </template>
      <template #footer>
        <el-button @click="showDetail = false">Close</el-button>
        <el-button type="primary">
          <el-icon><Download /></el-icon> Use this Skill
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.marketplace {
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
  max-width: 42ch;
  line-height: 1.5;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 28px;
  align-items: center;
}

.filter-bar :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border-subtle) inset !important;
}

.skill-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(288px, 1fr));
  gap: 18px;
}

.skill-card {
  cursor: pointer;
  border-radius: var(--radius-lg) !important;
  transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.skill-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card-hover) !important;
}

.skill-card h3 {
  margin: 0 0 10px;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: -0.02em;
  color: var(--text-primary);
}

.card-desc {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.55;
  margin: 0 0 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-rating {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.download-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-muted);
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 10px;
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
