# rx-billing
RxJava bindings for Google Play Billing

# Installation

Your top-level `build.gradle`:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

In module-level `build.gradle`:

```
dependencies {
    implementation 'com.github.mu29:rx-billing:0.5'
}
```

# Usage

### Initialize RxBilling Instance

```java

RxBilling billingClient;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    billingClient = new RxBilling().set(this);
}
```

...or you can inject with dagger

```java
@Provides
@PerApplication
public RxBilling provideRxBilling() {
    return new RxBilling()
}
```

```java
@Inject
RxBilling billingClient;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    billingClient.set(this);
}
```

### Purchase

```java
billingClient
    .purchase("product.test.1", null, BillingClient.SkuType.INAPP)
    .subscribe(purchases -> Log.d("IAP", purchases.get(0).getOrderId()));
```

### Query purchases

```java
billingClient
    .queryPurchases()
    .subscribe(purchases -> {
        for (Purchase purchase : purchases) {
            Log.d("IAP", purchase.getOrderId());
        }
    });
```

### Get sku details

```java
List<String> ids = new ArrayList<>();
ids.add("test.product.1");

billingClient
    .getSkuDetails(ids)
    .subscribe(details -> {
        for (SkuDetails detail: details) {
            Log.d("IAP", detail.getPrice());
        }
    });
```

### Consume

```java
billingClient
    .consume("purchaseToken")
    .subscribe(token -> Log.d("IAP", token));
```

## Author

InJung Chung / [@mu29](http://mu29.github.io/)

## License

[MIT](https://github.com/mu29/rx-billing/blob/master/LICENSE)
