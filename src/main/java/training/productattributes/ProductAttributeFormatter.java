package training.productattributes;

import io.sphere.sdk.products.attributes.DefaultProductAttributeFormatter;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeLocalRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class ProductAttributeFormatter extends DefaultProductAttributeFormatter {
    public ProductAttributeFormatter(final ProductTypeLocalRepository productTypes, final List<Locale> locales) {
        super(productTypes, locales);
    }

    public ProductAttributeFormatter(final Collection<ProductType> productTypes, final List<Locale> locales) {
        super(productTypes, locales);
    }
}
