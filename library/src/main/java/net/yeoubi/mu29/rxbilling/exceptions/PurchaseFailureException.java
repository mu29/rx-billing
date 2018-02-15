package net.yeoubi.mu29.rxbilling.exceptions;

/**
 * @author InJung Chung
 */
public class PurchaseFailureException extends Throwable {

    private int code;

    public PurchaseFailureException(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return "Purchase failed with response code " + code;
    }
}
