package campus.academy.exofinal.repository;

import campus.academy.exofinal.domain.Mission;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Mission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {}
