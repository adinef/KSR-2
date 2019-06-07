package net.script.config.paths;

import lombok.extern.slf4j.Slf4j;
import net.script.data.csv.CsvReader;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Component
@Slf4j
public class PathConfig {

    private static final String PATHS_FILE = "paths.xml";
    PathSettings pathSettings;

    public String knownPathFor(PathType pathType) {
        if (this.pathSettings == null) {
            try {
                Serializer serializer = new Persister();
                this.pathSettings = serializer.read(PathSettings.class, new File(PATHS_FILE));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        switch (pathType) {
            case QUALIFIERS:
                return this.pathSettings.getQualifierSettings();
            case QUANTIFIERS:
                return this.pathSettings.getQuantifierSettings();
            case SUMMARIZERS:
                return this.pathSettings.getSummarizerSettings();
        }
        return "";
    }

    public void setValue(PathType type, String value) {
        switch (type) {
            case QUALIFIERS:
                this.pathSettings.setQualifierSettings(value);
                break;
            case QUANTIFIERS:
                this.pathSettings.setQuantifierSettings(value);
                break;
            case SUMMARIZERS:
                this.pathSettings.setSummarizerSettings(value);
                break;
        }
    }

    public void savePathSettings() {
        Serializer serializer = new Persister();
        try {
            serializer.write(this.pathSettings, new File(PATHS_FILE));
        } catch (Exception e) {
            log.error("Error persisting config for paths. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
