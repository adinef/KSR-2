package net.script.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.annotations.Column;
import net.script.logic.summary.QualityTuple;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

    @Column("Treść")
    private String content;

    @Column("[T] śr stopień prawdziwości")
    private QualityTuple averageT;

    @Column("[T1] Stopień prawdziwości")
    private QualityTuple degreeOfTruth;

    @Column("[T2] Stopień nieprecyzyjności")
    private QualityTuple degreeOfImprecision;

    @Column("[T3] Stopień pokrycia")
    private QualityTuple degreeOfCovering;

    @Column("[T4] Stopień stosowności")
    private QualityTuple degreOfAppropriateness;

    @Column("[T5] Długość podsumowania")
    private QualityTuple lengthOfSummary;

    @Column("[T6] Stopień nieprecyzyjności kwantyfikatora")
    private QualityTuple degreeOfQuantifierImprecision;

    @Column("[T7] Stopień kardynalności kwantyfikatora")
    private QualityTuple degreeOfQuantifierCardinality;

    @Column("[T8] Stopień kardynalności sumaryzatora")
    private QualityTuple degreeOfSummarizerCardinality;

    @Column("[T9] Stopień nieprecyzyjności kwalifikatora")
    private QualityTuple degreeOfQualifierImprecision;

    @Column("[T10] Stopień kardynalności kwalifikatora")
    private QualityTuple degreeOfQualifierCardinality;

    @Column("[T11] Długość kwalifikatora")
    private QualityTuple lengthOfQualifier;
}
