package handson.graphql;

import io.aexp.nodes.graphql.annotations.GraphQLProperty;

@GraphQLProperty(name="products")
public class Products {

    private int total;

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }
}
