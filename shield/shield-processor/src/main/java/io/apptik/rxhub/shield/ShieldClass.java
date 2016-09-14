package io.apptik.rxhub.shield;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;

import io.apptik.rxhub.RxJava1Hub;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public class ShieldClass {

    private final ClassName shieldImplName;
    private final ClassName shieldInterfaceName;
    public final Map<ExecutableElement, NodeAnnotation> nodes = new LinkedHashMap<>();

    public ShieldClass(ClassName shieldImplName, ClassName shieldInterfaceName) {
        this.shieldImplName = shieldImplName;
        this.shieldInterfaceName = shieldInterfaceName;
    }


    public JavaFile generateShield() {
        return JavaFile.builder(shieldImplName.packageName(), generateShieldImpl())
                .addFileComment("Generated code from RxHub. Do not modify!")
                .build();
    }

    private TypeSpec generateShieldImpl() {
        TypeSpec.Builder res = TypeSpec.classBuilder(shieldImplName.simpleName())
                .addModifiers(PUBLIC);

        res.addSuperinterface(shieldInterfaceName);

        res.addField(ClassName.get(RxJava1Hub.class), "rxHub", PRIVATE, FINAL);
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC);
        constructor.addParameter(ClassName.get(RxJava1Hub.class), "rxHub");
        constructor.addCode(CodeBlock.builder().addStatement("this.rxHub=rxHub").build());
        res.addMethod(constructor.build());

        for (Map.Entry<ExecutableElement, NodeAnnotation> entry : nodes.entrySet()) {
            ExecutableElement methodEl = entry.getKey();
            NodeAnnotation na = entry.getValue();
            MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(
                    methodEl.getSimpleName().toString());
            methodSpec.addModifiers(PUBLIC);
            methodSpec.returns(ClassName.get(methodEl.getReturnType()));
            if (na.isInputNode()) {
                methodSpec.addParameter(ParameterSpec.builder(na.paramType, "src").build());
                methodSpec.addCode(CodeBlock.builder()
                        .addStatement("rxHub.addObservable($S,$L)",
                                na.nodeTag,"src").build());
            } else {
                if (na.hasEnclosedClassName()) {
                    methodSpec.addCode(CodeBlock.builder()
                            .addStatement("return rxHub.getFilteredObservable($S,$T.class)",
                                    na.nodeTag,
                                    na.getEnclosedClassName())
                            .build());
                } else {
                    methodSpec.addCode(CodeBlock.builder()
                            .addStatement("return rxHub.getObservable($S)",
                                    na.nodeTag).build());
                }
            }

            res.addMethod(methodSpec.build());
        }

        return res.build();

    }
}
