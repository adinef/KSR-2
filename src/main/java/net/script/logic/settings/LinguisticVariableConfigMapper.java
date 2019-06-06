package net.script.logic.settings;

import net.script.data.annotations.Coefficient;
import net.script.data.annotations.Function;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.settings.functions.FunctionsSettings;

import java.lang.reflect.Field;
import java.util.HashMap;

public class LinguisticVariableConfigMapper {

    public static SimpleLinguisticVariableSetting getSimpleWithFunction(
            LinguisticVariable variable) {

        QFunction function = variable.getFunction();
        FunctionsSettings.SingleFunctionSetting singleFunctionSetting = null;
        if (function != null) {
            Function funAnn = function.getClass().getAnnotation(Function.class);
            if (funAnn != null) {
                singleFunctionSetting =
                        FunctionsSettings.SingleFunctionSetting
                                .builder()
                                .name(funAnn.value())
                                .coefficients(new HashMap<>())
                                .build();
                for (Field field : function.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Coefficient kpAnn = field.getAnnotation(Coefficient.class);
                    if (kpAnn != null) {
                        Object val;
                        try {
                            val = field.get(function);
                        } catch (IllegalAccessException e) {
                            continue;
                        }
                        if (val instanceof Double) {
                            singleFunctionSetting.getCoefficients().put(kpAnn.value(), (Double) val);
                        }
                    }
                }
            }
        }
        SimpleLinguisticVariableSetting setting = SimpleLinguisticVariableSetting
                .builder()
                .functionSetting(singleFunctionSetting)
                .member(variable.getMemberFieldName())
                .name(variable.getName())
                .rangeStart(variable.getLvRange().getBegin())
                .rangeEnd(variable.getLvRange().getEnd())
                .build();
        return setting;
    }
}
