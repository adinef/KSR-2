package net.script.logic.fuzzy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FuzzySet<T> implements Map<T, Double> {

    private Map<T, Double> elementsValuesMapping = new HashMap<>();

    public FuzzySet(Map<T, Double> anotherMap) {
        this.elementsValuesMapping = anotherMap;
    }

    public FuzzySet() {

    }

    public static <E> FuzzySet<E> intersect(FuzzySet<E> firstSet, FuzzySet<E> secondSet) {
        FuzzySet<E> operationResult = new FuzzySet<>();
        for (Map.Entry<E, Double> entry : firstSet.entrySet()) {
            if ( secondSet.containsKey(entry.getKey())) {
                operationResult.put(entry.getKey(), Double.min(entry.getValue(), secondSet.get(entry.getKey())));
            }
        }
        return operationResult;
    }

    public static <E> FuzzySet<E> sum(FuzzySet<E> firstSet, FuzzySet<E> secondSet) {
        FuzzySet<E> operationResult = new FuzzySet<>();
        for (Map.Entry<E, Double> entry : secondSet.entrySet()) {
            if (operationResult.containsKey(entry.getKey())) {
                operationResult.put(entry.getKey(), Double.max(entry.getValue(), operationResult.get(entry.getKey())));
            } else {
                operationResult.put(entry.getKey(), entry.getValue());
            }
        }
        return operationResult;
    }

    public Set<T> fuzzySetUniverse() {
        return this.keySet();
    }

    public Set<T> support() {
        return this.entrySet()
                .stream()
                .filter( entry -> entry.getValue() > 0 )
                .map( entry -> entry.getKey())
                .collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return this.elementsValuesMapping.size();
    }

    @Override
    public boolean isEmpty() {
        return this.elementsValuesMapping.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.elementsValuesMapping.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.elementsValuesMapping.containsValue(value);
    }

    @Override
    public Double get(Object key) {
        return this.elementsValuesMapping.get(key);
    }

    @Override
    public Double put(T key, Double value) {
        return this.elementsValuesMapping.put(key, value);
    }

    public Double putEntry(Map.Entry<T, Double> entry) {
        return this.elementsValuesMapping.put(entry.getKey(), entry.getValue());
    }

    @Override
    public Double remove(Object key) {
        return this.elementsValuesMapping.remove(key);
    }

    @Override
    public void putAll(Map<? extends T, ? extends Double> m) {
        this.elementsValuesMapping.putAll(m);
    }

    @Override
    public void clear() {
        this.elementsValuesMapping.clear();
    }

    @Override
    public Set<T> keySet() {
        return this.elementsValuesMapping.keySet();
    }

    @Override
    public Collection<Double> values() {
        return this.elementsValuesMapping.values();
    }

    @Override
    public Set<Entry<T, Double>> entrySet() {
        return this.elementsValuesMapping.entrySet();
    }
}
