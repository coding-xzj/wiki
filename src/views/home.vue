<template>
  <a-layout>
    <a-layout-content style="padding: 0 50px">
      <a-layout style="padding: 24px 0; margin: 20px 0; background: #fff">
        <a-layout-sider class="sider" width="250">
          <a-menu
            mode="inline"
            theme="light"
            :style="{ height: '100%' }"
            @click="handleClick"
            :openKeys="openKeys"
            @openChange="onOpenChange"
          >
            <a-menu-item key="welcome">
              <MailOutlined />
              <span>欢迎</span>
            </a-menu-item>
            <a-sub-menu v-for="item in parentCate" :key="item.id">
              <template v-slot:title>
                <span>{{ item.name }}</span>
              </template>
              <a-menu-item v-for="child in item.children" :key="child.id">
                <span>{{ child.name }}</span>
              </a-menu-item>
            </a-sub-menu>
          </a-menu>
        </a-layout-sider>
        <a-layout-content
          :style="{
            padding: '0 24px',
            minHeight: '280px',
            maxHeight: '485px',
            overflowY: 'scroll',
          }"
        >
          <div class="ebook" v-if="isShowWelcome">
            <the-welcome></the-welcome>
          </div>
          <a-list
            class="ebook"
            v-else-if="isShowList"
            item-layout="vertical"
            size="large"
            :grid="{ gutter: 20, column: 3 }"
            :data-source="ebooks"
          >
            <template #renderItem="{ item }">
              <a-list-item key="item.name">
                <template #actions>
                  <span>
                    <component
                      v-bind:is="'FileOutlined'"
                      style="margin-right: 8px"
                    />
                    {{ item.docCount }}
                  </span>
                  <span>
                    <component
                      v-bind:is="'UserOutlined'"
                      style="margin-right: 8px"
                    />
                    {{ item.viewCount }}
                  </span>
                  <span>
                    <component
                      v-bind:is="'LikeOutlined'"
                      style="margin-right: 8px"
                    />
                    {{ item.voteCount }}
                  </span>
                </template>
                <a-list-item-meta :description="item.description">
                  <template #title>
                    <router-link :to="'/doc?ebookId=' + item.id">
                      {{ item.name }}
                    </router-link>
                  </template>
                  <template #avatar><a-avatar :src="item.cover" /></template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
          <p class="noEbook" v-else>该分类暂无电子书哦~去看看其他分类吧</p>
        </a-layout-content>
      </a-layout>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import { Tool } from "@/util/tool";
import TheWelcome from "@/components/the-welcome.vue";

const ebooks = ref();

// 记录所有分类
const rootSubmenuKeys = ref();

const parentCate = ref();
let categorys: any;

/**
 * 获取所有分类
 **/
const handleQueryCategory = async () => {
  const res = await axios.get("/category/all");
  const data = res.data;
  if (data.success) {
    categorys = data.content;

    // 记录所有分类id,用于(只)展开(父级)菜单
    rootSubmenuKeys.value = [];
    for (let i = 0; i < categorys.length; i++) {
      rootSubmenuKeys.value.push(categorys[i].id);
    }

    // 记录父分类
    parentCate.value = [];
    parentCate.value = Tool.array2Tree(categorys, 0);
  } else {
    message.error(data.message);
  }
};

const isShowWelcome = ref(true);

/**
 * 获取文章列表
 */
let categoryId2 = 0;
const isShowList = ref(true);
const handleQueryEbook = async () => {
  const res = await axios.get("/ebook/list", {
    params: {
      page: 1,
      size: 10,
      categoryId2: categoryId2
    }
  });
  const data = res.data;
  ebooks.value = data.content.list;
  if (data.content.list.length === 0) {
    console.log(data.content.list.length);

    isShowList.value = false;
  }
};

const handleClick = (value: any) => {
  isShowList.value = true;
  if (value.key === "welcome") {
    isShowWelcome.value = true;
  } else {
    categoryId2 = value.key;
    isShowWelcome.value = false;
    handleQueryEbook();
  }
};

// 只展开当前父级菜单
// 点击菜单，收起其他展开的所有菜单，保持菜单聚焦简洁。
const openKeys = ref([""]);
const onOpenChange = (openItems: string[]) => {
  const latestOpenKey = openItems.find(
    key => openKeys.value.indexOf(key) === -1
  );
  if (rootSubmenuKeys.value.indexOf(latestOpenKey!) === -1) {
    openKeys.value = openItems;
  } else {
    openKeys.value = latestOpenKey ? [latestOpenKey] : [];
  }
};

onMounted(() => {
  handleQueryCategory();
});
</script>

<style scoped>
.noEbook {
  height: 90%;
  width: 100%;
  opacity: 0.7;
  text-align: center;
  line-height: 130px;
  background-image: url("../assets/noEbook.png");
  background-size: 50%;
  background-position: 50% 30%;
  background-repeat: no-repeat;
}
/* .sider {
  background: url("../assets/logBg.png");
} */
/* .ebook {
  height: 90%;
  width: 100%;
  opacity: 0.7;
  background-image: url("../assets/Ebook.png");
  background-size: 80%;
  background-position: 50% 40%;
  background-repeat: no-repeat;
} */
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
.ant-layout-sider {
  background: #fff;
}
</style>
