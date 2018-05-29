package com.hm.apt_processor;

import com.google.auto.service.AutoService;
import com.hm.apt_annotation.BindView;
import com.hm.apt_annotation.OnClick;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by dumingwei on 2018/5/24 0024.
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementsUtils;
    private Map<String, ClassCreatorProxy> proxyMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        supportTypes.add(OnClick.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementsUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        //this method may be called many times,so we must clear proxyMap to avoid generate  repeated class
        proxyMap.clear();
        /**
         * BindView
         */
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            VariableElement variableElement = ((VariableElement) element);
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ClassCreatorProxy proxy = proxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(elementsUtils, classElement);
                proxyMap.put(fullClassName, proxy);
            }
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindAnnotation.value();
            proxy.putElement(id, variableElement);
        }
        /**
         * OnClick
         */
        Set<? extends Element> onClickElements = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        for (Element element : onClickElements) {
            ExecutableElement executableElement = ((ExecutableElement) element);
            TypeElement classElement = (TypeElement) executableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ClassCreatorProxy proxy = proxyMap.get(fullClassName);
            OnClick onClickAnnotation = executableElement.getAnnotation(OnClick.class);
            int[] values = onClickAnnotation.value();
            for (int i = 0; i < values.length; i++) {
                proxy.putOnclickElement(values[i], executableElement);
            }
        }

        for (String key : proxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = proxyMap.get(key);
            JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCode()).build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "finish...");
        return true;
    }
}
