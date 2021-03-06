package net.yeoubi.rxbilling;

import android.app.Activity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import net.yeoubi.rxbilling.exceptions.ConsumeFailureException;
import net.yeoubi.rxbilling.exceptions.QueryPurchaseFailureException;
import net.yeoubi.rxbilling.exceptions.SkuDetailsFailureException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author InJung Chung
 */
public class RxBilling {

    private Activity activity;
    private BillingClient client;
    private PurchaseListener purchaseListener = new PurchaseListener();

    public RxBilling set(Activity activity) {
        this.activity = activity;
        client = BillingClient.newBuilder(activity).setListener(purchaseListener).build();
        return this;
    }

    public Single<List<Purchase>> purchase(
        String skuId,
        ArrayList<String> oldSkus,
        @BillingClient.SkuType String billingType
    ) {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> {
                    purchaseListener.setPurchaseEmitter(emitter);
                    BillingFlowParams params = BillingFlowParams.newBuilder()
                        .setSku(skuId)
                        .setType(billingType)
                        .setOldSkus(oldSkus)
                        .build();
                    client.launchBillingFlow(activity, params);
                },
                emitter::onError
            )
        );
    }

    public Single<List<Purchase>> queryPurchases() {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> {
                    Purchase.PurchasesResult result = client.queryPurchases(BillingClient.SkuType.INAPP);

                    if (areSubscriptionsSupported()) {
                        Purchase.PurchasesResult subscriptionResult = client.queryPurchases(BillingClient.SkuType.SUBS);
                        if (subscriptionResult.getResponseCode() == BillingClient.BillingResponse.OK) {
                            result.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                        }
                    }

                    int code = result.getResponseCode();
                    if (code == BillingClient.BillingResponse.OK) {
                        emitter.onSuccess(result.getPurchasesList());
                    } else {
                        emitter.onError(new QueryPurchaseFailureException(code));
                    }
                },
                emitter::onError
            )
        );
    }

    public Single<List<SkuDetails>> getSkuDetails(List<String> skuList) {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> {
                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                        .setSkusList(skuList)
                        .setType(BillingClient.SkuType.INAPP)
                        .build();

                    client.querySkuDetailsAsync(params, (code, result) -> {
                        if (code == BillingClient.BillingResponse.OK) {
                            emitter.onSuccess(result);
                        } else {
                            emitter.onError(new SkuDetailsFailureException(code));
                        }
                    });
                },
                emitter::onError
            )
        );
    }

    public Single<String> consume(String token) {
        return Single.create(emitter ->
            tryConnect().subscribe(
                () -> client.consumeAsync(token, (code, purchaseToken) -> {
                    if (code == BillingClient.BillingResponse.OK) {
                        emitter.onSuccess(purchaseToken);
                    } else {
                        emitter.onError(new ConsumeFailureException(code));
                    }
                }),
                emitter::onError
            )
        );
    }

    private Completable tryConnect() {
        return Completable.create(emitter -> {
            if (client.isReady()) {
                emitter.onComplete();
                return;
            }

            client.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(int responseCode) {
                    emitter.onComplete();
                }

                @Override
                public void onBillingServiceDisconnected() {}
            });
        });
    }

    private boolean areSubscriptionsSupported() {
        int responseCode = client.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        return responseCode == BillingClient.BillingResponse.OK;
    }
}
