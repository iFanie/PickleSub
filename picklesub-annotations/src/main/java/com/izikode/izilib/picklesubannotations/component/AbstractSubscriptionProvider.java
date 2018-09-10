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
package com.izikode.izilib.picklesubannotations.component;

import com.squareup.otto.Bus;

public abstract class AbstractSubscriptionProvider implements SubscriptionProvider {

    protected Bus bus;

    public AbstractSubscriptionProvider bus(Bus bus) {
        this.bus = bus;
        return this;
    }

    protected Boolean singleSubscription = false;

    public AbstractSubscriptionProvider withSingleSubscription() {
        singleSubscription = true;
        return this;
    }

    public AbstractSubscriptionProvider reset() {
        bus = null;
        singleSubscription = false;

        return this;
    }

    protected synchronized void unregister(AbstractSubscriber subscriber, Object event) {
        if (bus == null || !singleSubscription) {
            return;
        }

        for (AbstractSubscriber registeredSubscriber : getSubscribers()) {
            bus.unregister(registeredSubscriber);
        }

        reset();

        for (AbstractSubscriber pendingSubscriber : getSubscribers()) {
            if (!pendingSubscriber.equals(subscriber) && pendingSubscriber.consumes(event)) {
                pendingSubscriber.subscribe(event);
            }
        }
    }

}
