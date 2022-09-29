package com.tistory.amyyzzin.cms.service.customer;

import static com.tistory.amyyzzin.cms.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static com.tistory.amyyzzin.cms.exception.ErrorCode.NOT_FOUND_USER;

import com.tistory.amyyzzin.cms.domain.customer.ChangeBalanceForm;
import com.tistory.amyyzzin.cms.domain.model.CustomerBalanceHistory;
import com.tistory.amyyzzin.cms.domain.repository.CustomerBalanceHistoryRepository;
import com.tistory.amyyzzin.cms.domain.repository.CustomerRepository;
import com.tistory.amyyzzin.cms.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {

    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional(noRollbackFor = {CustomException.class})
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form)
        throws CustomException {
        CustomerBalanceHistory customerBalanceHistory =
            customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customerId)
                .orElse(CustomerBalanceHistory.builder()
                    .changeMoney(0)
                    .currentMoney(0)
                    .customer(customerRepository.findById(customerId)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_USER)))
                    .build());
        if (customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0) {
            throw new CustomException(NOT_ENOUGH_BALANCE);
        }
        customerBalanceHistory = CustomerBalanceHistory.builder()
            .changeMoney(form.getMoney())
            .currentMoney(customerBalanceHistory.getCurrentMoney() + form.getMoney())
            .description(form.getMessage())
            .fromMessage(form.getFrom())
            .customer(customerBalanceHistory.getCustomer())
            .build();
        customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());

        return customerBalanceHistoryRepository.save(customerBalanceHistory);

    }
}
