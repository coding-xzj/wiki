<template>
  <a-layout-header class="header">
    <div class="logo">知识库</div>
    <a-menu theme="dark" mode="horizontal" :style="{ lineHeight: '64px' }">
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
                  <a class="login-menu"> 退出登录 </a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </a>
      <a class="login-menu" v-show="!user.id" @click="showLoginModal">
        <span>登录</span>
      </a>
    </a-menu>

    <a-modal
      title="登录"
      v-model:visible="loginModalVisible"
      :confirm-loading="loginModalLoading"
      ok-text="登录"
      cancel-text="取消"
      @ok="login"
      :maskClosable="false"
    >
      <a-form
        :model="loginUser"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 18 }"
      >
        <a-form-item label="登录名">
          <a-input v-model:value="loginUser.loginName" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input v-model:value="loginUser.password" type="password" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({});
</script>


<script lang="ts" setup>
import { ref, computed } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import store from "@/store";

declare let hexMd5: any;
declare let KEY: any;

// 登录后保存
const user = computed(() => store.state.user);

// 用来登录
const loginUser = ref({
  loginName: "",
  password: ""
});
const loginModalVisible = ref(false);
const loginModalLoading = ref(false);
const showLoginModal = () => {
  loginModalVisible.value = true;
};

// 登录
const login = async () => {
  loginModalLoading.value = true;
  loginUser.value.password = hexMd5(loginUser.value.password + KEY);
  const res = await axios.post("/user/login", loginUser.value);
  loginModalLoading.value = false;
  const data = res.data;
  console.log(res);

  if (data.success) {
    loginModalVisible.value = false;
    message.success("登录成功！");
    store.commit("setUser", data.content);
  } else {
    message.error(data.message);
  }
};

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
</style>
