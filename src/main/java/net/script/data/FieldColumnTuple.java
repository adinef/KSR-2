package net.script.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.script.data.annotations.Column;

import java.lang.reflect.Field;

@AllArgsConstructor
public class FieldColumnTuple {
    @Getter
    private Field field;
    @Getter
    private Column column;

    @Override
    public String toString() {
        return column.value();
    }
}
