package net.script.logic.fuzzy;

import net.script.logic.fuzzy.linguistic.LinguisticVariable;

import java.lang.reflect.Field;
import java.util.*;
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

    public static <E> FuzzySet<E> of(Collection<E> elements, LinguisticVariable lVariable) throws NoSuchFieldException, IllegalAccessException {
        E next = elements.iterator().next();
        Class<?> aClass = next.getClass();
        Field declaredField = aClass.getDeclaredField(lVariable.getMemberFieldName());
        declaredField.setAccessible(true);
        Map<E, Double> map = new HashMap<>();
        for (E elem : elements) {
            if (declaredField.getType().equals(Integer.class)) {
                map.put(elem, lVariable.getFunction().calculate((Integer)declaredField.get(elem)));
            } else {
                map.put(elem, lVariable.getFunction().calculate((Double)declaredField.get(elem)));
            }
        }
        return new FuzzySet<E>(map);
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
