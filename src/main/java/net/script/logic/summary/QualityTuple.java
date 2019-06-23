package net.script.logic.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QualityTuple {
    private Double value;
    private Metadata metadata;

    public static QualityTuple of(Double value, Object... objs) {
        return new QualityTuple(value, new Metadata(objs));
    }

    @Override
    public String toString() {
        return (value != null ? value.toString() : "");
    }
}
