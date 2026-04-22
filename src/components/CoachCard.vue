<template>
  <el-card class="coach-card" shadow="hover">
    <!-- 教练头像和信息 -->
    <div class="coach-header">
      <div class="coach-avatar">
        <el-avatar
            :size="80"
            :src="coach.zhaopian || '/images/coach-default.jpg'"
            @error="handleAvatarError"
        >
          {{ coach.jiaolianxingming.charAt(0) }}
        </el-avatar>
      </div>
      <div class="coach-basic-info">
        <h3 class="coach-name">{{ coach.jiaolianxingming }}</h3>
        <div class="coach-gender">
          <el-tag
              :type="coach.xingbie === '男' ? 'primary' : 'danger'"
              size="small"
          >
            <el-icon>
              <User />
            </el-icon>
            {{ coach.xingbie }}
          </el-tag>
        </div>
        <div class="coach-specialty">
          <el-tag type="success" size="small">
            {{ coach.zhuanye || '游泳教练' }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 教练简介 -->
    <div class="coach-intro">
      <p class="intro-text">
        {{ coach.jianjie || '专业游泳教练，拥有丰富的教学经验' }}
      </p>
    </div>

    <!-- 教练联系方式 -->
    <div class="coach-contact" v-if="coach.lianxidianhua">
      <div class="contact-item">
        <el-icon><Phone /></el-icon>
        <span>{{ coach.lianxidianhua }}</span>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="coach-actions">
      <el-button
          type="primary"
          size="small"
          @click="$emit('view-detail', coach.id)"
          class="action-btn"
      >
        <el-icon><View /></el-icon>
        查看详情
      </el-button>
      <el-button
          type="success"
          size="small"
          @click="$emit('contact', coach)"
          class="action-btn"
      >
        <el-icon><ChatDotRound /></el-icon>
        联系教练
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { User, Phone, View, ChatDotRound } from '@element-plus/icons-vue'

const props = defineProps({
  coach: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['view-detail', 'contact'])

const handleAvatarError = () => {
  // 头像加载失败时的处理
  console.log('头像加载失败')
}
</script>

<style scoped>
.coach-card {
  margin-bottom: 24px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  height: 100%;
}

.coach-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.coach-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.coach-avatar {
  margin-right: 16px;
}

.coach-avatar .el-avatar {
  border: 3px solid #e8f4ff;
}

.coach-basic-info {
  flex: 1;
}

.coach-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  margin-top: 0;
}

.coach-gender,
.coach-specialty {
  margin-bottom: 6px;
}

.coach-intro {
  margin-bottom: 16px;
}

.intro-text {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 63px;
}

.coach-contact {
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.contact-item .el-icon {
  color: #409EFF;
}

.coach-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .coach-header {
    flex-direction: column;
    text-align: center;
  }

  .coach-avatar {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .coach-actions {
    flex-direction: column;
  }
}
</style>