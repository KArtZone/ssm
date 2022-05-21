package zone.art.ssm.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.AuthorizeAction;
import org.springframework.statemachine.action.PreAuthorizeAction;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
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
@RequiredArgsConstructor
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
                .withExternal()
                .source(NEW)
                .target(NEW)
                .event(PRE_AUTHORIZE)
                .action(new PreAuthorizeAction())
                .and()
                .withExternal()
                .source(NEW)
                .target(PRE_AUTH)
                .event(PRE_AUTH_APPROVED)
                .and()
                .withExternal()
                .source(NEW)
                .target(PRE_AUTH_ERROR)
                .event(PRE_AUTH_DECLINED)
                .and()
                .withExternal()
                .source(PRE_AUTH)
                .target(PRE_AUTH)
                .event(AUTHORIZE)
                .action(new AuthorizeAction())
                .and()
                .withExternal()
                .source(PRE_AUTH)
                .target(AUTH)
                .event(AUTH_APPROVED)
                .and()
                .withExternal()
                .source(PRE_AUTH)
                .target(AUTH_ERROR)
                .event(AUTH_DECLINED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        final StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info("SM state changed from {} to {}", from, to);
            }
        };
        config.withConfiguration()
                .autoStartup(true)
                .listener(adapter);
    }

}

