package com.liyeyu.viewfinder_compiler.model;

import com.liyeyu.viewfinder_compiler.TypeUtil;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Liyeyu on 2017/3/6.
 */

public class AnnotatedClass {

    public TypeElement mClassElement;
    public List<BindViewField> mFields;
    public List<OnClickMethod> mMethods;
    public Elements mElementUtils;
    public AnnotatedClass(TypeElement classElement, Elements elementUtils) {
        this.mClassElement = classElement;
        this.mFields = new ArrayList<>();
        this.mMethods = new ArrayList<>();
        this.mElementUtils = elementUtils;
    }

    public String getFullClassName() {
        return mClassElement.getQualifiedName().toString();
    }

    public void addField(BindViewField field) {
        mFields.add(field);
    }

    public void addMethod(OnClickMethod method) {
        mMethods.add(method);
    }

    public JavaFile generateFinder() {

        // method inject(final T host, Object source, Provider provider)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject");
        methodBuilder.addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(TypeName.get(mClassElement.asType()),"host",Modifier.FINAL)
                .addParameter(TypeName.OBJECT,"source")
                .addParameter(TypeUtil.PROVIDER,"provider");

        for (BindViewField field : mFields) {
            // find views
            // host.view = (View)(provider.findView(source,R.id.tv));
            methodBuilder.addStatement("host.$N = ($T)(provider.findView(source,$L))"
                    ,field.getFieldName(),field.getFieldType(),field.getResId());
        }

//        if(mMethods.size()>0){
//            methodBuilder.addStatement("$T listener",TypeUtil.ANDROID_ON_CLICK_LISTENER);
//        }

        //anonymous class
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        for (OnClickMethod method:mMethods) {
            TypeSpec typeSpec = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtil.ANDROID_ON_CLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_VIEW,"view")
                            .addStatement("host.$N(view)",method.getMethodName()).build())
                    .build();
//            methodBuilder.addStatement("listener = $L ", typeSpec);
            for (int id : method.getResIds()) {
                methodBuilder.addStatement("provider.findView(source, $L).setOnClickListener($L)", id,typeSpec);
            }
        }

        TypeSpec finderType = TypeSpec.classBuilder(mClassElement.getSimpleName()+"$$Finder")
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(TypeUtil.FINDER,TypeName.get(mClassElement.asType())))
        .addMethod(methodBuilder.build())
        .build();

        String packName = mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();

        return JavaFile.builder(packName,finderType).build();
    }
}
