package net.script.utils.tasking;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class EntityReadListService<T> extends Service<List<T>> {

    private final Supplier<List<T>> supplier;

    public EntityReadListService(Supplier<List<T>> entityListSupplier) {
        this.supplier = entityListSupplier;
    }

    @Override
    protected Task<List<T>> createTask() {
        return new Task<List<T>>() {
            @Override
            protected List<T> call() throws Exception {
                return supplier.get();
            }
        };
    }
}
