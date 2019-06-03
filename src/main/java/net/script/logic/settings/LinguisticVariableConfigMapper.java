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

        SimpleLinguisticVariableSetting setting = new SimpleLinguisticVariableSetting();
        QFunction function = variable.getFunction();
        if (function != null) {
            Function funAnn = function.getClass().getAnnotation(Function.class);
            if (funAnn != null) {
                FunctionsSettings.SingleFunctionSetting singleFunctionSetting =
                        new FunctionsSettings.SingleFunctionSetting();
                singleFunctionSetting.setName(funAnn.value());
                singleFunctionSetting.setCoefficients(new HashMap<>());
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
                setting.setFunctionSetting(singleFunctionSetting);
            }
        }
        setting.setMember(variable.getMemberFieldName());
        setting.setName(variable.getName());
        setting.setRangeStart(variable.getLvRange().getBegin());
        setting.setRangeEnd(variable.getLvRange().getEnd());
        return setting;
    }
}
