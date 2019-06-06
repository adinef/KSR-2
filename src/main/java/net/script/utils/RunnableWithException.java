package net.script.utils;

@FunctionalInterface
public interface RunnableWithException {
    void run() throws Exception;
}
