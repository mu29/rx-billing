package net.yeoubi.rxbilling;

import android.support.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import net.yeoubi.rxbilling.exceptions.PurchaseFailureException;

import java.util.List;

import io.reactivex.SingleEmitter;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author InJung Chung
 */
class PurchaseListener implements PurchasesUpdatedListener {

    private Disposable disposable = new CompositeDisposable();
    private SingleEmitter<List<Purchase>> emitter = null;

    void setPurchaseEmitter(SingleEmitter<List<Purchase>> newEmitter) {
        if (emitter != null && !emitter.isDisposed()) {
            disposable.dispose();
        }

        emitter = newEmitter;
        emitter.setDisposable(disposable);
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
