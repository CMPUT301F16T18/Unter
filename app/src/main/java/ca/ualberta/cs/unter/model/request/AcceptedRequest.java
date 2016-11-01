package ca.ualberta.cs.unter.model.request;

import java.util.ArrayList;

/**
 * Request that has been sent by the rider,
 * and also accepted by one or more driver.
 * @see Request
 */
public class AcceptedRequest extends Request {
    public AcceptedRequest(String riderUserName, String driverUserName, double[] originCoordinate, double[] destinationCoordinate, Double estimatedFare) {
        super(riderUserName, driverUserName, originCoordinate, destinationCoordinate, estimatedFare);
    }

    public AcceptedRequest(String riderUserName, ArrayList<String> driverList, double[] originCoordinate, double[] destinationCoordinate, Double estimatedFare) {
        super(riderUserName, driverList, originCoordinate, destinationCoordinate, estimatedFare);
    }
}
