package zone.art.ssm.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
public enum PaymentEvent {

    PRE_AUTHORIZE,
    PRE_AUTH_APPROVED,
    PRE_AUTH_DECLINED,
    AUTHORIZE,
    AUTH_APPROVED,
    AUTH_DECLINED;

    @Setter
    @Getter
    private Payment payment;
}
