package net.script.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.annotations.Column;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

    @Column("Treść")
    private String content;

    @Column("[T] śr stopień prawdziwości")
    private double averageT;

    @Column("[T1] Stopień prawdziwości")
    private double degreeOfTruth;

    @Column("[T2] Stopień precyzyjności")
    private double degreeOfImprecision;

    @Column("[T3] Stopień pokrycia")
    private double degreeOfCovering;

    @Column("[T4] Stopień stosowności")
    private double degreOfAppropriateness;

    @Column("[T5] Długość podsumowania")
    private double lengthOfSummary;

    @Column("[T6] Stopień precyzyjności kwantyfikatora")
    private double degreeOfQuantifierImprecision;

    @Column("[T7] Stopień kardynalności kwantyfikatora")
    private double degreeOfQuantifierCardinality;

    @Column("[T8] Stopień kardynalności sumaryzatora")
    private double degreeOfSummarizerCardinality;

    @Column("[T9] Stopień precyzyjności kwalifikatora")
    private double degreeOfQualifierImprecision;

    @Column("[T10] Stopień kardynalności kwalifikatora")
    private double degreeOfQualifierCardinality;

    @Column("[T11] Długość kwalifikatora")
    private double lengthOfQualifier;
}
