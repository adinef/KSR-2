package net.script.view;

import lombok.Data;
import net.script.data.annotations.Column;

@Data
public class Summary {

    @Column("Treść")
    private String content;

    @Column("Stopień prawdziwości")
    private double degreeOfTruth;
}
