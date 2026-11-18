package com.aatzhmair.orderservice.payment;

import com.aatzhmair.orderservice.exception.PaymentFailedException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripePaymentService {

    public StripePaymentService(@Value("${app.stripe.secret-key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    public String charge(BigDecimal amount, String idempotencyKey) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount.movePointRight(2).longValueExact()) // dollars -> cents
                    .setCurrency("usd")
                    .setPaymentMethod("pm_card_visa") // Stripe's built-in test token — swap for a real PaymentMethod ID from a checkout frontend
                    .setConfirm(true)
                    .build();

            RequestOptions options = RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
            return PaymentIntent.create(params, options).getId();

        } catch (StripeException e) {
            throw new PaymentFailedException("Stripe payment failed: " + e.getMessage(), e);
        }
    }
}