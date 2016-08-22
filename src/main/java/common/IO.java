package common;

import io.sphere.sdk.json.SphereJsonUtils;

public final class IO {
    private IO() {
    }

    public static void toast(final Object o) {
        System.err.println(SphereJsonUtils.toPrettyJsonString(o));
    }
}
