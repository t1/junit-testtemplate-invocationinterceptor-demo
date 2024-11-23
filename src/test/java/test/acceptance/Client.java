package test.acceptance;

import java.util.function.Supplier;

public class Client {
    private final Supplier<String> accountIdSupplier;

    public Client(Supplier<String> accountIdSupplier) {
        this.accountIdSupplier = accountIdSupplier;
    }

    public String callX() {
        return "X:" + accountIdSupplier.get();
    }

    public String callY() {
        return "Y:" + accountIdSupplier.get();
    }

    public String callZ() {
        return "Z:" + accountIdSupplier.get();
    }
}
