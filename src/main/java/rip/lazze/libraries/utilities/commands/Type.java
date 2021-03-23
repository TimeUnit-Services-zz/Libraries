package rip.lazze.libraries.utilities.commands;

import rip.lazze.libraries.utilities.commands.param.ParameterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Type {
    Class<? extends ParameterType> value();
}
