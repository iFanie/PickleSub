/* Copyright 2018 Fanie Veizis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
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
