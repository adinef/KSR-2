package net.script.logic.settings.quantifier;

import lombok.Data;
import net.script.logic.fuzzy.functions.FunctionSetting;
import net.script.logic.settings.functions.FunctionsSettings;
import net.script.logic.settings.functions.FunctionsSettings.SingleFunctionSetting;
import org.simpleframework.xml.*;

import java.util.List;
import java.util.Map;

@Data
@Root(name = "quantifiers")
public class QuantifiersSettings {
    @ElementList(name = "definitions", entry = "quantifier")
    private List<SingleQuantifierSetting> quantifiers;

    @Data
    public static class SingleQuantifierSetting {
        @Attribute(name = "name")
        private String name;

        @Attribute(name = "member")
        private String member;

        @Element(name = "function", type = SingleFunctionSetting.class)
        private FunctionSetting functionSetting;
    }
}
