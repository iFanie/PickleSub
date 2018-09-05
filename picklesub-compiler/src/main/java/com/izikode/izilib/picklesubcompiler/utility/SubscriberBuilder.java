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

import com.izikode.izilib.picklesubannotations.SubscriptionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class SubscriberBuilder {

    private final static String PROVIDER_INTERFACE = SubscriptionProvider.class.getName();

    private final static String TB = "\t";
    private final static String NL = "\n";

    private final static String SOURCE_INSTANCE = "sourceInstance";
    private final static String SUBSCRIBERS = "subscribers";

    private PackageElement sourcePackage;
    private TypeElement sourceClass;

    private String buildPackage;
    private String buildSource;
    private String buildSubscriber;

    public SubscriberBuilder(PackageElement sourcePackage, TypeElement sourceClass) {
        this.sourcePackage = sourcePackage;
        this.sourceClass = sourceClass;

        assignFrom(sourcePackage.getQualifiedName().toString(), sourceClass.getSimpleName().toString());
    }

    public void assignFrom(String buildPackage, String buildSource) {
        this.buildPackage = buildPackage;
        this.buildSource = buildSource;
        this.buildSubscriber = buildSource + "Subscriber";
    }

    public void reAssignFrom(SubscriberBuilder subscriber) {
        sourcePackage = subscriber.sourcePackage;
        sourceClass = subscriber.sourceClass;

        assignFrom(subscriber.buildPackage, subscriber.buildSource);
    }

    public String sourceClass() {
        return buildPackage + "." + buildSource;
    }

    public TypeElement sourceElement() {
        return sourceClass;
    }

    public String subscriberClass() {
        return buildPackage + "." + buildSubscriber;
    }

    private final List<ConditionalFunction> conditionalFunctions = new ArrayList<>();

    public void add(ConditionalFunction conditionalFunction) {
        conditionalFunctions.add(conditionalFunction);
    }

    private final List<SimpleFunction> simpleFunctions = new ArrayList<>();

    public void add(SimpleFunction simpleFunction) {
        simpleFunctions.add(simpleFunction);
    }

    public String sourceCode() {
        return  "package " + buildPackage + ";" + NL +
                NL +
                "public class " + buildSubscriber + " implements " + PROVIDER_INTERFACE + " {" + NL +
                NL +
                TB + "private final " + sourceClass() + " " + SOURCE_INSTANCE + ";" + NL +
                NL +
                TB + "public " + buildSubscriber + "(" + sourceClass() + " " + SOURCE_INSTANCE + ") {" + NL +
                TB + TB + "this." + SOURCE_INSTANCE + " = " + SOURCE_INSTANCE + ";" + NL +
                TB + "}" + NL +
                NL +
                getProviderImplementationSourceCode() + NL +
                NL +
                getInitializerSourceCode() + NL +
                NL +
                getFunctionBuildersSourceCode() +
                "}" + NL;
    }

    public String getProviderImplementationSourceCode() {
        return  TB + "private Object[] " + SUBSCRIBERS + " = null;" + NL +
                NL +
                TB + "@Override" + NL +
                TB + "public Object[] getSubscribers() {" + NL +
                TB + TB + "if (" + SUBSCRIBERS + " == null) {" + NL +
                TB + TB + TB + SUBSCRIBERS + " = initializeSubscribers();" + NL +
                TB + TB + "}" + NL +
                NL +
                TB + TB + "return " + SUBSCRIBERS + ";" + NL +
                TB + "}";
    }

    private String getInitializerSourceCode() {
        StringBuilder providerBuilder = new StringBuilder(TB).append("private Object[] initializeSubscribers() {").append(NL)
                .append(TB).append(TB).append("return new Object[] { ");

        int index = 0;

        for (ConditionalFunction conditionalFunction : conditionalFunctions) {
            providerBuilder.append(conditionalFunction.getBuilderName(index)).append(", ");
            index++;
        }

        for (SimpleFunction simpleFunction : simpleFunctions) {
            providerBuilder.append(simpleFunction.getBuilderName(index)).append(", ");
            index++;
        }

        providerBuilder.setLength(providerBuilder.length() - 2);

        providerBuilder.append(" };").append(NL)
                .append(TB).append("}");

        return providerBuilder.toString();
    }

    private String getFunctionBuildersSourceCode() {
        int index = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (ConditionalFunction conditionalFunction : conditionalFunctions) {
            stringBuilder.append(conditionalFunction.getBuilderSourceCode(
                    index, sourceClass(), SOURCE_INSTANCE
            )).append(NL).append(NL);

            index++;
        }

        for (SimpleFunction simpleFunction : simpleFunctions) {
            stringBuilder.append(simpleFunction.getBuilderSourceCode(
                    index, sourceClass(), SOURCE_INSTANCE
            )).append(NL).append(NL);

            index++;
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SubscriberBuilder &&
                ((SubscriberBuilder) obj).buildPackage.equals(buildPackage) &&
                ((SubscriberBuilder) obj).buildSource.equals(buildSource);
    }

    @Override
    public String toString() {
        StringBuilder conditionals = new StringBuilder();

        for (ConditionalFunction conditionalFunction : conditionalFunctions) {
            conditionals.append(conditionalFunction.toString());
            conditionals.append("\n");
        }

        return String.format(Locale.ENGLISH, "In '%1$s', from '%2$s' will build '%3$s', with\n%4$s and\n",
                buildPackage, buildSource, buildSubscriber, conditionals.toString());
    }

}
