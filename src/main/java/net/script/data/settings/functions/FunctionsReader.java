package net.script.data.settings.functions;

import net.script.data.functions.QFunction;
import net.script.data.functions.factory.QFunctionFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Component
public class FunctionsReader {

    private static final String SETTINGS_FILE_PATH = "f_settings.xml";

    private List<QFunction> cached = null;

    public List<QFunction> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache == true) {
            Serializer serializer = new Persister();
            File file = new File(SETTINGS_FILE_PATH);
            FunctionsSetting read = serializer.read(FunctionsSetting.class, file);
            this.cached = new LinkedList<>();
            read.getFunctions().forEach( f -> this.cached.add(QFunctionFactory.getFunction(f)));
        }
        return this.cached;
    }

    public List<QFunction> read() throws Exception {
        return this.read(false);
    }

}
