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
package com.izikode.izilib.picklesubcompiler;

import com.izikode.izilib.picklesubannotations.ConditionallySubscribe;
import com.izikode.izilib.picklesubannotations.SimplySubscribe;
import com.izikode.izilib.picklesubcompiler.utility.ConditionalFunction;
import com.izikode.izilib.picklesubcompiler.utility.SimpleFunction;
import com.izikode.izilib.picklesubcompiler.utility.SubscriberBuilder;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class PickleSubCompiler extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Arrays.asList(
                SimplySubscribe.class.getCanonicalName(),
                ConditionallySubscribe.class.getCanonicalName()
        ));
    }

    private Filer filer;

    private boolean writeClass(String fileName, String sourceCode) {
        try {

            JavaFileObject fileObject = filer.createSourceFile(fileName);
            Writer writer = fileObject.openWriter();

            writer.write(sourceCode);
            writer.close();

            return true;

        } catch (IOException exception) {

            error(exception);
            return false;

        }
    }

    private Messager messager;

    private void note(String note) {
        messager.printMessage(Diagnostic.Kind.NOTE, note);
    }

    private void warn(String warning) {
        messager.printMessage(Diagnostic.Kind.WARNING, warning);
    }

    private void error(Exception error) {
        messager.printMessage(Diagnostic.Kind.ERROR, error.toString());
    }

    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    private final List<SubscriberBuilder> subscriberBuilders = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processConditionalFunctions(roundEnv);
        processSimpleFunctions(roundEnv);

        if (roundEnv.processingOver()) {
            for (SubscriberBuilder subscriberBuilder : subscriberBuilders) {
                if (writeClass(subscriberBuilder.subscriberClass(), subscriberBuilder.sourceCode())) {
                    note("Generated " + subscriberBuilder.subscriberClass());
                }
            }
        }

        return subscriberBuilders.size() > 0;
    }

    private void processConditionalFunctions(RoundEnvironment roundEnv) {
        for (Element element: roundEnv.getElementsAnnotatedWith(ConditionallySubscribe.class)) {
            SubscriberBuilder subscriberBuilder = getSubscriberBuilder(element);

            String functionName = element.getSimpleName().toString();
            String functionSubscription = element.toString().replace(functionName, "").split("[\\(\\)]")[1];
            String functionConditional = element.getAnnotation(ConditionallySubscribe.class).mustSatisfy();

            subscriberBuilder.add(new ConditionalFunction(
                    functionName, functionConditional, functionSubscription
            ));

            if (!subscriberBuilders.contains(subscriberBuilder)) {
                subscriberBuilders.add(subscriberBuilder);
            }
        }
    }

    private void processSimpleFunctions(RoundEnvironment roundEnv) {
        for (Element element: roundEnv.getElementsAnnotatedWith(SimplySubscribe.class)) {
            SubscriberBuilder subscriberBuilder = getSubscriberBuilder(element);

            String functionName = element.getSimpleName().toString();
            String functionSubscription = element.toString().replace(functionName, "").split("[\\(\\)]")[1];

            subscriberBuilder.add(new SimpleFunction(
                    functionName, functionSubscription
            ));

            if (!subscriberBuilders.contains(subscriberBuilder)) {
                subscriberBuilders.add(subscriberBuilder);
            }
        }
    }

    private SubscriberBuilder getSubscriberBuilder(Element buildElement) {
        return getSubscriberBuilder((TypeElement) buildElement.getEnclosingElement());
    }

    private SubscriberBuilder getSubscriberBuilder(TypeElement sourceClass) {
        SubscriberBuilder newSubscriberBuilder = new SubscriberBuilder(elementUtils.getPackageOf(sourceClass), sourceClass);

        for (SubscriberBuilder subscriberBuilder : subscriberBuilders) {
            if (subscriberBuilder.equals(newSubscriberBuilder) || firstIsSuperclassOfSecond(subscriberBuilder, newSubscriberBuilder)) {

                return subscriberBuilder;

            } else if (firstIsSubclassOfSecond(subscriberBuilder, newSubscriberBuilder)) {

                subscriberBuilder.reAssignFrom(newSubscriberBuilder);
                return subscriberBuilder;

            }
        }

        return newSubscriberBuilder;
    }

    private boolean firstIsSuperclassOfSecond(SubscriberBuilder first, SubscriberBuilder second) {
        TypeMirror firstType = first.sourceElement().getSuperclass();
        TypeMirror secondType = second.sourceElement().asType();

        return typeUtils.isSameType(firstType, secondType);
    }

    private boolean firstIsSubclassOfSecond(SubscriberBuilder first, SubscriberBuilder second) {
        return firstIsSuperclassOfSecond(second, first);
    }

}
