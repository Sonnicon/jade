package sonnicon.jade;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;

public class EventBuilder {
    private final TypeSpec.Builder outerBuilder;
    private final ArrayList<TypeSpec> genericClasses;

    EventBuilder() {
        outerBuilder = TypeSpec.classBuilder("EventTypes")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Auto-generated file. Do not modify.");
        genericClasses = new ArrayList<>();
    }

    boolean addEvent(EventGenerator generator) {
        TypeMirror[] params = null;
        try {
            //noinspection ResultOfMethodCallIgnored
            generator.param();
            assert false;
        } catch (MirroredTypesException ex) {
            params = ex.getTypeMirrors().toArray(new TypeMirror[0]);
        }

        TypeSpec genericClass = getGenericClass(params.length);
        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(generator.id() + "Event")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get("", genericClass.name),
                        Arrays.stream(params).map(ClassName::get).toArray(TypeName[]::new)))
                .addAnnotation(FunctionalInterface.class);

        MethodSpec.Builder handleMethodBuilder = MethodSpec.methodBuilder("handle")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(ClassName.get("sonnicon.jade.util", "Events"), "events")
                .addCode("events.handle((Class<? extends $T>)"
                                + generator.id() + "Event.class, " + String.join(", ", generator.label()) + ");",
                        sonnicon.jade.EventHandler.class);

        MethodSpec.Builder typeMethodBuilder = MethodSpec.methodBuilder("getType")
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                .returns(ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(EventHandler.class))))
                .addAnnotation(Override.class)
                .addCode("return $T.class;", ClassName.get("", generator.id() + "Event"));

        for (int i = 0; i < params.length; i++) {
            handleMethodBuilder.addParameter(ClassName.get(params[i]), generator.label()[i]);
        }

        classBuilder.addMethod(handleMethodBuilder.build());
        classBuilder.addMethod(typeMethodBuilder.build());
        outerBuilder.addType(classBuilder.build());
        return true;
    }

    TypeSpec getGenericClass(int num) {
        if (num < genericClasses.size() && genericClasses.get(num) != null) {
            return genericClasses.get(num);
        }
        while (num >= genericClasses.size()) {
            genericClasses.add(null);
        }

        TypeSpec.Builder genericClassBuilder = TypeSpec.interfaceBuilder("EventHandler" + num)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(EventHandler.class));

        MethodSpec.Builder applyInternalMethodBuilder = MethodSpec.methodBuilder("applyInternal")
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                .returns(void.class)
                .addParameter(Object[].class, "objs")
                .varargs()
                .addAnnotation(Override.class);

        MethodSpec.Builder applyMethodBuilder = MethodSpec.methodBuilder("apply")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(void.class);

        ArrayList<String> casts = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            String paramTypeName = String.valueOf((char) ('A' + i));
            TypeVariableName param = TypeVariableName.get(paramTypeName);
            applyMethodBuilder.addParameter(param, paramTypeName);
            genericClassBuilder.addTypeVariable(param);
            casts.add("(" + paramTypeName + ") objs[" + i + "]");
        }
        applyInternalMethodBuilder.addStatement("apply(" + String.join(",", casts) + ");");

        genericClassBuilder.addMethod(applyMethodBuilder.build());
        genericClassBuilder.addMethod(applyInternalMethodBuilder.build());

        TypeSpec genericClass = genericClassBuilder.build();
        outerBuilder.addType(genericClass);
        genericClasses.set(num, genericClass);
        return genericClassBuilder.build();
    }

    TypeSpec build() {
        return outerBuilder.build();
    }
}
