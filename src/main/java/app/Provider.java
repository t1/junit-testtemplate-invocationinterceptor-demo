package app;

import java.util.EnumSet;
import java.util.stream.Stream;

public enum Provider {
    A, B, C;

    public static final EnumSet<Provider> ALL = EnumSet.allOf(Provider.class);

    public static EnumSet<Provider> allBut(Provider[] values) {
        var providers = ALL;
        Stream.of(values).forEach(providers::remove);
        return providers;
    }
}
