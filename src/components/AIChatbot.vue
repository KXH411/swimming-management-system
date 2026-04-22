<template>
  <div class="ai-chatbot">
    <!-- 悬浮按钮 -->
    <transition name="fade-scale">
      <div class="chatbot-button" @click="toggleChat" v-if="!isOpen">
        <el-icon :size="28"><ChatDotRound /></el-icon>
      </div>
    </transition>

    <!-- 聊天窗口 -->
    <transition name="slide-up">
      <div v-show="isOpen" class="chatbot-window">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <div class="avatar">
              <el-icon color="#409EFF"><Service /></el-icon>
            </div>
            <div class="header-info">
              <span class="title">AI智能助手</span>
              <span class="status" :class="{ online: apiStatus, offline: !apiStatus }">
                {{ apiStatus ? '在线' : '离线' }}
              </span>
            </div>
          </div>
          <div class="header-right">
            <el-icon class="clear-btn" @click="clearConversation" title="清空对话">
              <Delete />
            </el-icon>
            <el-icon class="close-btn" @click="toggleChat" title="关闭">
              <Close />
            </el-icon>
          </div>
        </div>

        <!-- 消息列表 - 可滚动区域 -->
        <div class="chat-messages" ref="messagesContainerRef">
          <div v-for="(msg, index) in messages" :key="index" class="message-item" :class="msg.role">
            <div class="message-avatar">
              <el-avatar :size="36" :src="msg.role === 'user' ? userAvatar : botAvatar">
                {{ msg.role === 'user' ? '我' : '泳' }}
              </el-avatar>
            </div>
            <div class="message-bubble">
              <div class="message-text">{{ msg.content }}</div>
              <div class="message-time">{{ msg.time }}</div>
            </div>
          </div>
          
          <!-- 加载状态 -->
          <div v-if="loading" class="message-item bot">
            <div class="message-avatar">
              <el-avatar :size="36" :src="botAvatar">泳</el-avatar>
            </div>
            <div class="message-bubble">
              <div class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
          
          <!-- 滚动到底部的锚点 -->
          <div ref="bottomAnchor"></div>
        </div>

        <!-- 快捷问题 -->
        <div class="quick-questions">
          <el-tag 
            v-for="q in quickQuestions" 
            :key="q" 
            size="small" 
            @click="sendQuickQuestion(q)"
          >
            {{ q }}
          </el-tag>
        </div>

        <!-- 输入框 -->
        <div class="chat-input">
          <el-input
            ref="inputRef"
            v-model="inputMessage"
            placeholder="输入消息..."
            @keyup.enter="sendMessage"
            :disabled="loading"
            size="large"
          >
            <template #append>
              <el-button @click="sendMessage" :loading="loading" type="primary">
                发送
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, watch, onMounted } from 'vue'
import { ChatDotRound, Service, Close, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

const isOpen = ref(false)
const loading = ref(false)
const inputMessage = ref('')
const messagesContainerRef = ref(null)
const bottomAnchor = ref(null)
const inputRef = ref(null)
const apiStatus = ref(true)

const userAvatar = ref('')
const botAvatar = ref('')

// 快捷问题
const quickQuestions = ref([
  '有哪些游泳课程？',
  '教练有哪些？',
  '查看我的预约',
  '营业时间',
  '如何预约？'
])

// 消息列表
const messages = ref([
  {
    role: 'bot',
    content: '你好！我是游泳馆AI助手小泳🤖\n\n可以帮你查询课程、教练、预约等信息。请问有什么可以帮你的？',
    time: formatTime(new Date())
  }
])

// 格式化时间
function formatTime(date) {
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    if (bottomAnchor.value) {
      bottomAnchor.value.scrollIntoView({ behavior: 'smooth', block: 'end' })
    }
  })
}

// 聚焦输入框
function focusInput() {
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.focus()
    }
  })
}

// 切换聊天窗口
function toggleChat() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    setTimeout(() => {
      scrollToBottom()
      focusInput()
    }, 200)
  }
}

