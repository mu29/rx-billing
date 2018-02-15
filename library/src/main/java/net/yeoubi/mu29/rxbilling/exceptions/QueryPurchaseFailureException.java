package net.yeoubi.mu29.rxbilling.exceptions;

/**
 * @author InJung Chung
 */
public class QueryPurchaseFailureException extends Throwable {

    private int code;

    public QueryPurchaseFailureException(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return "Purchase failed with response code " + code;
    }
}
