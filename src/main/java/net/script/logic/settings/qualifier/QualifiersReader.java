package net.script.logic.settings.qualifier;

import net.script.config.paths.PathInjection;
import net.script.config.paths.PathType;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.Reader;
import net.script.logic.settings.SimpleLinguisticVariableSetting;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

@Service
public class QualifiersReader implements Reader<Qualifier> {

    private final Path SETTINGS_FILE_PATH;

    private List<Qualifier> cached = null;

    @Autowired
    public QualifiersReader(@PathInjection(PathType.QUALIFIERS) Path settings_file_path) {
        SETTINGS_FILE_PATH = settings_file_path;
    }

    public List<Qualifier> read(boolean reloadCache) throws Exception {
        if (cached == null || reloadCache) {
            Serializer serializer = new Persister();
            File file = new File(SETTINGS_FILE_PATH.toString());
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

    public List<Qualifier> read() throws Exception {
        return this.read(false);
    }

}
