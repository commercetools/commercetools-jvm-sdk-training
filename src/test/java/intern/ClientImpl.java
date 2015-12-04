package intern;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereRequest;

import java.util.concurrent.*;

final class ClientImpl implements Client {
    private final SphereClient delegate;

    public ClientImpl(final SphereClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public <T> CompletionStage<T> execute(final SphereRequest<T> sphereRequest) {
        return delegate.execute(sphereRequest);
    }

    @Override
    public <T> T complete(final SphereRequest<T> sphereRequest) {
        try {
            return execute(sphereRequest).toCompletableFuture().get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            final Throwable cause =
                    e.getCause() != null && e instanceof ExecutionException
                            ? e.getCause()
                            : e;
            throw cause instanceof RuntimeException? (RuntimeException) cause : new CompletionException(cause);
        }
    }
}
