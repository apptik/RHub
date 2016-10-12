package io.apptik.rhub.shield;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
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
import io.apptik.roxy.Removable;

/**
 * Base processor to generate Shield implementations for specific Hub.
 * <br>
 * Normally one will not use this directly but rather
 * more specific helpers based on some concrete Reactive Framework.
 * @param <H> The specific type of the Hub
 * @param <P> The specific type of the Publishers the Hub accepts and returns
 */
public abstract class ShieldProcessor<H extends RHub<? extends P>, P> extends
        AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Trees trees;
    private Messager messager;


    abstract Class<H> hubClass();

    abstract Set<Class<? extends P>> pubClass();

    Set<String> pubClassStrings = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        for (Class<? extends P> pClass : pubClass()) {
            pubClassStrings.add(pClass.getName());
        }
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
        messager = processingEnv.getMessager();
        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private boolean knowsClass(String clz) {
        return pubClassStrings.contains(clz);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(ProxyTag.class);
        return annotations;
    }

    @Override
    public final boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
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

        return false;
    }

    private Map<TypeElement, ShieldClass> findAndParseNodes(RoundEnvironment env) {
        Map<TypeElement, ShieldClass> shields = new LinkedHashMap<>();
        for (Element annotatedElement : env.getElementsAnnotatedWith(ProxyTag.class)) {
            checkIfOK1(annotatedElement);
            ExecutableElement annotatedNode = (ExecutableElement) annotatedElement;
            if (checkIfOK2(annotatedNode)) {
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
        }

        return shields;
    }

    private boolean checkIfOK2(ExecutableElement annotatedNode) {
        if (annotatedNode.getReturnType().getKind().equals(TypeKind.VOID)
                || typeUtils.isSameType(annotatedNode.getReturnType(),
                elementUtils.getTypeElement(Removable.class.getCanonicalName()).asType())) {
            if (annotatedNode.getParameters().size() > 1 ||
                    annotatedNode.getParameters().size() < 1) {
                throw new IllegalStateException(
                        String.format("void methods must accept exactly 1 param of returnType " +
                                        " to be annotated with @%s. Got : %s",
                                //pubClass,
                                ProxyTag.class.getSimpleName(),
                                annotatedNode.getParameters()

                        ));
            }
            if (!knowsClass(typeUtils.erasure(annotatedNode.getParameters().get(0).asType())
                    .toString())) {
                warn(annotatedNode, "void methods must accept exactly 1 param of returnType " +
                                " to be annotated with @%s. Got : %s",
                        //pubClass,
                        ProxyTag.class.getSimpleName(),
                        typeUtils.erasure(annotatedNode.getParameters().get(0).asType()));
                return false;
//                throw new IllegalStateException(
//                        String.format("void methods must accept exactly 1 param of returnType " +
//                                        "%s to be annotated with @%s. Got : %s",
//                                pubClass,
//                                ProxyTag.class.getSimpleName(),
//                                typeUtils.erasure(annotatedNode.getParameters().get(0).asType())
//
//                        ));
            }
        } else if (knowsClass(typeUtils.erasure(annotatedNode.getReturnType()).toString())) {
            if (annotatedNode.getParameters().size() > 0) {
                throw new IllegalStateException(
                        String.format("Methods that return  must have 0 " +
                                        "params to be annotated with @%s. Got : %s",
                                //pubClass,
                                ProxyTag.class.getSimpleName(),
                                annotatedNode.getReturnType()

                        ));
            }
        } else {
            //just ignore and warn
            warn(annotatedNode, "Only void methods or that return can " +
                            "be annotated with @%s. Got : %s",
                    //pubClass,
                    ProxyTag.class.getSimpleName(),
                    annotatedNode.getReturnType());
            return false;
//            throw new IllegalStateException(
//                    String.format("Only void methods or that return %s can " +
//                                    "be annotated with @%s. Got : %s",
//                            pubClass,
//                            ProxyTag.class.getSimpleName(),
//                            annotatedNode.getReturnType()
//
//                    ));
        }

        return true;
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

    private void warn(Element el, String msg, Object... args) {
        printMessage(Diagnostic.Kind.WARNING, el, msg, args);
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
