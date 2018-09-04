package com.izikode.izilib.picklesub;

import com.izikode.izilib.picklesubannotations.SubscriptionProvider;
import com.squareup.otto.Bus;

public class PickleSub {

    public static void register(SubscriptionProvider subscriptionProvider, Bus bus) {
        for (Object subscriber : subscriptionProvider.getSubscribers()) {
            bus.register(subscriber);
        }
    }

    public static void unregister(SubscriptionProvider subscriptionProvider, Bus bus) {
        for (Object subscriber : subscriptionProvider.getSubscribers()) {
            bus.unregister(subscriber);
        }
    }

}
