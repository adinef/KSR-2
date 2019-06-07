package net.script.utils.functional;

@FunctionalInterface
public interface RunnableWithException {
    void run() throws Exception;
}
