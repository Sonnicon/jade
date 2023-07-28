package sonnicon.jade;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(EventGenerators.class)
@Retention(RetentionPolicy.CLASS)
public @interface EventGenerator {
    String id();

    Class<?>[] param();

    String[] label();
}
