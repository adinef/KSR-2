package net.script.logic.settings.functions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.logic.fuzzy.functions.FunctionSetting;
import org.simpleframework.xml.*;

import java.util.List;
import java.util.Map;

@Data
@Root(name = "functions")
public class FunctionsSettings {
    @ElementList(name = "definitions", entry = "function")
    private List<SingleFunctionSetting> functions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SingleFunctionSetting implements FunctionSetting {
        @Attribute(name = "name")
        private String name;

        @ElementMap(name = "coefficients", entry = "coefficient", key = "name", value = "value", attribute = true, inline = true)
        private Map<String, Double> coefficients;
    }
}
