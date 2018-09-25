package handson;

import handson.impl.ClientService;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.projects.Project;
import io.sphere.sdk.projects.queries.ProjectGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createSphereClient;

/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO 1.1 dev.properties
 *  TODO 1.2 {@link ClientService#loadCTPClientConfig()}
 *  TODO 1.3 {@link ClientService#createSphereClient()}
 */
public class Exercise1 {
    private static final Logger LOG = LoggerFactory.getLogger(Exercise1.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        try (final SphereClient client = createSphereClient()) {

            final CompletableFuture<Project> getProjectResult = client.execute(ProjectGet.of()).toCompletableFuture();
            final Project project = getProjectResult.get();

            LOG.info("Project info {}", project);
        }
    }
}
