package com.example.firstpage;

public class CarbonUtils {

    private CarbonUtils() {} // Prevent instantiation

    public static float getCarbonEmissionRate(String mode) {
        switch (mode) {
            case "Walking": return 0.00f;
            case "Bicycle": return 0.00f;
            case "Motorcycle": return 0.08f / 1000;
            case "Jeepney": return 0.13f / 1000;
            case "Car": return 0.21f / 1000;
            case "Bus": return 0.06f / 1000;
            case "Truck": return 0.27f / 1000;
            case "Airplane": return 0.25f / 1000;
            default: return 0.21f / 1000;
        }
    }
}


