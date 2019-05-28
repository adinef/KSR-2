package net.script.logic.settings.functions;

import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.settings.Reader;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Component
public class FunctionsReader implements Reader<QFunction> {

    private static final String SETTINGS_FILE_PATH = "f_settings.xml";

    private List<QFunction> cached = null;

    @Override
    public List<QFunction> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache) {
            Serializer serializer = new Persister();
            File file = new File(SETTINGS_FILE_PATH);
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
