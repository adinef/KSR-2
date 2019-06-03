package net.script.logic.settings;

import lombok.Data;
import net.script.logic.fuzzy.functions.FunctionSetting;
import net.script.logic.settings.functions.FunctionsSettings;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;

@Data
public class SimpleLinguisticVariableSetting {
    @Attribute(name = "name")
    private String name;

    @Attribute(name = "member")
    private String member;

    @Path("range")
    @Element(name = "start")
    private Double rangeStart;

    @Path("range")
    @Element(name = "end")
    private Double rangeEnd;

    @Element(name = "function", type = FunctionsSettings.SingleFunctionSetting.class)
    private FunctionSetting functionSetting;
}
