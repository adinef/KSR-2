package net.script.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.annotations.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summary {

    @Column("Treść")
    private String content;

    @Column("Stopień prawdziwości")
    private double degreeOfTruth;
}
