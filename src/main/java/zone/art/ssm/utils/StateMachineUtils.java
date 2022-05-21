package zone.art.ssm.utils;

import org.springframework.statemachine.StateMachine;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
public class StateMachineUtils {

    private StateMachineUtils() {
    }

    public static PaymentState getId(StateMachine<PaymentState, PaymentEvent> sm) {
        return sm.getState().getId();
    }
}
