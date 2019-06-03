package net.script.logic.settings.quantifier;

import net.script.config.paths.PathInjection;
import net.script.config.paths.PathType;
import net.script.data.annotations.Function;
import net.script.data.annotations.Coefficient;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.ConfigAccessor;
import net.script.logic.settings.LinguisticVariableConfigMapper;
import net.script.logic.settings.SimpleLinguisticVariableSetting;
import net.script.logic.settings.functions.FunctionsSettings;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class QuantifiersConfigAccessor implements ConfigAccessor<Quantifier> {

    private final Path SETTINGS_FILE_PATH;

    private List<Quantifier> cached = null;


    @Autowired
    public QuantifiersConfigAccessor(@PathInjection(PathType.QUANTIFIERS) Path settings_file_path) {
        SETTINGS_FILE_PATH = settings_file_path;
    }

    public List<Quantifier> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache) {
            Serializer serializer = new Persister();
            File file = new File(SETTINGS_FILE_PATH.toString());
            QuantifiersSettings read = serializer.read(QuantifiersSettings.class, file);
            this.cached = new LinkedList<>();
            List<SimpleLinguisticVariableSetting> quantifiers = read.getQuantifiers();
            for (SimpleLinguisticVariableSetting setting : quantifiers) {
                QFunction function = QFunctionFactory.getFunction(setting.getFunctionSetting());
                this.cached.add(
                        new Quantifier(
                                setting.getName(),
                                setting.getMember(),
                                function,
                                new Range(setting.getRangeStart(), setting.getRangeEnd()))
                );
            }
        }
        return this.cached;
    }

    @Override
    public void saveCachedData() throws Exception {
        QuantifiersSettings newSettings = this.mapToSettings(this.cached);
        Serializer serializer = new Persister();
        File file = new File(this.SETTINGS_FILE_PATH.toString());
        serializer.write(newSettings, file);
    }

    private QuantifiersSettings mapToSettings(List<Quantifier> cached) throws Exception {
        QuantifiersSettings settings = new QuantifiersSettings();
        List<SimpleLinguisticVariableSetting> variableSettings = new LinkedList<>();
        for (Quantifier quantifier : cached) {
            SimpleLinguisticVariableSetting setting =
                    LinguisticVariableConfigMapper.getSimpleWithFunction(quantifier);
            variableSettings.add(setting);
        }
        settings.setQuantifiers(variableSettings);
        return settings;
    }

    public List<Quantifier> read() throws Exception {
        return this.read(false);
    }

}
