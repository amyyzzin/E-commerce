package com.tistory.amyyzzin.order.Repository;

import com.tistory.amyyzzin.order.domain.model.Product;
import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> searchByName(String name);

}
