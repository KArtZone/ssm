package zone.art.ssm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import zone.art.ssm.domain.PaymentEvent;
import zone.art.ssm.domain.PaymentState;

import java.util.EnumSet;

import static zone.art.ssm.domain.PaymentEvent.*;
import static zone.art.ssm.domain.PaymentState.*;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
@Configuration
@Slf4j
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(AUTH)
                .end(PRE_AUTH_ERROR)
                .end(AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(NEW).target(NEW).event(PRE_AUTHORIZE)
                .and()
                .withExternal().source(NEW).target(PRE_AUTH).event(PRE_AUTH_APPROVED)
                .and()
                .withExternal().source(NEW).target(PRE_AUTH_ERROR).event(PRE_AUTH_DECLINED);
    }
}

