<template>
  <el-dialog
    v-model="dialogVisible"
    title="新建预约"
    width="600px"
    :before-close="handleClose"
  >
    <el-form
      ref="reservationForm"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="选择课程" prop="kechengid">
        <el-select
          v-model="form.kechengid"
          placeholder="请选择课程"
          style="width: 100%"
          @change="handleCourseChange"
          :disabled="!!preselectedCourse"
        >
          <el-option
            v-for="course in availableCourses"
            :key="course.id"
            :label="course.xiangmumingcheng"
            :value="course.id"
          >
            <div class="course-option">
              <span>{{ course.xiangmumingcheng }}</span>
              <span class="course-price">¥{{ course.jiage }}</span>
            </div>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="课程信息" v-if="selectedCourseData">
        <div class="course-info">
          <div class="info-row">
            <span class="label">类型：</span>
            <span>{{ selectedCourseData.leixing }}</span>
          </div>
          <div class="info-row">
            <span class="label">教练：</span>
            <span>{{ selectedCourseData.jiaolianmingcheng }}</span>
          </div>
          <div class="info-row">
            <span class="label">时长：</span>
            <span>{{ selectedCourseData.shichang }} 分钟</span>
          </div>
          <div class="info-row">
            <span class="label">价格：</span>
            <span class="price">¥{{ selectedCourseData.jiage }}</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="预约日期" prop="reservationDate">
        <div class="date-time-picker">
          <el-date-picker
            v-model="form.reservationDate"
            type="date"
            placeholder="选择日期"
            style="width: 45%"
            :disabled-date="disabledDate"
            :shortcuts="dateShortcuts"
            @change="handleDateChange"
            value-format="YYYY-MM-DD"
          />
          <el-select
            v-model="form.timeSlot"
            placeholder="选择时间段"
            style="width: 45%"
            :disabled="!form.reservationDate"
            @change="handleTimeChange"
          >
            <el-option-group label="上午时段">
              <el-option
                v-for="slot in morningSlots"
                :key="slot.value"
                :label="slot.label"
                :value="slot.value"
              />
            </el-option-group>
            <el-option-group label="下午时段">
              <el-option
                v-for="slot in afternoonSlots"
                :key="slot.value"
                :label="slot.label"
                :value="slot.value"
              />
            </el-option-group>
            <el-option-group label="晚上时段">
              <el-option
                v-for="slot in eveningSlots"
                :key="slot.value"
                :label="slot.label"
                :value="slot.value"
              />
            </el-option-group>
          </el-select>
        </div>
      </el-form-item>

      <el-form-item label="联系电话" prop="lianxidianhua">
        <el-input
          v-model="form.lianxidianhua"
          placeholder="请输入联系电话"
          maxlength="11"
        />
      </el-form-item>

      <el-form-item label="备注" prop="beizhu">
        <el-input
          v-model="form.beizhu"
          type="textarea"
          :rows="2"
          placeholder="选填（如：特殊需求、时间偏好等）"
          maxlength="200"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">
        提交预约
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useCourseStore } from '@/store/course'
import { useReservationStore } from '@/store/reservation'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: Boolean,
  preselectedCourse: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const courseStore = useCourseStore()
const reservationStore = useReservationStore()

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const reservationForm = ref()
const loading = ref(false)
const selectedCourseData = ref(null)

// 时间段配置
const morningSlots = [
  { value: '09:00', label: '09:00 - 10:00' },
  { value: '10:00', label: '10:00 - 11:00' },
  { value: '11:00', label: '11:00 - 12:00' }
]

const afternoonSlots = [
  { value: '14:00', label: '14:00 - 15:00' },
  { value: '15:00', label: '15:00 - 16:00' },
  { value: '16:00', label: '16:00 - 17:00' },
  { value: '17:00', label: '17:00 - 18:00' }
]

const eveningSlots = [
  { value: '19:00', label: '19:00 - 20:00' },
  { value: '20:00', label: '20:00 - 21:00' }
]

// 表单数据
const form = ref({
  kechengid: '',
  reservationDate: '',
  timeSlot: '',
  lianxidianhua: '',
  beizhu: ''
})

