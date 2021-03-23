package rip.lazze.libraries.utilities.commands.utils;

import java.lang.reflect.Field;

public class EasyField<T>
{
    private Field field;
    private EasyClass<?> owner;

    public EasyField(final EasyClass<?> owner, final String name) {
        this.owner = owner;
        try {
            if (this.owner != null && this.owner.getClazz() != null) {
                this.field = this.owner.getClazz().getDeclaredField(name);
            }
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public T get() {
        try {
            if (this.field != null) {
                this.field.setAccessible(true);
                return (T)this.field.get(this.owner.get());
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(final T value) {
        if (this.field.isAccessible()) {
            this.field.setAccessible(true);
        }
        try {
            this.field.set(this.owner.get(), value);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}