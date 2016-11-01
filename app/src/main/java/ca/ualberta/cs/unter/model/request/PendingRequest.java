package ca.ualberta.cs.unter.model.request;

/**
 * Reuqest that has been sent by the rider,
 * but has not been accepted by any driver.
 * @see Request
 */
public class PendingRequest extends Request {

    /**
     * Constructor for pending request
     * @param riderUserName rider's user name
     * @param originCoordinate rider's pickup location coordinate
     * @param destinationCoordinate rider's destination coordinate
     */
    public PendingRequest(String riderUserName, double[] originCoordinate, double[] destinationCoordinate) {
        super(riderUserName, originCoordinate, destinationCoordinate);
    }
}
