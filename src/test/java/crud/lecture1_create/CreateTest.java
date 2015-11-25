package crud.lecture1_create;

import intern.BaseTest;
import io.sphere.sdk.products.attributes.AttributeConstraint;
import io.sphere.sdk.products.attributes.AttributeDefinition;
import io.sphere.sdk.products.attributes.MoneyType;
import io.sphere.sdk.products.attributes.StringType;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.commands.ProductTypeCreateCommand;
import io.sphere.sdk.producttypes.commands.ProductTypeDeleteCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeByKeyGet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateTest extends BaseTest {

    /**
     * if working with product types, use this import
     * import io.sphere.sdk.products.attributes.*;
     * This way there are less name clashes with CustomTypes.
     *
     */

    @Test
    public void createAProductType() {
        final ProductTypeCreateCommand createCommand = TODO();
        final ProductType productType = ct().complete(createCommand);
        assertThat(productType.getName()).isEqualTo("boiler");
        assertThat(productType.getKey()).isEqualTo("boiler");
        assertThat(productType.getDescription()).isEqualTo("engine to heat water");
    }

    @Test
    public void createAProductTypeWithAttributes() {
        final ProductTypeCreateCommand createCommand = TODO();
        final ProductType productType = ct().complete(createCommand);
        assertThat(productType.getKey()).isEqualTo("boiler");
        final AttributeDefinition shorttextAttributeDefinition = productType.getAttribute("shorttext");
        assertThat(shorttextAttributeDefinition.getAttributeType()).isInstanceOf(StringType.class);
        assertThat(shorttextAttributeDefinition.getAttributeConstraint()).isEqualTo(AttributeConstraint.NONE);
        assertThat(shorttextAttributeDefinition.getIsRequired()).isTrue();
        final AttributeDefinition srpPriceAttributeDefinition = productType.getAttribute("srpprice");
        assertThat(srpPriceAttributeDefinition.getAttributeType()).isInstanceOf(MoneyType.class);
        assertThat(srpPriceAttributeDefinition.getAttributeConstraint()).isEqualTo(AttributeConstraint.NONE);
        assertThat(srpPriceAttributeDefinition.getIsRequired()).isFalse();
    }

    @Before
    @After
    public void cleanUp() {
        Optional.ofNullable(ct().complete(ProductTypeByKeyGet.of("boiler")))
                .ifPresent(boiler -> ct().complete(ProductTypeDeleteCommand.of(boiler)));
    }
}
