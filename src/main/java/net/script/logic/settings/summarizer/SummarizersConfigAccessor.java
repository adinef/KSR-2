package net.script.logic.settings.summarizer;

import net.script.config.paths.PathConfig;
import net.script.config.paths.PathType;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.settings.ConfigAccessor;
import net.script.logic.settings.LinguisticVariableConfigMapper;
import net.script.logic.settings.SimpleLinguisticVariableSetting;
import net.script.logic.summarizer.Summarizer;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@Service
public class SummarizersConfigAccessor implements ConfigAccessor<Summarizer> {

    private final PathConfig pathConfig;

    private List<Summarizer> cached = null;

    @Autowired
    public SummarizersConfigAccessor(PathConfig pathConfig) {
        this.pathConfig = pathConfig;
    }

    public List<Summarizer> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache) {
            Serializer serializer = new Persister();
            File file = new File(pathConfig.knownPathFor(PathType.SUMMARIZERS));
            SummarizersSettings read = serializer.read(SummarizersSettings.class, file);
            this.cached = new LinkedList<>();
            List<SimpleLinguisticVariableSetting> summarizers = read.getSummarizers();
            for (SimpleLinguisticVariableSetting setting : summarizers) {
                QFunction function = QFunctionFactory.getFunction(setting.getFunctionSetting());
                this.cached.add(
                        new Summarizer(
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
        SummarizersSettings newSettings = this.mapToSettings(this.cached);
        Serializer serializer = new Persister();
        File file = new File(this.pathConfig.knownPathFor(PathType.SUMMARIZERS));
        serializer.write(newSettings, file);
    }

    private SummarizersSettings mapToSettings(List<Summarizer> cached) throws Exception {
        SummarizersSettings settings = new SummarizersSettings();
        List<SimpleLinguisticVariableSetting> variableSettings = new LinkedList<>();
        for (Summarizer summarizer : cached) {
            SimpleLinguisticVariableSetting setting =
                    LinguisticVariableConfigMapper.getSimpleWithFunction(summarizer);
            variableSettings.add(setting);
        }
        settings.setSummarizers(variableSettings);
        return settings;
    }

    public List<Summarizer> read() throws Exception {
        return this.read(false);
    }

}
