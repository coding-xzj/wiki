<template>
  <a-layout style="margin-top: 20px; padding: 0 50px">
    <a-layout-content
      :style="{
        background: '#fff',
        padding: '20px 30px',
        margin: 0,
        minHeight: '280px',
      }"
    >
      <a-row :gutter="24">
        <a-col :span="6">
          <a-form layout="inline" :model="param">
            <a-form-item>
              <a-button type="primary" @click="handleQuery()"> 查询 </a-button>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="add()"> 新增 </a-button>
            </a-form-item>
          </a-form>
          <a-table
            v-if="parentCate.length > 0"
            :columns="columns"
            :row-key="(record) => record.id"
            :data-source="parentCate"
            :loading="loading"
            :pagination="false"
            size="small"
            :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{ record.sort }} {{ text }}
            </template>
            <template v-slot:action="{ record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                  title="删除后不可恢复，确认删除?"
                  ok-text="是"
                  cancel-text="否"
                  @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger" size="small"> 删除 </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="18">
          <a-form :model="doc" layout="vertical">
            <a-row :gutter="20">
              <a-col :span="12">
                <a-form-item>
                  <a-input v-model:value="doc.name" placeholder="名称" />
                </a-form-item>
              </a-col>
              <a-col :span="6"
                ><a-form-item>
                  <a-input
                    v-model:value="doc.sort"
                    placeholder="顺序"
                  /> </a-form-item
              ></a-col>
              <a-col :span="6"
                ><a-form-item>
                  <a-tree-select
                    v-model:value="doc.parent"
                    style="width: 100%"
                    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                    :tree-data="treeSelectData"
                    placeholder="请选择父文档"
                    tree-default-expand-all
                    :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
                  >
                  </a-tree-select> </a-form-item
              ></a-col>
              <a-form-item>
                <div id="content"></div>
              </a-form-item>
            </a-row>
            <p style="text-align: right">
              <a-form layout="inline" :model="param">
                <a-form-item>
                  <a-button type="primary" @click="handleSave()">
                    保存
                  </a-button>
                </a-form-item>
                <a-form-item>
                  <a-button type="primary" @click="handlePreviewContent()">
                    <EyeOutlined /> 内容预览
                  </a-button>
                </a-form-item>
              </a-form>
            </p>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer
        width="900"
        placement="right"
        :closable="false"
        :visible="drawerVisible"
        @close="onDrawerClose"
      >
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts" setup>
import { onMounted, ref, createVNode } from "vue";
import axios from "axios";
import { message, Modal } from "ant-design-vue";
import { Tool } from "@/util/tool";
import { useRoute } from "vue-router";
import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
import E from "wangeditor";

const route = useRoute();

const param = ref();
param.value = {};
const docs = ref();
const loading = ref(false);
// 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
const treeSelectData = ref();
treeSelectData.value = [];

const columns = [
  {
    title: "名称",
    dataIndex: "name",
    slots: { customRender: "name" }
  },
  {
    title: "操作",
    key: "action",
    slots: { customRender: "action" }
  }
];

const parentCate = ref(); // 一级分类
parentCate.value = [];

/**
 * 数据查询
 **/
const handleQuery = async () => {
  loading.value = true;
  parentCate.value = [];
  const res = await axios.get("/doc/all/" + route.query.ebookId);
  loading.value = false;
  const data = res.data;
  if (data.success) {
    docs.value = data.content;

    parentCate.value = [];
    parentCate.value = Tool.array2Tree(docs.value, 0);

    // 父文档下拉框初始化，相当于点击新增
    treeSelectData.value = Tool.copy(parentCate.value) || [];
    // 为选择树添加一个"无"
    treeSelectData.value.unshift({ id: 0, name: "无" });
  } else {
    message.error(data.message);
  }
};

// -------- 表单 ---------
const doc = ref();
doc.value = {
  ebookId: route.query.ebookId
};
const modalVisible = ref(false);
const modalLoading = ref(false);
const editor = new E("#content");
editor.config.zIndex = 0;

