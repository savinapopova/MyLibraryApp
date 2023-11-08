package com.example.mylibrary.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.Locale;
import java.util.Map;

@Component
public class MaintenanceInterceptor implements HandlerInterceptor {

    private boolean isMaintenanceMode = false;
    private ThymeleafViewResolver tlvr;

    public MaintenanceInterceptor(ThymeleafViewResolver tlvr) {
        this.tlvr = tlvr;
    }

    public void activateMaintenanceMode() {
        isMaintenanceMode = true;
    }

    public void deactivateMaintenanceMode() {
        isMaintenanceMode = false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isMaintenanceMode) {

                View blockedView = tlvr.resolveViewName("maintenance", Locale.getDefault());
                if (blockedView != null) {
                    blockedView.render(Map.of(), request, response);
                }
                return false;
            }

            return true;

    }
}
