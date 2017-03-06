package com.liyeyu.viewfinder_compiler.model;

import com.liyeyu.viewfinder_annotation.OnClick;
import com.liyeyu.viewfinder_compiler.TypeUtil;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

/**
 * Created by Liyeyu on 2017/3/6.
 */

public class OnClickMethod {

    private final ExecutableElement mMethodElement;
    private final int[] mResIds;

    public OnClickMethod(Element element) {
        if(element.getKind()!= ElementKind.METHOD){
            throw new IllegalArgumentException(String.format("Only method can be annotated with @%s", OnClick.class.getSimpleName()));
        }
        mMethodElement = (ExecutableElement) element;
        mResIds = mMethodElement.getAnnotation(OnClick.class).value();

        if(mResIds==null){
            throw new IllegalArgumentException(String.format("Must set valid ids for @%s", OnClick.class.getSimpleName()));
        }else {
            for (int id:mResIds) {
                if(id<0){
                    throw new IllegalArgumentException(String.format("Must set valid id for @%s", OnClick.class.getSimpleName()));
                }
            }
        }


        List<? extends VariableElement> parameters = getMethodParameters();
        if(parameters.size()!=1 && !TypeName.get(parameters.get(0).asType()).equals(TypeUtil.ANDROID_VIEW)){
            throw new IllegalArgumentException(
                    String.format("The method annotated with @%s must have no parameters", OnClick.class.getSimpleName()));
        }
    }

    public Name getMethodName() {
        return mMethodElement.getSimpleName();
    }

    public List<? extends VariableElement> getMethodParameters() {
        return mMethodElement.getParameters();
    }

    public int[] getResIds() {
        return mResIds;
    }

}
