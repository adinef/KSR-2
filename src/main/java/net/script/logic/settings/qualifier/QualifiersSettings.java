package net.script.logic.settings.qualifier;

import lombok.Data;
import net.script.logic.settings.SimpleLinguisticVariableSetting;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Data
@Root(name = "qualifiers")
public class QualifiersSettings {
    @ElementList(name = "definitions", entry = "qualifier")
    private List<SimpleLinguisticVariableSetting> qualifiers;
}
