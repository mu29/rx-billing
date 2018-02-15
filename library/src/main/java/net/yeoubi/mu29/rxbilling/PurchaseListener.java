package net.yeoubi.mu29.rxbilling;

import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import net.yeoubi.mu29.rxbilling.exceptions.PurchaseFailureException;
import net.yeoubi.mu29.rxbilling.exceptions.PurchaseFlowInterruptException;

import java.util.List;

import io.reactivex.SingleEmitter;

/**
 * @author InJung Chung
 */
class PurchaseListener implements PurchasesUpdatedListener {

    private SingleEmitter<List<Purchase>> emitter = null;

    void setPurchaseEmitter(SingleEmitter<List<Purchase>> newEmitter) {
        if (emitter != null && !emitter.isDisposed()) {
            emitter.onError(new PurchaseFlowInterruptException());
        }

        emitter = newEmitter;
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (emitter == null || emitter.isDisposed()) {
            return;
        }

        if (responseCode == BillingClient.BillingResponse.OK) {
            emitter.onSuccess(purchases);
        } else {
            emitter.onError(new PurchaseFailureException(responseCode));
        }
    }
}
