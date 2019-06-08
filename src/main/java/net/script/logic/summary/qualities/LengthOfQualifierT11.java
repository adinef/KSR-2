package net.script.logic.summary.qualities;

import net.script.logic.summary.SummarizationState;

public class LengthOfQualifierT11 {
    public static double calculateLengthOfQualifierT11(SummarizationState summarizationState) {
        if(summarizationState.getQualifiers().size() <= 0) return -1;
        return 2 * Math.pow(0.5, (double) summarizationState.getQualifiers().size());
    }
}
