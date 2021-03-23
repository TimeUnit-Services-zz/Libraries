package rip.lazze.libraries.utilities.commands;

@FunctionalInterface
public interface Processor<T, R> {
    R process(T var1);
}
