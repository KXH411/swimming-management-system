<template>
  <el-card class="course-card" shadow="hover" :body-style="{ padding: '0' }">
    <!-- 课程图片 -->
    <div class="course-image-container">
      <img
          :src="course.fengmian || '/images/course-default.jpg'"
          :alt="course.xiangmumingcheng"
          class="course-image"
          @error="handleImageError"
      >
      <div class="course-tag">{{ course.leixing || '游泳课程' }}</div>
      <!-- 收藏按钮 -->
      <div class="course-favorite" @click.stop="$emit('favorite', course)">
        <el-icon :color="isFavorited ? '#f56c6c' : '#ccc'" :size="20">
          <StarFilled v-if="isFavorited" />
          <Star v-else />
        </el-icon>
      </div>
    </div>

    <!-- 课程内容 -->
    <div class="course-content">
      <h3 class="course-title" :title="course.xiangmumingcheng">
        {{ course.xiangmumingcheng }}
      </h3>

      <p class="course-description">
        {{ course.kechengjianjie || '专业的游泳教学课程，由经验丰富的教练指导' }}
      </p>

      <!-- 课程元信息 -->
      <div class="course-meta">
        <div class="meta-item">
          <el-icon><User /></el-icon>
          <span>{{ course.jiaolianmingcheng || '专业教练' }}</span>
        </div>
        <div class="meta-item">
          <el-icon><Clock /></el-icon>
          <span>{{ course.shichang || 60 }}分钟</span>
        </div>
        <div class="meta-item" v-if="course.storeupnum > 0">
          <el-icon><Star /></el-icon>
          <span>{{ course.storeupnum }}人收藏</span>
        </div>
      </div>

      <!-- 价格和操作 -->
      <div class="course-footer">
        <div class="course-price">
          <span class="price">¥{{ course.jiage || 199 }}</span>
          <span class="original-price" v-if="course.originalPrice">
            ¥{{ course.originalPrice }}
          </span>
        </div>

        <div class="course-actions">
          <el-button
              type="primary"
              size="small"
              @click="$emit('view-detail', course.id)"
          >
            查看详情
          </el-button>
          <el-button
              size="small"
              @click="handleReservation"
              :loading="reserving"
          >
            {{ reserving ? '预约中...' : '立即预约' }}
          </el-button>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed } from 'vue'
import { User, Clock, Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  course: {
    type: Object,
    required: true,
    default: () => ({
      id: null,
      xiangmumingcheng: '',
      leixing: '',
      jiaolianmingcheng: '',
      jiaolianid: null,
      kechengjianjie: '',
      shichang: 0,
      jiage: 0,
      storeupnum: 0,
      fengmian: ''
    })
  },
  // 🔥 接收收藏状态
  isFavorited: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view-detail', 'reserve', 'favorite'])

const reserving = ref(false)

// 验证课程数据
const validatedCourse = computed(() => {
  const course = { ...props.course }

  // 确保必要字段有值
  if (!course.jiaolianid) {
    const coachIdMap = {
      '张教练': 1,
      '李教练': 2,
      '王教练': 3,
      '赵教练': 4,
      '刘教练': 5,
      '陈教练': 6
    }
    course.jiaolianid = coachIdMap[course.jiaolianmingcheng] || 1
  }

  // 确保其他字段有默认值
  course.shichang = course.shichang || 60
  course.jiage = course.jiage || 199
  course.storeupnum = course.storeupnum || 0

  return course
})

const handleImageError = (event) => {
  event.target.src = '/images/course-default.jpg'
}

// 预约处理
const handleReservation = async () => {
  try {
    reserving.value = true
    const courseToReserve = validatedCourse.value

    console.log('📋 准备预约课程数据:', courseToReserve)

    // 检查必要字段
    if (!courseToReserve.id) {
      ElMessage.error('课程ID缺失，无法预约')
      return
    }

    if (!courseToReserve.jiaolianid) {
      ElMessage.warning('教练信息不完整，使用默认教练')
    }

    // 触发预约事件，传递完整的课程数据
    emit('reserve', courseToReserve)

  } catch (error) {
    console.error('❌ 预约处理错误:', error)
    ElMessage.error('预约处理出错')
  } finally {
    reserving.value = false
  }
}
</script>

<style scoped>
.course-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.course-image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-image {
  transform: scale(1.05);
}

.course-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #409EFF;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.course-favorite {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 10;
}

.course-favorite:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
}

.course-content {
  padding: 16px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 44px;
}

.course-description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 42px;
}

.course-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #999;
  font-size: 13px;
}

.meta-item .el-icon {
  font-size: 14px;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-price {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.course-actions {
  display: flex;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .course-actions {
    justify-content: space-between;
  }

  .course-actions .el-button {
    flex: 1;
  }
}
</style>