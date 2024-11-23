package test.acceptance;

import app.Provider;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class AccountFixture implements Extension, TestTemplateInvocationContextProvider, InvocationInterceptor {
    private static final Namespace NAMESPACE = Namespace.create(AccountFixture.class);

    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return Stream.of(Provider.values()).map(MyTestTemplateInvocationContext::new);
    }

    private final class MyTestTemplateInvocationContext implements TestTemplateInvocationContext {
        private final Provider provider;

        private MyTestTemplateInvocationContext(Provider provider) {this.provider = provider;}

        @Override
        public String getDisplayName(int invocationIndex) {
            return "Provider " + provider + " (" + invocationIndex + ")";
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return List.of(new TypeBasedParameterResolver<Client>() {
                @Override
                public Client resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
                    extensionContext.getStore(NAMESPACE).put("provider", provider);
                    // this is called before #interceptTestTemplateMethod, so the accountId has not yet been set
                    return new Client(() -> AccountFixture.this.accountId);
                }
            });
        }
    }


    @Override
    public void interceptTestTemplateMethod(
            Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
        var provider = (Provider) extensionContext.getStore(NAMESPACE).get("provider");

        if (provider == null) {
            accountId = "none";
        } else {
            assumeThat(enabledProviders(extensionContext))
                    .describedAs("%s not yet supported by %s", extensionContext.getRequiredTestMethod().getName(),
                            extensionContext.getDisplayName())
                    .contains(provider);
            accountId = "[" + provider.name() + "]";
        }

        invocation.proceed();
    }

    private Set<Provider> enabledProviders(ExtensionContext context) {
        return findAnnotation(context.getElement(), NotYetProvidedBy.class)
                .or(() -> findAnnotation(context.getTestClass(), NotYetProvidedBy.class))
                .map(NotYetProvidedBy::value)
                .map(Provider::allBut)
                .orElse(Provider.ALL);
    }
}
