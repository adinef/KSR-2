package net.script.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class Tuple<T, K> {
    private T first;
    private K second;
}
