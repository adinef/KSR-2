package net.script.logic.settings.quantifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.logic.fuzzy.functions.FunctionSetting;
import net.script.logic.settings.FunctionsSettings;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleQuantifierSetting {
    @Attribute(name = "name")
    private String name;

    @Element(name = "function", type = FunctionsSettings.SingleFunctionSetting.class)
    private FunctionSetting functionSetting;
}
