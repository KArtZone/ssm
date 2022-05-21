package org.springframework.statemachine.action;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import zone.art.ssm.domain.Payment;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;

import java.math.BigDecimal;

import static java.lang.System.out;
import static zone.art.ssm.domain.PaymentEvent.AUTH_APPROVED;
import static zone.art.ssm.domain.PaymentEvent.AUTH_DECLINED;
import static zone.art.ssm.service.PaymentServiceImpl.PAYMENT_ID_HEADER;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
public class AuthorizeAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        out.printf("%nAuth called. Payload: %s%n", context.getMessage().getPayload());
        // Тут должна быть знатная логика, которая либо успешно проведет платеж (событие AUTH_APPROVED),
        // либо завершит работу SM с ошибкой на этапе проведения платежа
        final Object messageHeader = context.getMessageHeader(PAYMENT_ID_HEADER);
        final Payment payment = context.getEvent().getPayment();
        if (payment.getAmount().compareTo(BigDecimal.valueOf(9999.42)) >= 0) {
            out.println("Auth approved");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(AUTH_APPROVED)
                    .setHeader(PAYMENT_ID_HEADER, messageHeader)
                    .build(
                    ));
        } else {
            out.printf("%nAuth: declined. No credit!%n");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(AUTH_DECLINED)
                    .setHeader(PAYMENT_ID_HEADER, messageHeader)
                    .build());
        }
    }
}
