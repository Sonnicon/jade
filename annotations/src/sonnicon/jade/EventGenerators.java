package sonnicon.jade;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface EventGenerators {
    EventGenerator[] value();
}
