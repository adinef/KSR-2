package net.script.logic.settings.qualifier;

import lombok.Data;
import net.script.logic.fuzzy.functions.FunctionSetting;
import net.script.logic.settings.SimpleLinguisticVariableSetting;
import net.script.logic.settings.functions.FunctionsSettings.SingleFunctionSetting;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Data
@Root(name = "qualifiers")
public class QualifiersSettings {
    @ElementList(name = "definitions", entry = "qualifier")
    private List<SimpleLinguisticVariableSetting> qualifiers;
}
