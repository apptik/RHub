package io.apptik.rhub.shield;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;

import io.apptik.rhub.RHub;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

class ShieldClass<H extends RHub> {


    private final ClassName shieldImplName;
    private final ClassName shieldInterfaceName;
    private final Class<H> hubClass;
    public final Map<ExecutableElement, ProxyTagAnnotation> nodes = new LinkedHashMap<>();

    public ShieldClass(ClassName shieldImplName, ClassName shieldInterfaceName,
                       Class<H> hubClass) {
        this.shieldImplName = shieldImplName;
        this.shieldInterfaceName = shieldInterfaceName;
        this.hubClass = hubClass;
    }


    public JavaFile generateShield() {
        return JavaFile.builder(shieldImplName.packageName(), generateShieldImpl())
                .addFileComment("Generated code from RHub. Do not modify!")
                .build();
    }

    private TypeSpec generateShieldImpl() {
        TypeSpec.Builder res = TypeSpec.classBuilder(shieldImplName.simpleName())
                .addModifiers(PUBLIC);

        res.addSuperinterface(shieldInterfaceName);
        res.addField(ClassName.get(hubClass), "rHub", PRIVATE, FINAL);
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(PUBLIC);
        constructor.addParameter(ClassName.get(hubClass), "rHub");
        constructor.addCode(CodeBlock.builder().addStatement("this.rHub=rHub").build());
        res.addMethod(constructor.build());

        for (Map.Entry<ExecutableElement, ProxyTagAnnotation> entry : nodes.entrySet()) {
            ExecutableElement methodEl = entry.getKey();
            ProxyTagAnnotation na = entry.getValue();
            MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(
                    methodEl.getSimpleName().toString());
            methodSpec.addModifiers(PUBLIC);
            methodSpec.returns(ClassName.get(methodEl.getReturnType()));
            if (na.isInput()) {
                methodSpec.addParameter(ParameterSpec.builder(na.paramType, "src").build());
                methodSpec.addCode(CodeBlock.builder()
                        .addStatement((na.isInputWithRemovable() ? "return " : "")
                                + "rHub.addUpstream($S,$L)", na.nodeTag, "src").build());
            } else {
                if (na.hasEnclosedClassName()) {
                    methodSpec.addCode(CodeBlock.builder()
                            .addStatement("return rHub.getPub($S,$T.class)",
                                    na.nodeTag,
                                    na.getEnclosedClassName())
                            .build());
                } else {
                    methodSpec.addCode(CodeBlock.builder()
                            .addStatement("return rHub.getPub($S)",
                                    na.nodeTag).build());
                }
            }

            res.addMethod(methodSpec.build());
        }

        return res.build();

    }
}
