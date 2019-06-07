package net.script.config.paths;

import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Data
@Root(name = "paths")
public class PathSettings {
    @Element(name = "qualifiers")
    private String qualifierSettings;

    @Element(name = "quantifiers")
    private String quantifierSettings;

    @Element(name = "summarizers")
    private String summarizerSettings;
}
