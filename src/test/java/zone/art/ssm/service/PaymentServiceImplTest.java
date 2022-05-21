package zone.art.ssm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import zone.art.ssm.domain.Payment;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;
import zone.art.ssm.repository.PaymentRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static zone.art.ssm.domain.PaymentState.*;
import static zone.art.ssm.utils.StateMachineUtils.getId;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository repository;

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    Payment payment;

    // изменить на значение меньше чем "9999.42" чтобы протестировать ветки ошибок
    final static BigDecimal AMOUNT = new BigDecimal("9999.41");

    @BeforeEach
    void before() {
        payment = Payment.builder()
                .amount(AMOUNT)
                .state(NEW)
                .build();
    }

    @Test
    @Transactional
    void smFlowTest() {
        final Payment payment = paymentService.newPayment(this.payment);
        final Long paymentId = payment.getId();
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(String.valueOf(paymentId));
        Payment fromDB = repository.getReferenceById(paymentId);
        // Создание платежа. SM и Payment в статусе NEW
        assertThat(fromDB.getState()).isEqualTo(NEW);
        assertThat(getId(sm)).isEqualTo(NEW);

        sm = paymentService.preAuth(paymentId);
        fromDB = repository.getReferenceById(paymentId);

        assertThat(fromDB.getAmount()).isEqualTo(AMOUNT);

        if (payment.getAmount().compareTo(BigDecimal.valueOf(9999.42)) >= 0) {
            // Предварительная обработка. SM и Payment в статусе PRE_AUTH
            assertThat(fromDB.getState()).isEqualTo(PRE_AUTH);
            assertThat(getId(sm)).isEqualTo(PRE_AUTH);
        } else {
            // Неудачная предварительная обработка.
            // SM и Payment в статусе PRE_AUTH_ERROR
            assertThat(fromDB.getState()).isEqualTo(PRE_AUTH_ERROR);
            assertThat(getId(sm)).isEqualTo(PRE_AUTH_ERROR);
            return;
        }

        sm = paymentService.authorize(paymentId);
        fromDB = repository.getReferenceById(paymentId);
        if (payment.getAmount().compareTo(BigDecimal.valueOf(9999.42)) >= 0) {
            // Платеш успешно проведен. SM и Payment в статусе AUTH
            assertThat(fromDB.getState()).isEqualTo(AUTH);
            assertThat(getId(sm)).isEqualTo(AUTH);
        } else {
            // Платеш не проведен.
            // SM и Payment в статусе AUTH_ERROR
            assertThat(fromDB.getState()).isEqualTo(AUTH_ERROR);
            assertThat(getId(sm)).isEqualTo(AUTH_ERROR);
        }
    }
}