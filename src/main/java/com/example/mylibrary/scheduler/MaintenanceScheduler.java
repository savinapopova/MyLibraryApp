package com.example.mylibrary.scheduler;

import com.example.mylibrary.interceptor.MaintenanceInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MaintenanceScheduler {

    private final MaintenanceInterceptor maintenanceInterceptor;

    public MaintenanceScheduler(MaintenanceInterceptor maintenanceInterceptor) {
        this.maintenanceInterceptor = maintenanceInterceptor;
    }

    @Scheduled(cron = "0 3 9 * * THU")
    public void activateMaintenance() {
        System.out.println("MAINTENANCE ON");
        maintenanceInterceptor.activateMaintenanceMode();
    }

    @Scheduled(cron = "0 5 9 * * THU")
    public void deactivateMaintenance() {
        System.out.println("MAINTENANCE OFF");
        maintenanceInterceptor.deactivateMaintenanceMode();
    }


}
