package com.izikode.izilib.picklesubannotations.utility;

import java.util.Locale;

public class SimpleFunction {

    private final static String OTTO_SUBSCRIBE = "@com.squareup.otto.Subscribe";

    private final static String TB = "\t";
    private final static String NL = "\n";

    private final String functionName;
    private final String functionSubscription;

    public SimpleFunction(String functionName, String functionSubscription) {
        this.functionName = functionName;
        this.functionSubscription = functionSubscription;
    }

    public String getBuilderName(int index) {
        return "getSubscriber" + index + "()";
    }

    public String getBuilderSourceCode(int index, String sourceSubscriberClass, String sourceSubscriberInstance) {
        return  TB + "private Object " + getBuilderName(index) + " {" + NL +
                TB + TB + "return new Object() {" + NL +
                NL +
                TB + TB + TB + "private final " + sourceSubscriberClass + " sourceSubscriber = " + sourceSubscriberInstance + ";" + NL +
                NL +
                TB + TB + TB + OTTO_SUBSCRIBE + NL +
                TB + TB + TB + "public void subscribe(" + functionSubscription + " subscription) {" + NL +
                TB + TB + TB + TB + "sourceSubscriber." + functionName + "(subscription);" + NL +
                TB + TB + TB + "}" + NL +
                NL +
                TB + TB + "};" + NL +
                TB + "}";
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "'%1$s' subscribes to '%2$s'",
                functionName, functionSubscription);
    }

}
