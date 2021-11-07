package org.ivankov.study.akka.D_graphdsl;

/**
 * @author Alexandr Ivankov on 2021-10-31
 */
public class VehicleSpeed {
    private int vehicleId;
    private double speed;

    public VehicleSpeed(int vehicleId, double speed) {
        this.vehicleId = vehicleId;
        this.speed = speed;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public double getSpeed() {
        return speed;
    }
}
