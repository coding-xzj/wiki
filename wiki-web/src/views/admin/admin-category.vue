<template>
  <a-layout class="box">
    <a-layout-content class="border">
      <a-row type="flex" justify="end">
        <a-form layout="inline" :model="queryInfo" style="margin-bottom: 20px">
          <a-form-item>
            <a-button type="primary" @click="add()">新增</a-button>
          </a-form-item>
        </a-form>
      </a-row>
      <a-table
        :columns="columns"
        :row-key="(record) => record.id"
        :data-source="parentCate"
        :loading="loading"
        :pagination="false"
        :scroll="{ y: 380 }"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
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
    title="分类表单"
    v-model:visible="modalVisible"
    :confirm-loading="modalLoading"
    okText="确定"
    cancelText="取消"
    @ok="handleModalOk"
  >
    <a-form
      ref="formRef"
      :model="category"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
      :rules="rules"
    >
      <a-form-item name="name" label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item name="cate" label="父分类">
        <a-select v-model:value="category.parent" ref="select">
          <a-select-option :value="0">无</a-select-option>
          <a-select-option
            v-for="item in parentCate"
            :key="item.id"
            :value="item.id"
            :disabled="category.id === item.id"
            >{{ item.name }}</a-select-option
          >
        </a-select>
      </a-form-item>
      <a-form-item name="sort" label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { onMounted, ref, toRaw } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import { Tool } from "@/util/tool";

const queryInfo = ref();
const categorys = ref();
const loading = ref(false);

const columns = [
  {
    title: "名称",
    dataIndex: "name",
    width: "40%"
  },
  {
    title: "顺序",
    dataIndex: "sort",
    width: "40%"
  },
  {
    title: "操作",
    key: "action",
    width: "40%",
    slots: { customRender: "action" }
  }
];

const parentCate = ref(); // 存放一级分类
parentCate.value = [];

/**
 * 数据查询
 **/
const handleQuery = async () => {
  loading.value = true;
  categorys.value = [];
  const res = await axios.get("/category/all");
  loading.value = false;
  const data = res.data;
  if (data.success) {
    categorys.value = data.content;
    parentCate.value = [];

    parentCate.value = Tool.array2Tree(categorys.value, 0);
  } else {
    message.error(data.message);
  }
};

// -------- 表单 ---------
const formRef = ref();
const category = ref({});
const modalVisible = ref(false);
const modalLoading = ref(false);

const handleModalOk = () => {
  formRef.value
    .validate()
    .then(async () => {
      modalLoading.value = true;
      const res = await axios.post("/category/save", category.value);
      modalLoading.value = false;
      const data = res.data;
      if (data.success) {
        modalVisible.value = false;
        // 重新加载列表
        handleQuery();
      } else {
        message.error(data.message);
      }
    })
    .catch(() => {
      message.error("请按规则填入表单");
    });
};

const rules = {
  name: [
    {
      required: true,
      message: "请输入分类名称",
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
  category.value = Tool.copy(record);
};

/**
 * 新增
 */
const add = () => {
  modalVisible.value = true;
  category.value = {};
};

const handleDelete = async (id: number) => {
  const res = await axios.delete("/category/delete/" + id);
  const data = res.data;
  if (data.success) {
    // 重新加载列表
    handleQuery();
  } else {
    message.error(data.message);
  }
};

onMounted(() => {
  handleQuery();
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
.ant-table-body {
  min-height: 500px;
}
</style>
