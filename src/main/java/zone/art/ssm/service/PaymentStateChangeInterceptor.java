package zone.art.ssm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import zone.art.ssm.domain.Payment;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;
import zone.art.ssm.repository.PaymentRepository;

import java.util.Optional;

import static zone.art.ssm.service.PaymentServiceImpl.PAYMENT_ID_HEADER;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository repository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine, StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable((Long) msg.getHeaders().getOrDefault(
                PAYMENT_ID_HEADER, -1L))).ifPresent(paymentId -> {
            final Payment payment = repository.getReferenceById(paymentId);
            payment.setState(state.getId());
            repository.save(payment);
        });
    }
}
