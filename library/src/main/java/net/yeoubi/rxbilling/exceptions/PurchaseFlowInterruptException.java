package net.yeoubi.rxbilling.exceptions;

/**
 * @author InJung Chung
 */
public class PurchaseFlowInterruptException extends Throwable {
    @Override
    public String getMessage() {
        return "Other purchase flow is initiated.";
    }
}
