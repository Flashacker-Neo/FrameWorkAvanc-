package campus.academy.exofinal.web.rest;

import campus.academy.exofinal.domain.Mission;
import campus.academy.exofinal.repository.MissionRepository;
import campus.academy.exofinal.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link campus.academy.exofinal.domain.Mission}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MissionResource {

    private final Logger log = LoggerFactory.getLogger(MissionResource.class);

    private static final String ENTITY_NAME = "mission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MissionRepository missionRepository;

    public MissionResource(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    /**
     * {@code POST  /missions} : Create a new mission.
     *
     * @param mission the mission to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mission, or with status {@code 400 (Bad Request)} if the mission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/missions")
    public ResponseEntity<Mission> createMission(@Valid @RequestBody Mission mission) throws URISyntaxException {
        log.debug("REST request to save Mission : {}", mission);
        if (mission.getId() != null) {
            throw new BadRequestAlertException("A new mission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Mission result = missionRepository.save(mission);
        return ResponseEntity
            .created(new URI("/api/missions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /missions/:id} : Updates an existing mission.
     *
     * @param id the id of the mission to save.
     * @param mission the mission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mission,
     * or with status {@code 400 (Bad Request)} if the mission is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/missions/{id}")
    public ResponseEntity<Mission> updateMission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Mission mission
    ) throws URISyntaxException {
        log.debug("REST request to update Mission : {}, {}", id, mission);
        if (mission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Mission result = missionRepository.save(mission);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, mission.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /missions/:id} : Partial updates given fields of an existing mission, field will ignore if it is null
     *
     * @param id the id of the mission to save.
     * @param mission the mission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mission,
     * or with status {@code 400 (Bad Request)} if the mission is not valid,
     * or with status {@code 404 (Not Found)} if the mission is not found,
     * or with status {@code 500 (Internal Server Error)} if the mission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/missions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Mission> partialUpdateMission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Mission mission
    ) throws URISyntaxException {
        log.debug("REST request to partial update Mission partially : {}, {}", id, mission);
        if (mission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!missionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Mission> result = missionRepository
            .findById(mission.getId())
            .map(existingMission -> {
                if (mission.getName() != null) {
                    existingMission.setName(mission.getName());
                }
                if (mission.getDescription() != null) {
                    existingMission.setDescription(mission.getDescription());
                }

                return existingMission;
            })
            .map(missionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, mission.getId().toString())
        );
    }

    /**
     * {@code GET  /missions} : get all the missions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of missions in body.
     */
    @GetMapping("/missions")
    public ResponseEntity<List<Mission>> getAllMissions(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Missions");
        Page<Mission> page = missionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /missions/:id} : get the "id" mission.
     *
     * @param id the id of the mission to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mission, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/missions/{id}")
    public ResponseEntity<Mission> getMission(@PathVariable Long id) {
        log.debug("REST request to get Mission : {}", id);
        Optional<Mission> mission = missionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mission);
    }

    /**
     * {@code DELETE  /missions/:id} : delete the "id" mission.
     *
     * @param id the id of the mission to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/missions/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        log.debug("REST request to delete Mission : {}", id);
        missionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
