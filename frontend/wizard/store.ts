import { reactive } from 'vue';

export const store = reactive({
    currentStep: 1,
    config: {
        repos: [] as any[],
        authors: [] as any[],
        groups: [] as any[],
        report: {
            title: 'RepoSense Report',
        } as any,
    },
    nextStep() {
        this.currentStep++;
    },
    prevStep() {
        if (this.currentStep > 1) {
            this.currentStep--;
        }
    },
    setStep(step: number) {
        this.currentStep = step;
    }
});
