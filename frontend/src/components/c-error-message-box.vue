<template lang='pug'>
  .error-message-box(v-if="Object.entries(errorMessages).length")
    .error-message-box__close-button(@click="dismissTab($event)") &times;
    .error-message-box__message The following issues occurred when analyzing the following repositories:
    .error-message-box__failed-repo(
      v-for="errorBlock in errorIsShowingMore\
          ? errorMessages\
          : Object.values(errorMessages).slice(0, numberOfErrorMessagesToShow)"
    )
      font-awesome-icon(icon="exclamation")
      span.error-message-box__failed-repo--name {{ errorBlock.repoName }}
      .error-message-box__failed-repo--reason(
        v-if="errorBlock.errorMessage.startsWith('Unexpected error stack trace')"
      )
        span Oops, an unexpected error occurred. If this is due to a problem in RepoSense, please report in&nbsp;
        a(
          :href="getReportIssueGitHubLink(errorBlock.errorMessage)", target="_blank"
        )
          strong our issue tracker&nbsp;
        span or email us at&nbsp;
        a(
          :href="getReportIssueEmailLink(errorBlock.errorMessage)"
        )
          span {{ getReportIssueEmailAddress() }}
      .error-message-box__failed-repo--reason(v-else) {{ errorBlock.errorMessage }}\
    .error-message-box__show-more-container(v-if="Object.keys(errorMessages).length > numberOfErrorMessagesToShow")
      span(v-if="!errorIsShowingMore") Remaining error messages omitted to save space.&nbsp;
      a(v-if="!errorIsShowingMore", @click="toggleErrorShowMore()") SHOW ALL...
      a(v-else, @click="toggleErrorShowMore()") SHOW LESS...
</template>

<script lang='ts'>
import { defineComponent, PropType } from 'vue';

import { ErrorMessage } from "../types/zod/summary-type";

export default defineComponent({
  name: 'c-error-message-box',

  props: {
    errorMessages: {
      type: Object as PropType<ErrorMessage>,
      required: true,
    },
  },

  data(): {
    errorIsShowingMore: boolean,
    numberOfErrorMessagesToShow: number,
    reportIssueEmailAddress: string,
  } {
    return {
      errorIsShowingMore: false,
      numberOfErrorMessagesToShow: 4,
      reportIssueEmailAddress: 'seer@comp.nus.edu.sg',
    };
  },

  computed: {
    getReportIssueTitle(): string {
      return `${encodeURI('Unexpected error with RepoSense version ')}${window.repoSenseVersion}`;
    },
  },

  methods: {
    dismissTab(event: Event): void {
      if (event.target instanceof Element && event.target.parentNode instanceof HTMLElement) {
        event.target.parentNode.style.display = 'none';
      }
    },

    toggleErrorShowMore(): void {
      this.errorIsShowingMore = !this.errorIsShowingMore;
    },

    getReportIssueGitHubLink(stackTrace: string): string {
      return `${window.REPOSENSE_REPO_URL}/issues/new?title=${this.getReportIssueTitle}
      &body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueEmailLink(stackTrace: string): string {
      return `mailto:${this.reportIssueEmailAddress}?subject=${this.getReportIssueTitle}
      &body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueMessage(message: string): string {
      return encodeURI(message);
    },
  },
});
</script>

<style lang='scss'>
.error-message-box__show-more-container {
  display: flex;
  justify-content: flex-end;
  margin-top: .3rem;
}
</style>
