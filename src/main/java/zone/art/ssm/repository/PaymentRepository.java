package zone.art.ssm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zone.art.ssm.domain.Payment;

/**
 * @author Art Kart
 * @since 21.05.2022
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
