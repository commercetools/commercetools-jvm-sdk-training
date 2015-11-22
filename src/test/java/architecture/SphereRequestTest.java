package architecture;

import com.fasterxml.jackson.databind.JsonNode;
import intern.BaseTest;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.queries.CategoryByIdGet;
import io.sphere.sdk.client.HttpRequestIntent;
import io.sphere.sdk.client.JsonNodeSphereRequest;
import io.sphere.sdk.client.SphereRequest;
import io.sphere.sdk.http.HttpMethod;
import io.sphere.sdk.http.HttpResponse;
import org.junit.Test;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class SphereRequestTest extends BaseTest {
    /**
     * Shows how generic a request to commercetools platform can be.
     * Uses a String as output type which is hard to work with.
     */
    @Test
    public void demoOfAPureStringSphereRequest() {
        final String id = categoryId();
        final SphereRequest<String> request = new SphereRequest<String>() {
            @Override
            public HttpRequestIntent httpRequestIntent() {
                return HttpRequestIntent.of(HttpMethod.GET, format("/categories/%s", id));
            }

            @Override
            public String deserialize(final HttpResponse httpResponse) {
                final byte[] responseBody = httpResponse.getResponseBody();
                return new String(responseBody);
            }
        };

        final String result = ct().complete(request);
        assertThat(result).contains(id);
    }

    /**
     * Improved example which uses a helper class and parses the result into a JSON tree.
     * The navigation in the object needs to be known and fields are accessed with names as String.
     */
    @Test
    public void demoOfAPureJsonSphereRequest() {
        final String id = categoryId();
        final SphereRequest<JsonNode> request = JsonNodeSphereRequest.of(HttpMethod.GET, format("/categories/%s", id));
        final JsonNode result = ct().complete(request);
        final JsonNode idNode = result.get("id");
        assertThat(idNode.asText() /* type guessing */).isEqualTo(id);
    }

    /**
     * Types SphereRequest and result. Fields can be accessed by methods and have type information included.
     */
    @Test
    public void demoOfATypedSphereRequest() {
        final String id = categoryId();
        final SphereRequest<Category> request = CategoryByIdGet.of(id);
        final Category result = ct().complete(request);
        final String observedId = result.getId();
        assertThat(observedId).isEqualTo(id);
    }

    /**
     * Check what happens with an invalid ID.
     *
     */
    @Test
    public void taskWhatHappensWithEmptyResponse() {
        /* start: do not modify */
        final String id = "foo";
        final SphereRequest<Category> request = CategoryByIdGet.of(id);
        final Category result = ct().complete(request);
        /* end: do not modify */

        //TODO fix the assertions
        final String observedId = result.getId();
        assertThat(observedId).isEqualTo(id);
    }
}
