package net.script.logic.fuzzy.linguistic;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
public class Range {
    private Double begin;
    private Double end;

    @Override
    public String toString() {
        return String.format("[Początek: %s, Koniec: %s]", begin, end);
    }
}
