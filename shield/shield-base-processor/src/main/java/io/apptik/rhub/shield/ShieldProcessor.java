package io.apptik.rhub.shield;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import io.apptik.rhub.RHub;

public abstract class ShieldProcessor<H extends RHub<P>, P> extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Trees trees;
    private Messager messager;


    abstract Class<H> hubClass();
    abstract Class<P> pubClass();


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
        annotations.add(ProxyTag.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        Map<TypeElement, ShieldClass> shields = findAndParseNodes(env);
        for (Map.Entry<TypeElement, ShieldClass> entry : shields.entrySet()) {
            TypeElement typeElement = entry.getKey();
            ShieldClass shieldClass = entry.getValue();
            JavaFile javaFile = shieldClass.generateShield();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                error(typeElement, "Error writing Shield Impl for %s: %s", typeElement,
                        e.getMessage());
            }
        }

        return true;
    }

    private Map<TypeElement, ShieldClass> findAndParseNodes(RoundEnvironment env) {
        Map<TypeElement, ShieldClass> shields = new LinkedHashMap<>();
        for (Element annotatedElement : env.getElementsAnnotatedWith(ProxyTag.class)) {
            checkIfOK1(annotatedElement);
            ExecutableElement annotatedNode = (ExecutableElement) annotatedElement;
            checkIfOK2(annotatedNode);
            TypeElement shieldInterface = (TypeElement) annotatedElement.getEnclosingElement();
            ShieldClass shieldClass = shields.get(shieldInterface);
            if (shieldClass == null) {
                String packageName = getPackageName(shieldInterface);
                String className = getClassName(shieldInterface, packageName);
                ClassName shiledInterfaceName = ClassName.get(packageName, className);
                ClassName shieldImplName = ClassName.get(packageName, className + "_Impl");

                shieldClass = new ShieldClass(shieldImplName, shiledInterfaceName, hubClass());
                shields.put(shieldInterface, shieldClass);
            }
            TypeName param = null;
            if (annotatedNode.getParameters().size() > 0) {
                param = TypeName.get(annotatedNode.getParameters().get(0).asType());
            }
            ProxyTagAnnotation na = new ProxyTagAnnotation(
                    annotatedNode.getAnnotation(ProxyTag.class).value(),
                    TypeName.get(annotatedNode.getReturnType()), param);
            shieldClass.nodes.put(annotatedNode, na);
        }

        return shields;
    }

    private void checkIfOK2(ExecutableElement annotatedNode) {
        if (typeUtils.erasure(annotatedNode.getReturnType()).toString()
                .equals(pubClass().getName())) {
            if (annotatedNode.getParameters().size() > 0) {
                throw new IllegalStateException(
                        String.format("Methods that return Observable must have 0 " +
                                        "params to be annotated with @%s. Got : %s",
                                ProxyTag.class.getSimpleName(),
                                annotatedNode.getReturnType()

                        ));
            }
        } else if (annotatedNode.getReturnType().getKind().equals(TypeKind.VOID)) {
            if (annotatedNode.getParameters().size() > 1 ||
                    annotatedNode.getParameters().size() < 1) {
                throw new IllegalStateException(
                        String.format("void methods must accept exactly 1 param of returnType " +
                                        "Observable to be annotated with @%s. Got : %s",
                                ProxyTag.class.getSimpleName(),
                                annotatedNode.getParameters()

                        ));
            }
            if (!typeUtils.erasure(annotatedNode.getParameters().get(0).asType()).toString()
                    .equals(pubClass().getName())) {
                throw new IllegalStateException(
                        String.format("void methods must accept exactly 1 param of returnType " +
                                        "Observable to be annotated with @%s. Got : %s",
                                ProxyTag.class.getSimpleName(),
                                typeUtils.erasure(annotatedNode.getParameters().get(0).asType())

                        ));
            }
        } else {
            throw new IllegalStateException(
                    String.format("Only void methods or that return Observable can " +
                                    "be annotated with @%s. Got : %s",
                            ProxyTag.class.getSimpleName(),
                            annotatedNode.getReturnType()

                    ));
        }
    }

    private void checkIfOK1(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            throw new IllegalStateException(
                    String.format("Only methods can be annotated with @%s",
                            ProxyTag.class.getSimpleName()));
        }
        if (annotatedElement.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
            throw new IllegalStateException(
                    String.format("Only Interfaces can contain annotation @%s",
                            ProxyTag.class.getSimpleName()));
        }
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
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
