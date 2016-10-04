package io.apptik.rhub.shield;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import io.apptik.roxy.Removable;

public class ProxyTagAnnotation {

    final String nodeTag;
    final TypeName returnType;
    final TypeName paramType;

    public ProxyTagAnnotation(String nodeTag, TypeName returnType, TypeName paramType) {
        this.nodeTag = nodeTag;
        this.returnType = returnType;
        this.paramType = paramType;
    }

    public ClassName getRawClassName() {
        if (returnType instanceof ParameterizedTypeName) {
            return ((ParameterizedTypeName) returnType).rawType;
        }
        if(returnType instanceof ClassName) {
            return (ClassName) returnType;
        } else {
            //we got typename instead of a classname
            return null;
        }
    }

    public boolean isInput() {
        return returnType.equals(TypeName.VOID) || isInputWithRemovable();
    }

    public boolean isInputWithRemovable() {
        return ClassName.get(Removable.class).equals(getRawClassName());
    }



    public boolean hasEnclosedClassName() {
        if (returnType instanceof ParameterizedTypeName) {
            return ((ParameterizedTypeName) returnType).typeArguments.get(0) instanceof ClassName;
        }
        return false;
    }

    public ClassName getEnclosedClassName() {
        if (returnType instanceof ParameterizedTypeName) {
            return (ClassName) ((ParameterizedTypeName) returnType).typeArguments.get(0);
        }
        return null;
    }
}
