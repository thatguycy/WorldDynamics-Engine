package com.thatguycy.worlddynamicsengine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GovernmentType {
    private static final Set<String> TYPES = new HashSet<>();

    public static void loadTypes(Set<String> types) {
        TYPES.clear();
        TYPES.addAll(types);
    }

    public static Set<String> getTypes() {
        return Collections.unmodifiableSet(TYPES);
    }

    public static boolean isValidType(String type) {
        return TYPES.contains(type);
    }
}