package com.example.rickmortyexplorer.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.example.rickmortyexplorer.model.DeviceStatus;

public class BatteryUtils {
    private BatteryUtils() {
    }

    public static DeviceStatus readDeviceStatus(Context context) {
        Intent batteryStatus = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryStatus == null) {
            return new DeviceStatus(0, false, "No se pudo leer la bateria.", System.currentTimeMillis());
        }

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryLevel = scale > 0 ? Math.round(level * 100f / scale) : 0;

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
                || plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;

        return new DeviceStatus(
                batteryLevel,
                charging,
                buildRecommendation(batteryLevel, charging),
                System.currentTimeMillis()
        );
    }

    private static String buildRecommendation(int batteryLevel, boolean charging) {
        if (charging) {
            return "Dispositivo cargando: puedes sincronizar favoritos y consultar el backend.";
        }
        if (batteryLevel <= 20) {
            return "Bateria baja: usa la cache local y evita sincronizaciones intensivas.";
        }
        if (batteryLevel <= 50) {
            return "Bateria media: modo equilibrado, consulta datos remotos cuando sea necesario.";
        }
        return "Bateria suficiente: exploracion online recomendada.";
    }
}
