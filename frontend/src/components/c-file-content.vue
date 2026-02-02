<template lang="pug">
.file-content-viewer
  .code-line(
    v-for="(line, index) in lines",
    :key="index"
  )
    .line-number {{ line.lineNumber }}
    .line-text {{ line.content || ' ' }}
</template>

<script lang='ts'>
import { defineComponent, PropType } from 'vue';

interface FileLine {
  lineNumber: number;
  content: string;
  author: {
    gitId: string;
  };
  isFullCredit: boolean;
}

export default defineComponent({
  name: 'c-file-content',
  props: {
    lines: {
      type: Array as PropType<Array<FileLine>>,
      required: true,
    },
  },
});
</script>

<style scoped lang="scss">
@import '../styles/_colors.scss';

.file-content-viewer {
  background: #fff;
  font-family: monospace;
  font-size: .9rem;
  text-align: left;
}

.code-line {
  display: flex;
  line-height: 1.5;

  &:hover {
    background: #f5f5f5;
  }
}

.line-number {
  color: mui-color('grey');
  flex-shrink: 0;
  padding: 0 .5rem;
  text-align: right;
  user-select: none;
  width: 3rem;
}

.line-text {
  flex: 1;
  padding-right: .5rem;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
