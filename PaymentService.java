package com.example.spring_cap;

import com.stripe.exception.StripeException;

public interface PaymentService {
    PaymentOrder createOrder(User user, Package packageObj);
    PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws Exception;
    String createStripePaymentLink(User user, Long amount, Long packageId) throws StripeException;
    boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId);
    String createPaymentIntent(User user, Long amount, Long packageId) throws StripeException; // Add this
}