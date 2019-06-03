package net.script.logic.settings;

import java.util.List;

public interface ConfigAccessor<T> {
    List<T> read() throws Exception;
    List<T> read(boolean cached) throws Exception;
    void saveCachedData() throws Exception;
}
