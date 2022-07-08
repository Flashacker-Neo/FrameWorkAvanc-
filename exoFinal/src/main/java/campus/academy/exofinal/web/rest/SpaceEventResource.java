package campus.academy.exofinal.web.rest;

import campus.academy.exofinal.domain.SpaceEvent;
import campus.academy.exofinal.repository.SpaceEventRepository;
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
 * REST controller for managing {@link campus.academy.exofinal.domain.SpaceEvent}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SpaceEventResource {

    private final Logger log = LoggerFactory.getLogger(SpaceEventResource.class);

    private static final String ENTITY_NAME = "spaceEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpaceEventRepository spaceEventRepository;

    public SpaceEventResource(SpaceEventRepository spaceEventRepository) {
        this.spaceEventRepository = spaceEventRepository;
    }

    /**
     * {@code POST  /space-events} : Create a new spaceEvent.
     *
     * @param spaceEvent the spaceEvent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spaceEvent, or with status {@code 400 (Bad Request)} if the spaceEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/space-events")
    public ResponseEntity<SpaceEvent> createSpaceEvent(@Valid @RequestBody SpaceEvent spaceEvent) throws URISyntaxException {
        log.debug("REST request to save SpaceEvent : {}", spaceEvent);
        if (spaceEvent.getId() != null) {
            throw new BadRequestAlertException("A new spaceEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SpaceEvent result = spaceEventRepository.save(spaceEvent);
        return ResponseEntity
            .created(new URI("/api/space-events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /space-events/:id} : Updates an existing spaceEvent.
     *
     * @param id the id of the spaceEvent to save.
     * @param spaceEvent the spaceEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaceEvent,
     * or with status {@code 400 (Bad Request)} if the spaceEvent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spaceEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/space-events/{id}")
    public ResponseEntity<SpaceEvent> updateSpaceEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SpaceEvent spaceEvent
    ) throws URISyntaxException {
        log.debug("REST request to update SpaceEvent : {}, {}", id, spaceEvent);
        if (spaceEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaceEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spaceEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SpaceEvent result = spaceEventRepository.save(spaceEvent);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spaceEvent.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /space-events/:id} : Partial updates given fields of an existing spaceEvent, field will ignore if it is null
     *
     * @param id the id of the spaceEvent to save.
     * @param spaceEvent the spaceEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spaceEvent,
     * or with status {@code 400 (Bad Request)} if the spaceEvent is not valid,
     * or with status {@code 404 (Not Found)} if the spaceEvent is not found,
     * or with status {@code 500 (Internal Server Error)} if the spaceEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/space-events/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SpaceEvent> partialUpdateSpaceEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SpaceEvent spaceEvent
    ) throws URISyntaxException {
        log.debug("REST request to partial update SpaceEvent partially : {}, {}", id, spaceEvent);
        if (spaceEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, spaceEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!spaceEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SpaceEvent> result = spaceEventRepository
            .findById(spaceEvent.getId())
            .map(existingSpaceEvent -> {
                if (spaceEvent.getName() != null) {
                    existingSpaceEvent.setName(spaceEvent.getName());
                }
                if (spaceEvent.getDate() != null) {
                    existingSpaceEvent.setDate(spaceEvent.getDate());
                }
                if (spaceEvent.getDescription() != null) {
                    existingSpaceEvent.setDescription(spaceEvent.getDescription());
                }
                if (spaceEvent.getPhoto() != null) {
                    existingSpaceEvent.setPhoto(spaceEvent.getPhoto());
                }
                if (spaceEvent.getPhotoContentType() != null) {
                    existingSpaceEvent.setPhotoContentType(spaceEvent.getPhotoContentType());
                }
                if (spaceEvent.getType() != null) {
                    existingSpaceEvent.setType(spaceEvent.getType());
                }

                return existingSpaceEvent;
            })
            .map(spaceEventRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spaceEvent.getId().toString())
        );
    }

    /**
     * {@code GET  /space-events} : get all the spaceEvents.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spaceEvents in body.
     */
    @GetMapping("/space-events")
    public ResponseEntity<List<SpaceEvent>> getAllSpaceEvents(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of SpaceEvents");
        Page<SpaceEvent> page;
        if (eagerload) {
            page = spaceEventRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = spaceEventRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /space-events/:id} : get the "id" spaceEvent.
     *
     * @param id the id of the spaceEvent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spaceEvent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/space-events/{id}")
    public ResponseEntity<SpaceEvent> getSpaceEvent(@PathVariable Long id) {
        log.debug("REST request to get SpaceEvent : {}", id);
        Optional<SpaceEvent> spaceEvent = spaceEventRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(spaceEvent);
    }

    /**
     * {@code DELETE  /space-events/:id} : delete the "id" spaceEvent.
     *
     * @param id the id of the spaceEvent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/space-events/{id}")
    public ResponseEntity<Void> deleteSpaceEvent(@PathVariable Long id) {
        log.debug("REST request to delete SpaceEvent : {}", id);
        spaceEventRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
