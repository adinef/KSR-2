package net.script.logic.settings.qualifier;

import net.script.config.paths.PathConfig;
import net.script.config.paths.PathType;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.settings.ConfigAccessor;
import net.script.logic.settings.LinguisticVariableConfigMapper;
import net.script.logic.settings.SimpleLinguisticVariableSetting;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Service
public class QualifiersConfigAccessor implements ConfigAccessor<Qualifier> {

    private final PathConfig pathConfig;

    private List<Qualifier> cached = null;

    @Autowired
    public QualifiersConfigAccessor(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }

    public List<Qualifier> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache) {
            Serializer serializer = new Persister();
            File file = new File(pathConfig.knownPathFor(PathType.QUALIFIERS));
            QualifiersSettings read = serializer.read(QualifiersSettings.class, file);
            this.cached = new LinkedList<>();
            List<SimpleLinguisticVariableSetting> quantifiers = read.getQualifiers();
            for (SimpleLinguisticVariableSetting setting : quantifiers) {
                QFunction function = QFunctionFactory.getFunction(setting.getFunctionSetting());
                this.cached.add(
                        new Qualifier(
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
        QualifiersSettings newSettings = this.mapToSettings(this.cached);
        Serializer serializer = new Persister();
        File file = new File(this.pathConfig.knownPathFor(PathType.QUALIFIERS));
        serializer.write(newSettings, file);
    }

    private QualifiersSettings mapToSettings(List<Qualifier> cached) throws Exception {
        QualifiersSettings settings = new QualifiersSettings();
        List<SimpleLinguisticVariableSetting> variableSettings = new LinkedList<>();
        for (Qualifier qualifier : cached) {
            SimpleLinguisticVariableSetting setting =
                    LinguisticVariableConfigMapper.getSimpleWithFunction(qualifier);
            variableSettings.add(setting);
        }
        settings.setQualifiers(variableSettings);
        return settings;
    }

    public List<Qualifier> read() throws Exception {
        return this.read(false);
    }

}
