package net.script.logic.settings.functions;

import net.script.config.paths.PathInjection;
import net.script.config.paths.PathType;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.settings.Reader;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@Service
public class FunctionsReader implements Reader<QFunction> {

    private final Path SETTINGS_FILE_PATH;

    private List<QFunction> cached = null;

    @Autowired
    public FunctionsReader(@PathInjection(PathType.FUNCTIONS) Path path) {
        SETTINGS_FILE_PATH = path;
    }

    @Override
    public List<QFunction> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache) {
            Serializer serializer = new Persister();
            File file = new File(SETTINGS_FILE_PATH.toString());
            FunctionsSettings read = serializer.read(FunctionsSettings.class, file);
            this.cached = new LinkedList<>();
            read.getFunctions().forEach( f -> this.cached.add(QFunctionFactory.getFunction(f)));
        }
        return this.cached;
    }

    @Override
    public List<QFunction> read() throws Exception {
        return this.read(false);
    }
}
