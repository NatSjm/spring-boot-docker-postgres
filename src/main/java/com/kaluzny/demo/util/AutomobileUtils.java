package com.kaluzny.demo.util;

import com.kaluzny.demo.domain.Automobile;

public class AutomobileUtils {
    public static boolean isCherryFerrary(Automobile automobile) {
        return "cherry".equalsIgnoreCase(automobile.getColor()) && "Ferrary".equalsIgnoreCase(automobile.getName());
    }
}