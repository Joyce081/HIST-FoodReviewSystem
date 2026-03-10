<template>
  <div class="custom-tabs">
    <div
      v-for="tab in tabs"
      :key="tab.name"
      class="subtabs"
      :class="{ 'subtabs-active': modelValue === tab.name }"
      :style="{ width: tabsWidth }"
      @click="handleClick(tab)"
    >
      <span>{{ tab.label }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import "@/assets/css/components/customTabs/index.scss"

defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  tabsWidth: {
    type: String,
    default: "100px",
  },
  tabs: {
    type: Array<any>,
    default: () => [],
  },
});

const emit = defineEmits(['update:modelValue', 'tab-click']);

const handleClick = (tab: any) => {
  emit('update:modelValue', tab.name);
  emit('tab-click', tab);
};
</script>
