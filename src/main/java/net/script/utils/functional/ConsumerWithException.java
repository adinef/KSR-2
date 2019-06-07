package net.script.utils.functional;

@FunctionalInterface
public interface ConsumerWithException<T> {
    void consume(T elem) throws Exception;
}
