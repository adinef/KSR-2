package net.script.config.main;

import java.util.ArrayList;
import java.util.List;

public class ApplicationVariables {
    public static final List<String> INIT_VALUES = new ArrayList<>();
    public static boolean determineRuntimeConfig() {
        return INIT_VALUES.contains("runtime");
    }
}
