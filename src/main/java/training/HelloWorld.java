package training;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.projects.Project;
import io.sphere.sdk.projects.queries.ProjectGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("common")
public class HelloWorld implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.exit(SpringApplication.run(HelloWorld.class, args));
    }

    @Autowired
    private BlockingSphereClient client;

    @Override
    public void run(final String... strings) throws Exception {
        final Project project = client.executeBlocking(ProjectGet.of());
        System.err.println(project);
    }
}
