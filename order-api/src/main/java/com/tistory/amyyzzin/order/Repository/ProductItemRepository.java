package com.tistory.amyyzzin.order.Repository;

import com.tistory.amyyzzin.order.domain.model.Product;
import com.tistory.amyyzzin.order.domain.model.ProductItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {

//    @EntityGraph(attributePaths = {"productItems"}, type = EntityGraphType.LOAD)
//    Optional<Product> findBySellerIdAndId(Long sellerId, Long id);

}
