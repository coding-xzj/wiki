<template>
  <a-layout class="box">
    <a-layout-content class="border">
      <a-row type="flex" justify="end">
        <a-form layout="inline" :model="queryName" style="margin-bottom: 20px">
          <a-form-item>
            <a-input
              v-model:value="queryName.loginName"
              placeholder="登陆名"
            ></a-input>
          </a-form-item>
          <a-form-item>
            <a-button
              type="primary"
              @click="handleQuery({ page: 1, size: pagination.pageSize })"
              >查询</a-button
            >
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">新增</a-button>
          </a-form-item>
        </a-form>
      </a-row>
      <a-table
        :columns="columns"
        :row-key="(record) => record.id"
        :data-source="users"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        style="min-height: 395px"
      >
        <template v-slot:action="{ record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">编辑</a-button>
            <a-popconfirm
              title="删除后不可恢复，确认删除?"
              ok-text="是"
              cancel-text="否"
              @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
    title="用户表单"
    v-model:visible="modalVisible"
    :confirm-loading="modalLoading"
    ok-text="确定"
    cancel-text="取消"
    @ok="handleModalOk"
  >
    <a-form
      ref="formRef"
      :rules="rules"
      :model="user"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 18 }"
    >
      <a-form-item name="loginName" label="登陆名">
        <a-input v-model:value="user.loginName" />
      </a-form-item>
      <a-form-item name="name" label="昵称">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item name="password" label="密码">
        <a-input-password v-model:value="user.password" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import { Tool } from "@/util/tool";
import { hexMd5, KEY } from "@/util/md5";

const queryName = ref();
queryName.value = {};

const users = ref();
const pagination = ref({
  current: 1,
  pageSize: 5,
  total: 0
});
const loading = ref(false);

const columns = [
  {
    title: "登陆名",
    dataIndex: "loginName",
    width: "20%"
  },
  {
    title: "名称",
    dataIndex: "name",
    width: "20%"
  },
  {
    title: "密码",
    dataIndex: "password",
    width: "30%"
  },
  {
    title: "操作",
    key: "action",
    width: "30%",
    slots: { customRender: "action" }
  }
];

/**
 * 数据查询
 **/
const handleQuery = async (queryInfo: any) => {
  loading.value = true;
  users.value = [];
  const res = await axios.get("/user/list", {
    params: {
      page: queryInfo.page,
      size: queryInfo.size,
      loginName: queryName.value.loginName
    }
  });
  loading.value = false;
  const data = res.data;
  if (data.success) {
    users.value = data.content.list;
    // 重置分页按钮
    pagination.value.current = queryInfo.page;
    pagination.value.total = data.content.total;
  } else {
    message.error(data.message);
  }
};

/**
 * 表格点击页码时触发
 */
const handleTableChange = (pagination: any) => {
  handleQuery({
    page: pagination.current,
    size: pagination.pageSize
  });
};

// -------- 表单 ---------
const user = ref();
const formRef = ref();
const modalVisible = ref(false);
const modalLoading = ref(false);
const handleModalOk = async () => {
  formRef.value
    .validate()
    .then(async () => {
      modalLoading.value = true;
      user.value.password = hexMd5(user.value.password + KEY);
      const res = await axios.post("/user/save", user.value);
      modalLoading.value = false;
      const data = res.data;
      if (data.success) {
        modalVisible.value = false;
        // 重新加载列表
        handleQuery({
          page: pagination.value.current,
          size: pagination.value.pageSize
        });
      } else {
        message.error(data.message);
      }
    })
    .catch(() => {
      message.error("请按要求完成表单");
    });
};

const rules = {
  loginName: [
    {
      required: true,
      message: "请输入登录名",
      trigger: "blur",
      type: "string"
    }
  ],
  name: [
    {
      required: true,
      message: "请输入用户名",
      trigger: "blur",
      type: "string"
    }
  ],
  password: [
    {
      required: true,
      message: "请输入密码",
      trigger: "blur",
      type: "string"
    }
  ]
};

/**
 * 编辑
 */
const edit = (record: any) => {
  modalVisible.value = true;
  user.value = Tool.copy(record);
};

/**
 * 新增
 */
const add = () => {
  modalVisible.value = true;
  user.value = {};
};

/**
 * 删除
 */
const handleDelete = async (id: number) => {
  const res = await axios.delete("/user/delete/" + id);
  const data = res.data; // data = commonResp
  if (data.success) {
    // 重新加载列表
    handleQuery({
      page: pagination.value.current,
      size: pagination.value.pageSize
    });
  } else {
    message.error(data.message);
  }
};

onMounted(() => {
  handleQuery({
    page: 1,
    size: pagination.value.pageSize
  });
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
