package zone.art.ssm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import zone.art.ssm.domain.Payment;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;
import zone.art.ssm.repository.PaymentRepository;

import javax.transaction.Transactional;

import static org.springframework.messaging.support.MessageBuilder.withPayload;
import static zone.art.ssm.domain.PaymentEvent.AUTHORIZE;
import static zone.art.ssm.domain.PaymentEvent.PRE_AUTHORIZE;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository repository;

    private final PaymentStateChangeInterceptor interceptor;

    private final StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Override
    @Transactional
    public Payment newPayment(Payment payment) {
        return repository.save(payment);
    }

    @Override
    public Payment getById(Long id) {
        return repository.getReferenceById(id);
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        return sendEvent(paymentId, PRE_AUTHORIZE);
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId) {
        return sendEvent(paymentId, AUTHORIZE);
    }

    private StateMachine<PaymentState, PaymentEvent> sendEvent(final Long paymentId, final PaymentEvent event) {
        final Payment payment = repository.getReferenceById(paymentId);
        final StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        event.setPayment(payment);
        final Message<PaymentEvent> message = withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        sm.sendEvent(message);
        return sm;
    }

    private StateMachine<PaymentState, PaymentEvent> build(final Long paymentId) {
        final Payment payment = repository.getReferenceById(paymentId);
        final StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(Long.toString(payment.getId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(interceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(
                            payment.getState(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
