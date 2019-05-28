package net.script.logic.settings;

import java.util.List;

public interface Reader<T> {
    List<T> read() throws Exception;
    List<T> read(boolean cached) throws Exception;
}
