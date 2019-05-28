package net.script.logic.settings.quantifier;

import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.Reader;
import net.script.logic.settings.quantifier.QuantifiersSettings.SingleQuantifierSetting;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Component
public class QuantifiersReader implements Reader<Quantifier> {

    private static final String SETTINGS_FILE_PATH = "q_settings.xml";

    private List<Quantifier> cached = null;

    public List<Quantifier> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache == true) {
            Serializer serializer = new Persister();
            File file = new File(SETTINGS_FILE_PATH);
            QuantifiersSettings read = serializer.read(QuantifiersSettings.class, file);
            this.cached = new LinkedList<>();
            List<SingleQuantifierSetting> quantifiers = read.getQuantifiers();
            for (SingleQuantifierSetting setting : quantifiers) {
                QFunction function = QFunctionFactory.getFunction(setting.getFunctionSetting());
                this.cached.add( new Quantifier(setting.getName(), setting.getMember(), function) );
            }
        }
        return this.cached;
    }

    public List<Quantifier> read() throws Exception {
        return this.read(false);
    }

}
