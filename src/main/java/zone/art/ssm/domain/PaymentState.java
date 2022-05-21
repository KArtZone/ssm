package zone.art.ssm.domain;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
public enum PaymentState {

    NEW,
    PRE_AUTH,
    PRE_AUTH_ERROR,
    AUTH,
    AUTH_ERROR
}
