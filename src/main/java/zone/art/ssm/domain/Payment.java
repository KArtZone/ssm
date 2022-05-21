package zone.art.ssm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.math.BigDecimal;

import static javax.persistence.EnumType.STRING;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(STRING)
    private PaymentState state = PaymentState.NEW;

    private BigDecimal amount;
}
