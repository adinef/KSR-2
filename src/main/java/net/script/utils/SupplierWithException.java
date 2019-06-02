package net.script.utils;

@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}
