package net.script.logic.settings.quantifier;

import lombok.Data;
import org.simpleframework.xml.*;

import java.util.List;

@Data
@Root(name = "quantifiers")
public class QuantifiersSettings {
    @ElementList(name = "definitions", entry = "quantifier")
    private List<SimpleQuantifierSetting> quantifiers;
}
