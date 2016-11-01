package ca.ualberta.cs.unter.model.request;

/**
 * Request that has been completed in the past.
 * @see Request
 */
public class CompletedRequest extends Request{
    public CompletedRequest(String riderUserName, String driverUserName, double[] originCoordinate, double[] destinationCoordinate, Double estimatedFare) {
        super(riderUserName, driverUserName, originCoordinate, destinationCoordinate, estimatedFare);
    }
}