// 日期快捷选项
const dateShortcuts = [
  {
    text: '明天',
    value: () => {
      const date = new Date()
      date.setDate(date.getDate() + 1)
      return date
    }
  },
  {
    text: '后天',
    value: () => {
      const date = new Date()
      date.setDate(date.getDate() + 2)
      return date
    }
  },
  {
    text: '本周末',
    value: () => {
      const date = new Date()
      const day = date.getDay()
      const diff = day === 0 ? 0 : 7 - day
      date.setDate(date.getDate() + diff)
      return date
    }
  },
  {
    text: '一周后',
    value: () => {
      const date = new Date()
      date.setDate(date.getDate() + 7)
      return date
    }
  }
]

// 验证规则
const rules = {
  kechengid: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  reservationDate: [
    { required: true, message: '请选择预约日期', trigger: 'change' }
  ],
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  lianxidianhua: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 计算属性
const availableCourses = computed(() => courseStore.courses || [])

// 禁用过去的日期
const disabledDate = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime()
}

// 课程选择变化
const handleCourseChange = (id) => {
  selectedCourseData.value = availableCourses.value.find(c => c.id === id)
}

// 日期变化时清空时段
const handleDateChange = () => {
  form.value.timeSlot = ''
}

// 🔥 时段变化时组合完整时间（解决时区问题）
const handleTimeChange = (slot) => {
  if (form.value.reservationDate && slot) {
    // 使用本地时间构建，避免时区转换
    const [hour, minute] = slot.split(':')
    // 格式：YYYY-MM-DD HH:MM:SS
    const localDateTime = `${form.value.reservationDate} ${hour}:${minute}:00`
    form.value.yuyueshijian = localDateTime
    console.log('📅 选择的本地时间:', localDateTime)
  }
}

// 提交预约
const handleSubmit = async () => {
  try {
    await reservationForm.value.validate()
    
    if (!form.value.reservationDate || !form.value.timeSlot) {
      ElMessage.warning('请完整选择预约时间')
      return
    }
    
    if (!selectedCourseData.value) {
      ElMessage.warning('请选择课程')
      return
    }
    
    loading.value = true
    
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    // 🔥 构建预约数据，使用本地时间字符串
    const reservationData = {
      kechengid: form.value.kechengid,
      xiangmumingcheng: selectedCourseData.value.xiangmumingcheng,
      leixing: selectedCourseData.value.leixing,
      jiaolianmingcheng: selectedCourseData.value.jiaolianmingcheng,
      jiaolianid: selectedCourseData.value.jiaolianid || 1,
      yuyueshijian: form.value.yuyueshijian,  // 使用本地时间字符串
      lianxidianhua: form.value.lianxidianhua,
      beizhu: form.value.beizhu,
      crossuserid: userInfo.id,
      yonghuzhanghao: userInfo.yonghuzhanghao,
      yonghuxingming: userInfo.yonghuxingming
    }
    
    console.log('📝 提交预约数据:', reservationData)
    console.log('📅 预约时间(本地):', reservationData.yuyueshijian)
    
    const result = await reservationStore.addReservation(reservationData)
    
    if (result.success) {
      const [hour, minute] = form.value.timeSlot.split(':')
      const endHour = parseInt(hour) + 1
      ElMessage.success(`预约成功！时间：${form.value.reservationDate} ${hour}:00 - ${endHour}:00`)
      emit('success')
      handleClose()
    }
  } catch (error) {
    if (error.errors) {
      ElMessage.error('请完善表单信息')
    } else {
      console.error('预约失败:', error)
      ElMessage.error(error.message || '预约失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  form.value = {
    kechengid: '',
    reservationDate: '',
    timeSlot: '',
    lianxidianhua: '',
    beizhu: ''
  }
  selectedCourseData.value = null
  reservationForm.value?.clearValidate()
}

// 监听对话框打开
watch(dialogVisible, (visible) => {
  if (visible) {
    if (courseStore.courses.length === 0) {
      courseStore.fetchCourses()
    }
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.lianxidianhua) {
      form.value.lianxidianhua = userInfo.lianxidianhua
    }
    
    // 🔥 如果有预选的课程，自动选中并禁用选择框
    if (props.preselectedCourse) {
      console.log('📚 预选课程:', props.preselectedCourse)
      form.value.kechengid = props.preselectedCourse.id
      selectedCourseData.value = props.preselectedCourse
    }
  }
})
</script>

<style scoped>
.course-option {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.course-price {
  color: #f56c6c;
  font-weight: bold;
}

.course-info {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 8px;
  width: 100%;
}

.info-row {
  margin-bottom: 6px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  color: #666;
  width: 50px;
  display: inline-block;
}

.info-row .price {
  color: #f56c6c;
  font-weight: bold;
}

.date-time-picker {
  display: flex;
  gap: 12px;
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>