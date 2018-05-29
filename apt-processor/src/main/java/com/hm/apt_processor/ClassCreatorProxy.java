package com.hm.apt_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by dumingwei on 2018/5/27 0027.
 */
public class ClassCreatorProxy {

    private static int viewIndex = 0;
    private String bindingClassName;
    private String packageName;
    /**
     * represent class
     */
    private TypeElement typeElement;
    private Map<Integer, VariableElement> bindViewElementMap = new HashMap<>();
    private Map<Integer, ExecutableElement> onClickElementMap = new HashMap<>();
    private ClassName viewClass = ClassName.get("android.view", "View");
    private ClassName keepClass = ClassName.get("android.support.annotation", "Keep");
    private ClassName clickClass = ClassName.get("android.view", "View.OnClickListener");

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        packageName = packageElement.getQualifiedName().toString();
        bindingClassName = typeElement.getSimpleName().toString() + "_ViewBinding";
    }

    public void putElement(int id, VariableElement element) {
        bindViewElementMap.put(id, element);
    }

    public void putOnclickElement(int id, ExecutableElement element) {
        onClickElementMap.put(id, element);
    }

    public String getPackageName() {
        return packageName;
    }

    public TypeSpec generateJavaCode() {
        return TypeSpec.classBuilder(bindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods())
                .build();
    }

    /**
     * 生成 Method
     *
     * @return
     */
    private MethodSpec generateMethods() {
        ClassName typeClass = ClassName.get(typeElement.getQualifiedName().toString().replace(
                "." + typeElement.getSimpleName().toString(), ""),
                typeElement.getSimpleName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(typeClass, "target", Modifier.FINAL);//方法参数
        for (Integer id : bindViewElementMap.keySet()) {
            VariableElement element = bindViewElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addStatement("target.$L=($L)((android.app.Activity)target).findViewById($L)", name, type, id);
        }
        for (Integer id : onClickElementMap.keySet()) {
            ExecutableElement element = onClickElementMap.get(id);
            VariableElement variableElement = bindViewElementMap.get(id);
            /**
             * 如果使用OnClick注解的View,同时也使用了BindView注解,直接添加click事件，
             * 否则就得先findViewById
             */
            if (variableElement != null) {
                String name = variableElement.getSimpleName().toString();
                TypeSpec click = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(clickClass)
                        .addMethod(MethodSpec.methodBuilder("onClick")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(viewClass, "view")
                                .addStatement("target.$L(target.$L)", element.getSimpleName().toString(), name)
                                .returns(void.class)
                                .build())
                        .build();
                methodBuilder.addStatement("target.$L.setOnClickListener($L)", name, click);
            } else {
                methodBuilder.addStatement("final View view$L=((android.app.Activity)target).findViewById($L)", viewIndex, id);
                TypeSpec click = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(clickClass)
                        .addMethod(MethodSpec.methodBuilder("onClick")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(viewClass, "view")
                                .addStatement("target.$L(view$L)", element.getSimpleName().toString(), viewIndex)
                                .returns(void.class)
                                .build())
                        .build();
                methodBuilder.addStatement("view$L.setOnClickListener($L)", viewIndex, click);
                viewIndex++;
            }

        }
        return methodBuilder.build();
    }

}
