package org.springframework.statemachine.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import zone.art.ssm.domain.Payment;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;

import java.math.BigDecimal;

import static zone.art.ssm.domain.PaymentEvent.PRE_AUTH_APPROVED;
import static zone.art.ssm.domain.PaymentEvent.PRE_AUTH_DECLINED;
import static zone.art.ssm.service.PaymentServiceImpl.PAYMENT_ID_HEADER;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
@Slf4j
public class PreAuthorizeAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.info("PreAuth called. Payload: {}", context.getMessage().getPayload());
        // Тут должна быть знатная логика, которая либо продолжит выполнение SM (событие PRE_AUTH_APPROVED),
        // либо завершит работу SM с ошибкой на этапе предварительной обработки
        final Object messageHeader = context.getMessageHeader(PAYMENT_ID_HEADER);
        final Payment payment = context.getEvent().getPayment();
        if (payment.getAmount().compareTo(BigDecimal.valueOf(9999.42)) >= 0) {
            log.info("PreAuth approved!");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PRE_AUTH_APPROVED)
                    .setHeader(PAYMENT_ID_HEADER, messageHeader)
                    .build(
                    ));
        } else {
            log.info("PreAuth: declined. No credit!");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PRE_AUTH_DECLINED)
                    .setHeader(PAYMENT_ID_HEADER, messageHeader)
                    .build());
        }
    }
}
