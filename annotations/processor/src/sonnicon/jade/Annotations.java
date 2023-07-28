package sonnicon.jade;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

//todo make this nicer when I am processing more
@AutoService(javax.annotation.processing.Processor.class)
public class Annotations extends AbstractProcessor {
    EventBuilder eb = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // we do it ONCE
        if (eb != null) {
            return true;
        }
        eb = new EventBuilder();

        // Add all the events
        for (Element element : roundEnvironment.getElementsAnnotatedWith(EventGenerator.class)) {
            for (EventGenerator annotation : element.getAnnotationsByType(EventGenerator.class)) {
                eb.addEvent(annotation);
            }
        }
        for (Element element : roundEnvironment.getElementsAnnotatedWith(EventGenerators.class)) {
            for (EventGenerators annotation : element.getAnnotationsByType(EventGenerators.class)) {
                for (EventGenerator gen : annotation.value()) {
                    eb.addEvent(gen);
                }
            }
        }

        // Build and write
        JavaFile javaFile = JavaFile.builder("sonnicon.jade.generated", eb.build()).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException ex) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supported = new HashSet<>();
        supported.add(EventGenerator.class.getCanonicalName());
        supported.add(EventGenerators.class.getCanonicalName());
        return supported;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}