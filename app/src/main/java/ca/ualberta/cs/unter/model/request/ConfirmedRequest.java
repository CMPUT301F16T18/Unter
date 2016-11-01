package ca.ualberta.cs.unter.model.request;

/**
 * Reuqest that has been sent by the rider,
 * and accepted by one or more driver. Also,
 * it has been confirmed by the rider as well.
 * @see Request
 */
public class ConfirmedRequest extends Request{
    public ConfirmedRequest(String riderUserName, String driverUserName, double[] originCoordinate, double[] destinationCoordinate, Double estimatedFare) {
        super(riderUserName, driverUserName, originCoordinate, destinationCoordinate, estimatedFare);
    }
}
