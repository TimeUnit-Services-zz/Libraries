package rip.lazze.libraries.utilities.commands;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Command {
    String[] names();

    String permission();

    boolean hidden() default false;

    boolean async() default false;

    boolean requiresPlayer() default false;

    boolean shouldcomplete() default true;

    String description() default "";
}