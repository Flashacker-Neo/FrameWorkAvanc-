package campus.academy.exofinal.repository;

import campus.academy.exofinal.domain.SpaceEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SpaceEvent entity.
 */
@Repository
public interface SpaceEventRepository extends JpaRepository<SpaceEvent, Long> {
    default Optional<SpaceEvent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SpaceEvent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SpaceEvent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct spaceEvent from SpaceEvent spaceEvent left join fetch spaceEvent.mission",
        countQuery = "select count(distinct spaceEvent) from SpaceEvent spaceEvent"
    )
    Page<SpaceEvent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct spaceEvent from SpaceEvent spaceEvent left join fetch spaceEvent.mission")
    List<SpaceEvent> findAllWithToOneRelationships();

    @Query("select spaceEvent from SpaceEvent spaceEvent left join fetch spaceEvent.mission where spaceEvent.id =:id")
    Optional<SpaceEvent> findOneWithToOneRelationships(@Param("id") Long id);
}
