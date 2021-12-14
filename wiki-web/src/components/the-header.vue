<template>
  <a-layout-header class="header">
    <a-menu
      mode="horizontal"
      theme="dark"
      :style="{ padding: '0 50px', lineHeight: '64px' }"
    >
      <div class="logo">知识库</div>
      <a-menu-item key="/">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item
        key="/admin/user"
        :style="user.id ? {} : { display: 'none' }"
      >
        <router-link to="/admin/user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item
        key="/admin/ebook"
        :style="user.id ? {} : { display: 'none' }"
      >
        <router-link to="/admin/ebook">电子书管理</router-link>
      </a-menu-item>
      <a-menu-item
        key="/admin/category"
        :style="user.id ? {} : { display: 'none' }"
      >
        <router-link to="/admin/category">分类管理</router-link>
      </a-menu-item>
      <a class="login-menu" v-show="user.id">
        <a-dropdown>
          <span>{{ user.name }}</span>
          <template #overlay>
            <a-menu>
              <a-menu-item>
                <a-popconfirm
                  title="确认退出登录?"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="logout()"
                >
                  <div class="logout">退出登录</div>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </a>
    </a-menu>
  </a-layout-header>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({});
</script>


<script lang="ts" setup>
import { computed } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import store from "@/store";

// 登录后保存
const user = computed(() => store.state.user);

// 退出登录
const logout = async () => {
  const res = await axios.get("/user/logout/" + user.value.token);
  const data = res.data;
  if (data.success) {
    message.success("退出登录成功！");
    store.commit("setUser", {});
  } else {
    message.error(data.message);
  }
};
</script>

<style>
.header {
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1;
}
.logo {
  width: 120px;
  height: 31px;
  float: left;
  color: white;
  font-size: 18px;
}
.login-menu {
  float: right;
  color: white;
  padding-left: 10px;
}
.logout {
  line-height: 0;
  padding: 10px 0;
}
</style>
