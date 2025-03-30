package com.example.spring_cap;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    PaymentOrder findByPaymentLinkId(String paymentId);
    long countByPackageObjIdAndStatus(Long packageId, PaymentOrderStatus status);
    List<PaymentOrder> findByUserIdAndStatus(Long userId, PaymentOrderStatus status);
}