package net.script.logic.settings.functions;

import lombok.Data;
import net.script.logic.fuzzy.functions.FunctionSetting;
import org.simpleframework.xml.*;

import java.util.List;
import java.util.Map;

@Data
@Root(name = "functions")
public class FunctionsSetting {
    @ElementList(name = "functions", entry = "function")
    private List<SingleFunctionSetting> functions;

    @Data
    public static class SingleFunctionSetting implements FunctionSetting {
        @Attribute(name = "name")
        private String name;

        @ElementMap(name = "coefficients", entry = "coefficient", key = "name", value = "value", attribute = true, inline = true)
        private Map<String, Double> coefficients;
    }
}