const handleSave = async () => {
  modalLoading.value = true;
  doc.value.content = editor.txt.html();
  const res = await axios.post("/doc/save", doc.value);
  modalLoading.value = false;
  const data = res.data;
  if (data.success) {
    // modalVisible.value = false;
    message.success("保存成功！");

    // 重新加载列表
    handleQuery();
  } else {
    message.error(data.message);
  }
};

/**
 * 将某节点及其子孙节点全部置为disabled
 */
const setDisable = (treeSelectData: any, id: any) => {
  // console.log(treeSelectData, id);
  // 遍历数组，即遍历某一层节点
  for (let i = 0; i < treeSelectData.length; i++) {
    const node = treeSelectData[i];
    if (node.id === id) {
      // 如果当前节点就是目标节点
      console.log("disabled", node);
      // 将目标节点设置为disabled
      node.disabled = true;

      // 遍历所有子节点，将所有子节点全部都加上disabled
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        for (let j = 0; j < children.length; j++) {
          setDisable(children, children[j].id);
        }
      }
    } else {
      // 如果当前节点不是目标节点，则到其子节点再找找看。
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        setDisable(children, id);
      }
    }
  }
};

const deleteIds: Array<string> = [];
const deleteNames: Array<string> = [];
/**
 * 查找整根树枝
 */
const getDeleteIds = (treeSelectData: any, id: any) => {
  // console.log(treeSelectData, id);
  // 遍历数组，即遍历某一层节点
  for (let i = 0; i < treeSelectData.length; i++) {
    const node = treeSelectData[i];
    if (node.id === id) {
      // 如果当前节点就是目标节点
      console.log("delete", node);
      // 将目标ID放入结果集ids
      // node.disabled = true;
      deleteIds.push(id);
      deleteNames.push(node.name);

      // 遍历所有子节点
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        for (let j = 0; j < children.length; j++) {
          getDeleteIds(children, children[j].id);
        }
      }
    } else {
      // 如果当前节点不是目标节点，则到其子节点再找找看。
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        getDeleteIds(children, id);
      }
    }
  }
};

/**
 * 内容查询
 **/
const handleQueryContent = async () => {
  const res = await axios.get("/doc/find-content/" + doc.value.id);
  const data = res.data;
  if (data.success) {
    editor.txt.html(data.content);
  } else {
    message.error(data.message);
  }
};

/**
 * 编辑
 */
const edit = (record: any) => {
  // 清空富文本框
  editor.txt.html("");
  modalVisible.value = true;
  doc.value = Tool.copy(record);
  handleQueryContent();

  // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
  treeSelectData.value = Tool.copy(parentCate.value);
  setDisable(treeSelectData.value, record.id);

  // 为选择树添加一个"无"
  treeSelectData.value.unshift({ id: 0, name: "无" });
};

/**
 * 新增
 */
const add = () => {
  // 清空富文本框
  editor.txt.html("");
  modalVisible.value = true;
  doc.value = {
    ebookId: route.query.ebookId
  };

  treeSelectData.value = Tool.copy(parentCate.value) || [];

  // 为选择树添加一个"无"
  treeSelectData.value.unshift({ id: 0, name: "无" });
};

const handleDelete = (id: number) => {
  // console.log(parentCate, parentCate.value, id)
  // 清空数组，否则多次删除时，数组会一直增加
  deleteIds.length = 0;
  deleteNames.length = 0;
  getDeleteIds(parentCate.value, id);
  Modal.confirm({
    title: "重要提醒",
    icon: createVNode(ExclamationCircleOutlined),
    content:
      "将删除：【" + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
    async onOk() {
      // console.log(ids)
      const res = await axios.delete("/doc/delete/" + deleteIds.join(","));
      const data = res.data; // data = commonResp
      if (data.success) {
        // 重新加载列表
        handleQuery();
      } else {
        message.error(data.message);
      }
    }
  });
};

// ----------------富文本预览--------------
const drawerVisible = ref(false);
const previewHtml = ref();
const handlePreviewContent = () => {
  const html = editor.txt.html();
  previewHtml.value = html;
  drawerVisible.value = true;
};
const onDrawerClose = () => {
  drawerVisible.value = false;
};

onMounted(() => {
  handleQuery();

  editor.create();
});
</script>

<style scoped>
#content {
  margin-left: 10px;
}
img {
  width: 50px;
  height: 50px;
}
</style>