// 清空对话
function clearConversation() {
  messages.value = [
    {
      role: 'bot',
      content: '对话已清空。你好！我是游泳馆AI助手小泳🤖，有什么可以帮你的？',
      time: formatTime(new Date())
    }
  ]
  scrollToBottom()
  focusInput()
}

// 发送快捷问题
function sendQuickQuestion(question) {
  inputMessage.value = question
  sendMessage()
}

// 发送消息
async function sendMessage() {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = inputMessage.value.trim()
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: userMessage,
    time: formatTime(new Date())
  })
  
  inputMessage.value = ''
  scrollToBottom()
  
  loading.value = true
  
  try {
    const response = await axios.post('http://localhost:8080/api/ai/chat', {
      message: userMessage,
      userId: userId
    })
    
    if (response.data && response.data.success) {
      messages.value.push({
        role: 'bot',
        content: response.data.reply,
        time: formatTime(new Date())
      })
      apiStatus.value = true
    } else {
      throw new Error('AI响应失败')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    apiStatus.value = false
    messages.value.push({
      role: 'bot',
      content: '抱歉，我遇到了一些问题。请稍后再试。',
      time: formatTime(new Date())
    })
  } finally {
    loading.value = false
    scrollToBottom()
    focusInput()
  }
}

// 监听消息变化，自动滚动
watch(messages, () => {
  scrollToBottom()
}, { deep: true })

// 获取用户头像
onMounted(() => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    userAvatar.value = userInfo.touxiang || ''
  } catch (e) {}
})
</script>

<style scoped>
.ai-chatbot {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 9999;
}

/* 悬浮按钮动画 */
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all 0.3s ease;
}
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0);
}

.chatbot-button {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.chatbot-button:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.chatbot-button .el-icon {
  color: white;
}

/* 聊天窗口动画 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}
.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.chatbot-window {
  position: fixed;
  bottom: 100px;
  right: 30px;
  width: 400px;
  height: 580px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 头部 */
.chat-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left .avatar {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-left .avatar .el-icon {
  color: white;
  font-size: 20px;
}

.header-info {
  display: flex;
  flex-direction: column;
}

.header-info .title {
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.header-info .status {
  font-size: 11px;
  margin-top: 2px;
}

.header-info .status.online {
  color: #a0ffa0;
}

.header-info .status.offline {
  color: #ffa0a0;
}

.header-right {
  display: flex;
  gap: 16px;
}

.header-right .el-icon {
  color: white;
  font-size: 20px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.header-right .el-icon:hover {
  opacity: 0.8;
}

/* 消息列表 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

/* 自定义滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #e9ecef;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  animation: fadeInUp 0.3s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 18px;
  position: relative;
}

.message-item.user .message-bubble {
  background: #409EFF;
  color: white;
  border-radius: 18px 4px 18px 18px;
}

.message-item.bot .message-bubble {
  background: white;
  color: #333;
  border-radius: 4px 18px 18px 18px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-time {
  font-size: 10px;
  margin-top: 6px;
  opacity: 0.6;
}

.message-item.user .message-time {
  text-align: right;
  color: rgba(255, 255, 255, 0.8);
}

.message-item.bot .message-time {
  text-align: left;
  color: #999;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: 0s; }
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-6px);
    opacity: 1;
  }
}

/* 快捷问题 */
.quick-questions {
  padding: 12px 16px;
  border-top: 1px solid #e9ecef;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  background: white;
}

.quick-questions .el-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.quick-questions .el-tag:hover {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
}

/* 输入框 */
.chat-input {
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #e9ecef;
}

/* 响应式 */
@media (max-width: 768px) {
  .chatbot-window {
    width: calc(100vw - 40px);
    height: 500px;
    right: 20px;
    bottom: 80px;
  }
  
  .chatbot-button {
    bottom: 20px;
    right: 20px;
  }
}
</style>