package zone.art.ssm.service;

import org.springframework.statemachine.StateMachine;
import zone.art.ssm.domain.Payment;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
public interface PaymentService {

    Payment newPayment(Payment payment);

    Payment getById(Long id);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId);
}
