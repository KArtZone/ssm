package zone.art.ssm.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewMachine() {
        final StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine();
        stateMachine.start();
        System.out.println(stateMachine.getState().toString());
        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        System.out.println(stateMachine.getState().toString());
        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        System.out.println(stateMachine.getState());
    }
}