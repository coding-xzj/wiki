<template>
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
        <a-input-password v-model:value="loginUser.password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import store from "@/store";
import { hexMd5, KEY } from "@/util/md5";
import { useRouter } from "vue-router";

const router = useRouter();

// 用来登录
const loginUser = ref({
  loginName: "",
  password: ""
});
const loginModalVisible = ref(true);
const loginModalLoading = ref(false);

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
    router.push("/");
  } else {
    message.error(data.message);
  }
};
</script>

<style scoped>
</style>