package rip.lazze.libraries.config.annotations;

import rip.lazze.libraries.config.AbstractSerializer;
import rip.lazze.libraries.config.AbstractSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigSerializer {
    Class<? extends AbstractSerializer<Object>> serializer();
}
