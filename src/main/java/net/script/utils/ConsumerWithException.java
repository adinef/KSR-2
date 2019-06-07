package net.script.utils;

@FunctionalInterface
public interface ConsumerWithException<T> {
    void consume(T elem) throws Exception;
}
