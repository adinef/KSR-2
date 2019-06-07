package net.script.utils.functional;

@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}
