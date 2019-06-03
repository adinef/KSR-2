package net.script.logic.fuzzy;

import lombok.NonNull;
import lombok.ToString;
import net.script.data.annotations.Column;
import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@ToString(onlyExplicitlyIncluded = true)
public class FuzzySet<T> implements Map<T, Double> {

    @ToString.Include
    private Map<T, Double> elementsValuesMapping = new HashMap<>();

    @ToString.Include
    private LinguisticVariable linguisticVariable;

    private FuzzySet(Map<T, Double> anotherMap, LinguisticVariable lv) {
        this.elementsValuesMapping = anotherMap;
        this.linguisticVariable = lv;
    }

    private FuzzySet() {

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
                .map(Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static <E> FuzzySetBuilder<E> with(Collection<E> coll) {
        return new FuzzySetBuilder<>(coll);
    }

    @SafeVarargs
    public static <E> FuzzySetBuilder<E> with(E... elems) {
        if (elems != null) {
            return new FuzzySetBuilder<>(Arrays.asList(elems));
        } else {
            return new FuzzySetBuilder<>(new ArrayList<>());
        }
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

    public static class FuzzySetBuilder<E> {
        private final Collection<E> coll;

        private FuzzySetBuilder(Collection<E> collection) {
            this.coll = collection;
        }

        public FuzzySet<E> from(LinguisticVariable lv) {
            try {
                return this.of(this.coll, lv);
            } catch (Exception e) {
                e.printStackTrace();
                return new FuzzySet<>();
            }
        }

        private <K> FuzzySet<K> of(Collection<K> elements, LinguisticVariable lVariable) throws IllegalAccessException {
            K next = elements.iterator().next();
            Class<?> aClass = next.getClass();
            for (Field field : aClass.getDeclaredFields()) {
                Column columnAnn = field.getAnnotation(Column.class);
                if (columnAnn != null) {
                    if (appliesToColumn(lVariable, columnAnn)) {
                        field.setAccessible(true);
                        Map<K, Double> map = extractAndMapValues(elements, lVariable, field);
                        return new FuzzySet<>(map, lVariable);
                    }
                }
            }
            return new FuzzySet<>();
        }

        private <K> Map<K, Double> extractAndMapValues(Collection<K> elements,
                                             LinguisticVariable lVariable,
                                             Field field) throws IllegalAccessException {
            Map<K, Double> map = new HashMap<>();
            for (K elem : elements) {
                Object elemValue = field.get(elem);
                if (elemValue != null) {
                    if (field.getType().equals(Integer.class)) {
                        map.put(elem, lVariable.getFunction().calculate((Integer) elemValue));
                    } else {
                        map.put(elem, lVariable.getFunction().calculate((Double) elemValue));
                    }
                }
            }
            return map;
        }

        private boolean appliesToColumn(LinguisticVariable lVariable, Column annotation) {
            return annotation.value().equals(lVariable.getMemberFieldName());
        }
    }
}
