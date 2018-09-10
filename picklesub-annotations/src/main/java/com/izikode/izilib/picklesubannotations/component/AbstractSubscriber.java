package com.izikode.izilib.picklesubannotations.component;

public abstract class AbstractSubscriber<Event> {

    private final Class<Event> eventClass;

    public AbstractSubscriber(Class<Event> eventClass) {
        this.eventClass = eventClass;
    }

    public boolean consumes(Object event) {
        return event.getClass().isAssignableFrom(eventClass);
    }

    @com.squareup.otto.Subscribe
    public abstract void subscribe(Event subscription);

}
