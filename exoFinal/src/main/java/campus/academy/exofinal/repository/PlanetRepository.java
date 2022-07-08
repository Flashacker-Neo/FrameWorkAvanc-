package campus.academy.exofinal.repository;

import campus.academy.exofinal.domain.Planet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Planet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlanetRepository extends JpaRepository<Planet, Long> {}
