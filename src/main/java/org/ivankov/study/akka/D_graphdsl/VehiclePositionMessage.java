package org.ivankov.study.akka.D_graphdsl;

import java.util.Date;

/**
 * @author Alexandr Ivankov on 2021-10-31
 */
public class VehiclePositionMessage {

    private int vehicleId;
    private Date currentDateTime;
    private int longPosition;
    private int latPosition;

    public VehiclePositionMessage(int vehicleId, Date currentDateTime, int longPosition, int latPosition) {
        this.vehicleId = vehicleId;
        this.currentDateTime = currentDateTime;
        this.longPosition = longPosition;
        this.latPosition = latPosition;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public Date getCurrentDateTime() {
        return currentDateTime;
    }

    public int getLongPosition() {
        return longPosition;
    }

    public int getLatPosition() {
        return latPosition;
    }
}
