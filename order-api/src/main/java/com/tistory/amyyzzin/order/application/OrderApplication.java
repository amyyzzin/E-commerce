package com.tistory.amyyzzin.order.application;

import static com.tistory.amyyzzin.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.tistory.amyyzzin.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

import com.tistory.amyyzzin.order.client.UserClient;
import com.tistory.amyyzzin.order.client.user.ChangeBalanceForm;
import com.tistory.amyyzzin.order.client.user.CustomerDto;
import com.tistory.amyyzzin.order.domain.model.ProductItem;
import com.tistory.amyyzzin.order.domain.redis.Cart;
import com.tistory.amyyzzin.order.exception.CustomException;
import com.tistory.amyyzzin.order.service.ProductItemService;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderApplication {

    private final CartApplication cartApplication;
    private final UserClient userClient;

    private final ProductItemService productItemService;


    @Transactional
    public void order(String token, Cart cart) {

        // 주문 시 기존 카트 버림
        // 선택주문 : 내가 사지 않은 아이템을 살려야 함 -> ?

        Cart orderCart = cartApplication.refreshCart(cart);
        if (orderCart.getMessages().size() > 0) {
            throw new CustomException(ORDER_FAIL_CHECK_CART);
        }
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

        int totalPrice = getTotalPrice(cart);

        if (customerDto.getBalance() < getTotalPrice(cart)) {
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }

        userClient.changeBalance(token, ChangeBalanceForm.builder()
            .from("USER")
            .message("Order")
            .money(-totalPrice)
            .build());

        for (Cart.Product product : orderCart.getProducts()) {
            for (Cart.ProductItem cartItem : product.getItems()) {
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }


    }

    public Integer getTotalPrice(Cart cart) {
        return cart.getProducts().stream().flatMapToInt(product ->
                product.getItems().stream().flatMapToInt(
                    productItem -> IntStream.of(productItem.getPrice() * productItem.getCount())))
            .sum();
    }

    // 결제를 위해 필요한 것
    // 1. 물건들이 전부 주문 가능 한 상태인지 확인
    // 2. 가격 변동이 있었는지에 대해 확인
    // 3. 고객의 돈이 충분한지
    // 4. 결제 & 상품의 재고 관리


}
