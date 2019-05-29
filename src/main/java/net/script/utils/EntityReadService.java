package net.script.utils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class EntityReadService<T> extends Service<Iterable<T>> {

    private final Supplier<Iterable<T>> supplier;

    public EntityReadService(Supplier<Iterable<T>> entityListSupplier) {
        this.supplier = entityListSupplier;
    }

    @Override
    protected Task<Iterable<T>> createTask() {
        return new Task<>() {
            @Override
            protected Iterable<T> call() throws Exception {
                return supplier.get();
            }
        };
    }
}
