package io.apptik.rxhub.shield;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ShieldProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Trees trees;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
        messager = processingEnv.getMessager();
        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(NodeTag.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {

        for (Element annotatedElement : env.getElementsAnnotatedWith(NodeTag.class)) {

            if (annotatedElement.getKind() != ElementKind.METHOD) {
                throw new IllegalStateException(
                        String.format("Only methods can be annotated with @%s",
                                NodeTag.class.getSimpleName()));
            }
            if (annotatedElement.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
                throw new IllegalStateException(
                        String.format("Only Interfaces can contain annotation @%s",
                                NodeTag.class.getSimpleName()));
            }

        }

        return true;
    }

    /**
     * Prints an error message
     *
     * @param el   The element which has caused the error. Can be null
     * @param msg  The error message
     * @param args any other args
     */
    private void error(Element el, String msg, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, el, msg, args);
    }

    private void note(Element el, String msg, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, el, msg, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[]
            args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        messager.printMessage(kind, message, element);
    }
}
