package net.yeoubi.mu29.rxbilling.exceptions;

/**
 * @author InJung Chung
 */
public class BillingServiceConnectionRefuseException extends Throwable {
    @Override
    public String getMessage() {
        return "Service cannot be connected.";
    }
}
