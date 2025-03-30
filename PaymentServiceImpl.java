package com.example.spring_cap;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public PaymentServiceImpl(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    public PaymentOrder createOrder(User user, Package packageObj) {
        PaymentOrder order = new PaymentOrder();
        order.setUser(user);
        order.setPackageObj(packageObj);
        order.setAmount((long) packageObj.getPrice() * 100); // Convert to cents
        order.setPaymentMethod(PaymentMethod.STRIPE);
        order.setStatus(PaymentOrderStatus.PENDING);
        return paymentOrderRepository.save(order);
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws Exception {
        PaymentOrder order = paymentOrderRepository.findByPaymentLinkId(paymentId);
        if (order == null) {
            throw new Exception("Payment order not found");
        }
        return order;
    }

    @Override
    public String createStripePaymentLink(User user, Long amount, Long packageId) throws StripeException {
        // Placeholder; not used in this flow
        return "";
    }

    @Override
    public String createPaymentIntent(User user, Long amount, Long packageId) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency("cad")
                .putMetadata("packageId", packageId.toString()) // Use putMetadata instead of setMetadata
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }

    @Override
    public boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) {
        try {
            Stripe.apiKey = stripeApiKey;
            PaymentIntent intent = PaymentIntent.retrieve(paymentId);
            if ("succeeded".equals(intent.getStatus())) {
                paymentOrder.setPaymentLinkId(paymentId);
                paymentOrder.setStatus(PaymentOrderStatus.COMPLETED);
                paymentOrderRepository.save(paymentOrder);
                return true;
            }
            paymentOrder.setStatus(PaymentOrderStatus.FAILED);
            paymentOrderRepository.save(paymentOrder);
            return false;
        } catch (StripeException e) {
            paymentOrder.setStatus(PaymentOrderStatus.FAILED);
            paymentOrderRepository.save(paymentOrder);
            return false;
        }
    }
}