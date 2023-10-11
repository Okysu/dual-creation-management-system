<template>
  <n-modal v-model:show="showModal">
    <n-card
      style="width: 600px"
      :title="title"
      :bordered="false"
      size="huge"
      role="dialog"
      aria-modal="true"
    >
      <n-form
        ref="formRef"
        :label-placement="labelPlacement"
        :model="fieldValue"
        :rules="rules"
      >
        <n-form-item
          v-for="item in field"
          :label="item.label"
          :key="item.field"
          :path="item.field"
        >
          <n-input
            v-if="item.type === 'input'"
            v-model:value="fieldValue[item.field]"
            :placeholder="item.placeholder ?? undefined"
            :disabled="item.disabled ?? false"
          />
          <n-input
            type="password"
            v-else-if="item.type === 'input-password'"
            v-model:value="fieldValue[item.field]"
            :placeholder="item.placeholder ?? undefined"
            :disabled="item.disabled ?? false"
          />
          <n-input-number
            v-else-if="item.type === 'input-number'"
            v-model:value="fieldValue[item.field]"
            :placeholder="item.placeholder ?? undefined"
            :disabled="item.disabled ?? false"
          />
          <n-select
            v-else-if="item.type === 'select'"
            v-model:value="fieldValue[item.field]"
            :options="item.options"
            :disabled="item.disabled ?? false"
          />
          <n-date-picker
            v-else-if="item.type === 'date-picker'"
            v-model:value="fieldValue[item.field]"
            type="datetime"
            :disabled="item.disabled ?? false"
            clearable
          />
          <n-transfer
            v-else-if="item.type === 'transfer'"
            v-model:value="fieldValue[item.field]"
            virtual-scroll
            :options="item.options"
            :disabled="item.disabled ?? false"
            source-filterable
            target-filterable
          />
          <n-input
            v-else-if="item.type === 'textarea'"
            v-model:value="fieldValue[item.field]"
            type="textarea"
            :placeholder="item.placeholder ?? undefined"
            :disabled="item.disabled ?? false"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space style="float: right">
          <n-button
            @click="(showModal = false), onCancel(deepClone(fieldValue))"
            >取消</n-button
          >
          <n-button type="primary" @click="formSubmit">确定</n-button>
        </n-space>
      </template>
    </n-card>
  </n-modal>
</template>

<script lang="ts" setup>
import { ref, type PropType } from "vue";
import { deepClone } from "@/utils/function";
import type { FormInst } from "naive-ui";

const formRef = ref<FormInst | null>(null);
export type Field = {
  label: string;
  field: string;
  type:
    | "input"
    | "input-password"
    | "input-number"
    | "select"
    | "date-picker"
    | "transfer"
    | "textarea";
  options?: any[];
  placeholder?: string;
  disabled?: boolean;
};
const fieldValue = ref<{ [key: string]: any }>({});
const showModal = ref(false);
const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  onCancel: {
    type: Function,
    required: false,
    default: () => {},
  },
  onOpen: {
    type: Function,
    required: false,
    default: () => {},
  },
  onSubmit: {
    type: Function,
    required: false,
    default: () => {},
  },
  field: {
    type: Array as PropType<Field[]>,
    required: true,
  },
  defaultValue: {
    type: Object as PropType<{ [key: string]: any }>,
    required: false,
    default: () => ({}),
  },
  rules: {
    type: Object as PropType<{ [key: string]: any }>,
    required: false,
    default: () => ({}),
  },
  labelPlacement: {
    type: String as PropType<"left" | "top">,
    required: false,
    default: "left",
  },
});

const initForm = (defaultValue?: { [key: string]: any }) => {
  fieldValue.value = {};
  const defaultVal = defaultValue ?? props.defaultValue;
  props.field.forEach((item) => {
    fieldValue.value[item.field] =
      defaultVal[item.field] ??
      (item.type === "input-number" || item.type === "date-picker"
        ? 0
        : item.type === "transfer"
        ? []
        : "");
  });
  Object.keys(defaultVal).forEach((key) => {
    if (!fieldValue.value[key]) {
      fieldValue.value[key] = defaultVal[key];
    }
  });
};

const formSubmit = (e: Event) => {
  e.preventDefault();
  formRef.value?.validate((errors) => {
    if (!errors) {
      props.onSubmit(deepClone(fieldValue.value), true);
    } else {
      props.onSubmit(deepClone(fieldValue.value), false);
    }
  });
};

const toggleModal = () => {
  showModal.value = !showModal.value;
  if (!showModal.value) {
    props.onCancel(deepClone(fieldValue.value));
  } else {
    initForm();
    props.onOpen(deepClone(fieldValue.value));
  }
};

const openModal = (defaultValue?: { [key: string]: any }) => {
  initForm(defaultValue);
  showModal.value = true;
  props.onOpen(deepClone(fieldValue.value));
};

const closeModal = () => {
  showModal.value = false;
  props.onCancel(deepClone(fieldValue.value));
};

defineExpose({
  toggleModal,
  openModal,
  closeModal,
});
</script>

<style scoped>
.n-input-number,
.n-date-picker {
  width: 100%;
}
</style>
