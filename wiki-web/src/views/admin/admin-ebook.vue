<template>
  <a-layout class="box">
    <a-layout-content class="border">
      <!-- 工具栏 -->
      <a-row type="flex" justify="end">
        <a-form layout="inline" :model="queryName" style="margin-bottom: 20px">
          <a-form-item>
            <a-input
              v-model:value="queryName.name"
              placeholder="名称"
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
      <!-- 电子书 -->
      <a-table
        :columns="columns"
        :row-key="(record) => record.id"
        :data-source="ebooks"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        style="min-height: 395px"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template #category="{ record }">
          <span>
            {{ getCategoryName(record.category1Id) }} /
            {{ getCategoryName(record.category2Id) }}
          </span>
        </template>
        <template #action="{ record }">
          <a-space size="small">
            <router-link :to="'/admin/doc?ebookId=' + record.id">
              <a-button type="primary">文档管理</a-button>
            </router-link>
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
    title="电子书表单"
    v-model:visible="modalVisible"
    :confirm-loading="modalLoading"
    okText="确定"
    cancelText="取消"
    @ok="handleModalOk"
  >
    <a-form
      ref="formRef"
      :rules="rules"
      :model="ebook"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
    >
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item name="name" label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader
          v-model:value="categoryIds"
          placeholder="请选择"
          :field-names="{ label: 'name', value: 'id', children: 'children' }"
          :options="parentCate"
        />
      </a-form-item>
      <a-form-item name="description" label="描述">
        <a-input v-model:value="ebook.description" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import { Tool } from "@/util/tool";

// 查询条件
const queryName = ref();
queryName.value = {};

// 存储电子书
const ebooks = ref();

// 分页管理
const pagination = ref({
  current: 1,
  pageSize: 4,
  total: 0
});

// 请求时的加载
const loading = ref(false);

// 定义表格
const columns = [
  {
    title: "封面",
    dataIndex: "cover",
    slots: { customRender: "cover" },
    width: 200
  },
  {
    title: "名称",
    dataIndex: "name",
    width: 200
  },
  {
    title: "分类",
    slots: { customRender: "category" },
    width: 200
  },
  {
    title: "文档数",
    dataIndex: "docCount",
    width: 120
  },
  {
    title: "阅读数",
    dataIndex: "viewCount",
    width: 120
  },
  {
    title: "点赞数",
    dataIndex: "voteCount",
    width: 120
  },
  {
    title: "操作",
    key: "action",
    slots: { customRender: "action" }
  }
];

/**
 * 数据查询
 **/
const handleQuery = async (queryInfo: any) => {
  loading.value = true;

  ebooks.value = [];
  const res = await axios.get("/ebook/list", {
    params: {
      page: queryInfo.page,
      size: queryInfo.size,
      name: queryName.value.name
    }
  });

  loading.value = false;
  console.log(res);
  const data = res.data;
  if (data.success) {
    // 获取电子书数据列表
    ebooks.value = data.content.list;
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
  console.log("看看自带的分页参数都有啥：" + pagination);
  handleQuery({
    page: pagination.current,
    size: pagination.pageSize
  });
};

// -------- 表单 ---------
const formRef = ref();
const categoryIds = ref(); // 父分类ID
const ebook = ref();
const modalVisible = ref(false);
const modalLoading = ref(false);

// 保存
const handleModalOk = () => {
  formRef.value
    .validate()
    .then(async () => {
      modalLoading.value = true;
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];

      const res = await axios.post("/ebook/save", ebook.value);
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
      message.error("请按规则完成表单");
    });
};

const rules = {
  name: [
    {
      required: true,
      message: "请输入电子书名称",
      trigger: "blur",
      type: "string"
    }
  ],
  description: [
    {
      required: true,
      message: "请描述电子书",
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
  ebook.value = Tool.copy(record);
  categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id];
};

/**
 * 新增
 */
const add = () => {
  modalVisible.value = true;
  ebook.value = {};
};

/**
 * 删除
 */
const handleDelete = async (id: number) => {
  const res = await axios.delete("/ebook/delete/" + id);
  const data = res.data;
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

const parentCate = ref(); // 记录一级分类
let categorys: any; // 记录所有分类
/**
 * 查询所有分类
 **/
const handleQueryCategory = async () => {
  loading.value = true;
  const res = await axios.get("/category/all");
  loading.value = false;
  const data = res.data;

  if (data.success) {
    categorys = data.content;
    parentCate.value = Tool.array2Tree(categorys, 0);
    handleQuery({
      page: 1,
      size: pagination.value.pageSize
    });
  } else {
    message.error(data.message);
  }
};

const getCategoryName = (cid: number) => {
  let result = "";
  categorys.forEach((item: any) => {
    if (item.id === cid) {
      result = item.name;
    }
  });
  return result;
};

onMounted(() => {
  handleQueryCategory();
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
