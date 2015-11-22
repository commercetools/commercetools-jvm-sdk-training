package intern;

import io.sphere.sdk.categories.CategoryDraft;
import io.sphere.sdk.categories.CategoryDraftBuilder;
import io.sphere.sdk.categories.commands.CategoryCreateCommand;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.models.LocalizedString;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public abstract class BaseTest {
    private static Client client;

    @BeforeClass
    public static void setupClient() throws IOException {
        final Properties prop = new Properties();
        prop.load(BaseTest.class.getClassLoader().getResourceAsStream("dev.properties"));
        final SphereClientConfig clientConfig = SphereClientConfig.of(prop.getProperty("projectKey"), prop.getProperty("clientId"), prop.getProperty("clientSecret"));
        final SphereClient sphereClient = SphereClientFactory.of().createClient(clientConfig);
        client = new ClientImpl(sphereClient);
    }

    @BeforeClass
    public static void fixtures() throws IOException {
        for(int i = 0; i < 100; i++) {
            final String group = "A";
            final LocalizedString name = LocalizedString.of(Locale.ENGLISH, String.format("Category " + group + "%3d", i));
            final LocalizedString slug = name.slugified();
            final CategoryDraft categoryDraft = CategoryDraftBuilder.of(name, slug)
                    .externalId(group + i)
                    .build();
            ct().complete(CategoryCreateCommand.of(categoryDraft));
        }
    }

    protected static String categoryId() {
        final CategoryQuery request = CategoryQuery.of().withPredicates(m -> m.externalId().is("A1"));
        return ct().complete(request).getResults().get(0).getExternalId();
    }

    @AfterClass
    public void closeClient() throws Exception {
        client.close();
        client = null;
    }

    protected static Client ct() {
        return client;
    }
}
