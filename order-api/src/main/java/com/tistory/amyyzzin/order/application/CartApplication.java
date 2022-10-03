package com.tistory.amyyzzin.order.application;

import static com.tistory.amyyzzin.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.tistory.amyyzzin.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

import com.tistory.amyyzzin.order.domain.model.Product;
import com.tistory.amyyzzin.order.domain.model.ProductItem;
import com.tistory.amyyzzin.order.domain.product.AddProductCartForm;
import com.tistory.amyyzzin.order.domain.redis.Cart;
import com.tistory.amyyzzin.order.exception.CustomException;
import com.tistory.amyyzzin.order.service.CartService;
import com.tistory.amyyzzin.order.service.ProductSearchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartApplication {

    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form) {

        Product product = productSearchService.getByProductId(form.getId());
        if (product == null) {
            throw new CustomException(NOT_FOUND_PRODUCT);
        }
        Cart cart = cartService.getCart(customerId);

        if (cart != null && !addAble(cart, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }
        return cartService.addCart(customerId, form);
    }

    public Cart getCart(Long customerId) {

        Cart cart = refreshCart(cartService.getCart(customerId));
        Cart returnCart = new Cart();

        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());
        cart.setMessages(new ArrayList<>());
        //메세지 없는 것
        cartService.putCart(customerId, cart);
        return returnCart;
    }

    public void clearCart(Long customerId) {
        cartService.putCart(customerId,null);
    }

    private Cart refreshCart(Cart cart) {

        // 1. 상품이나 상품 아이템의 정보 가격 수량이 변경되었는지 체크하고, 그에 맞는 알람을 제공해준다.
        // 2. 상품의 수량, 가격을 우리가 임의로 변경한다.

        Map<Long, Product> productMap = productSearchService.getListByProductIds(
                cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList()))
            .stream().collect(Collectors.toMap(Product::getId, product -> product));

        for (int i = 0; i < cart.getProducts().size(); i++) {

            Cart.Product cartProduct = cart.getProducts().get(i);

            Product p = productMap.get(cartProduct.getId());

            if (p == null) {
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + "상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            //각각 케이스별로 에러를 쪼개고, 에러가 정상 출력 되야 하는지 체크해야 한다.
            List<String> tmpMessage = new ArrayList<>();

            for (int j = 0; j < cartProduct.getItems().size(); j++) {
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(i);
                ProductItem pi = productItemMap.get(cartProductItem.getId());

                if (pi == null) {
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    tmpMessage.add(cartProductItem.getName() + "옵션이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChange = false;
                boolean isCountNotEnough = false;

                if (!cartProductItem.getPrice().equals(pi.getPrice())) {
                    isPriceChange = true;
                    cartProductItem.setPrice(pi.getPrice());
                }
                if (cartProductItem.getCount() > pi.getCount()) {
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }
                if (isPriceChange && isCountNotEnough) {
                    tmpMessage.add(cartProductItem.getName() + "가격 변동 및 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                } else if (isPriceChange) {
                    tmpMessage.add(cartProductItem.getName() + "가격이 변동되었습니다.");
                } else if (isCountNotEnough) {
                    tmpMessage.add(cartProductItem.getName() + "수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }

            if (cartProduct.getItems().size() == 0) {
                cart.getProducts().remove(cartProduct);
                i--;
                tmpMessage.add(cartProduct.getName() + "상품의 옵션이 없습니다. 구매가 불가능 합니다.");
            } else if (tmpMessage.size() > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName() + "상품의 변동 사항 : ");
                for (String message : tmpMessage) {
                    builder.append(message);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }
        }
        cartService.putCart(cart.getCustomerId(), cart);
        return cart;

    }

    private boolean addAble(Cart cart, Product product, AddProductCartForm form) {
//        if(form.getId().equals(product.getId()))
        Cart.Product cartProduct = cart.getProducts().stream()
            .filter(p -> p.getId().equals(form.getId()))
            .findFirst().orElse(Cart.Product.builder().id(product.getId())
                .items(Collections.emptyList()).build());

        Map<Long, Integer> cartItemCountMap = cartProduct.getItems().stream()
            .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream()
            .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getItems().stream().noneMatch(
            formItem -> {
                Integer cartCount = cartItemCountMap.get(formItem.getId());
                if (cartCount == null) {
                    cartCount = 0;
                }
                Integer currentCount = currentItemCountMap.get(formItem.getId());
                return formItem.getCount() + cartCount > currentCount;
            });
    }
}
