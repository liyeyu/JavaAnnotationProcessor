package com.liyeyu.viewfinder_compiler;


import com.google.auto.service.AutoService;
import com.liyeyu.viewfinder_annotation.BindView;
import com.liyeyu.viewfinder_annotation.OnClick;
import com.liyeyu.viewfinder_compiler.model.AnnotatedClass;
import com.liyeyu.viewfinder_compiler.model.BindViewField;
import com.liyeyu.viewfinder_compiler.model.OnClickMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by Liyeyu on 2017/2/23.
 */
@AutoService(Processor.class)
public class ViewFinderProcesser extends AbstractProcessor {
    /**
     * Google auto-service auto Generate META-INF/services/javax.annotation.processing.Processor file
     */

    private Filer mFiler;
    private Elements mElementUtils;
    private Messager mMessager;


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types  = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    private Map<String, AnnotatedClass> mAnnotatedClassMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        mAnnotatedClassMap.clear();

        try {
            processBindView(roundEnv);
            processOnClick(roundEnv);
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
            return true; // stop process
        }
        for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
            try {
                info("Generating file for %s", annotatedClass.getFullClassName());
                annotatedClass.generateFinder().writeTo(mFiler);
            } catch (IOException e) {
                error("Generate file failed, reason: %s", e.getMessage());
                return true;
            }
        }

        return true;
    }

    private void processBindView(RoundEnvironment roundEnv) throws IllegalArgumentException {
        for (Element element:roundEnv.getElementsAnnotatedWith(BindView.class)) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            annotatedClass.addField(new BindViewField(element));
        }
    }

    private void processOnClick(RoundEnvironment roundEnv) {
        for (Element element:roundEnv.getElementsAnnotatedWith(OnClick.class)) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            annotatedClass.addMethod(new OnClickMethod(element));
        }
    }

    private AnnotatedClass getAnnotatedClass(Element element) {
        TypeElement  typeElement = (TypeElement) element.getEnclosingElement();
        String className = typeElement.getQualifiedName().toString();
        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(className);
        if(annotatedClass==null){
            annotatedClass = new AnnotatedClass(typeElement,mElementUtils);
            mAnnotatedClassMap.put(className,annotatedClass);
        }
        return annotatedClass;
    }
    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

}
