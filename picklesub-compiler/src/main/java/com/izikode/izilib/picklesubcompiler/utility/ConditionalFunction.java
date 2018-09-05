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
package com.izikode.izilib.picklesubcompiler.utility;

import java.util.Locale;

public class ConditionalFunction {

    private final static String OTTO_SUBSCRIBE = "@com.squareup.otto.Subscribe";

    private final static String TB = "\t";
    private final static String NL = "\n";

    private final String functionName;
    private final String functionConditional;
    private final String functionSubscription;

    public ConditionalFunction(String functionName, String functionConditional, String functionSubscription) {
        this.functionName = functionName;
        this.functionConditional = functionConditional;
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
                TB + TB + TB + TB + "if (sourceSubscriber." + functionConditional + "(subscription)) {" + NL +
                TB + TB + TB + TB + TB + "sourceSubscriber." + functionName + "(subscription);" + NL +
                TB + TB + TB + TB + "}" + NL +
                TB + TB + TB + "}" + NL +
                NL +
                TB + TB + "};" + NL +
                TB + "}";
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "'%1$s' subscribes to '%2$s' which satisfies '%3$s'",
                functionName, functionSubscription, functionConditional);
    }

}
