package rip.lazze.libraries.utilities.commands.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Param {
    String name();

    String defaultValue() default "";

    String[] tabCompleteFlags() default {};

    boolean wildcard() default false;
}