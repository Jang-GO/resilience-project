package janggo.practice.calleeservice.circuitbreaker;

import org.springframework.stereotype.Component;

@Component
public class ServiceState {
    private volatile boolean isHealthy = true;

    public boolean isHealthy() {
        return isHealthy;
    }

    public void breakService() {
        this.isHealthy = false;
    }

    public void fixService() {
        this.isHealthy = true;
    }
}
