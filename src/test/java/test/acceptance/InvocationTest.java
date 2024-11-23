package test.acceptance;

import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.RegisterExtension;

import static app.Provider.A;
import static app.Provider.C;
import static org.assertj.core.api.BDDAssertions.then;

class InvocationTest {
    @RegisterExtension
    static AccountFixture account = new AccountFixture();

    @TestTemplate
    void testX(TestReporter reporter, Client client) {
        var response = client.callX();

        then(response).isEqualTo("X:" + account.getAccountId());
        reporter.publishEntry("successful: " + response);
    }

    @TestTemplate
    @NotYetProvidedBy(C)
    void testY(Client client) {
        var response = client.callY();

        then(response).isEqualTo("Y:" + account.getAccountId());
    }

    @TestTemplate
    @NotYetProvidedBy({A, C})
    void testZ(Client client) {
        var response = client.callZ();

        then(response).isEqualTo("Z:" + account.getAccountId());
    }

    @TestTemplate
    void testWithoutClient() {
        var accountId = account.getAccountId();

        then(accountId).isEqualTo("none");
    }
}
