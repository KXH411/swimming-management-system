<template>
  <div id="app">
    <Layout v-if="showLayout">
      <router-view />
    </Layout>
    <router-view v-else />
    
    <!-- AI助手 -->
    <AIChatbot />
  </div>
</template>

<script>
import Layout from '@/components/Layout.vue'
import AIChatbot from '@/components/AIChatbot.vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'App',
  components: {
    Layout,
    AIChatbot
  },
  setup() {
    const route = useRoute()
    const showLayout = computed(() => !route.meta.hideLayout)
    return { showLayout }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  min-height: 100vh;
}
</style>