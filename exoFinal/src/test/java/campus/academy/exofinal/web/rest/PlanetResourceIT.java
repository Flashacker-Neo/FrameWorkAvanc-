package campus.academy.exofinal.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import campus.academy.exofinal.IntegrationTest;
import campus.academy.exofinal.domain.Planet;
import campus.academy.exofinal.domain.enumeration.PlanetTypes;
import campus.academy.exofinal.repository.PlanetRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PlanetResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlanetResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_DISTANCE = 1L;
    private static final Long UPDATED_DISTANCE = 2L;

    private static final PlanetTypes DEFAULT_TYPE = PlanetTypes.GAZ;
    private static final PlanetTypes UPDATED_TYPE = PlanetTypes.STONE;

    private static final Instant DEFAULT_SATELLITE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SATELLITE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/planets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlanetMockMvc;

    private Planet planet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Planet createEntity(EntityManager em) {
        Planet planet = new Planet().name(DEFAULT_NAME).distance(DEFAULT_DISTANCE).type(DEFAULT_TYPE).satellite(DEFAULT_SATELLITE);
        return planet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Planet createUpdatedEntity(EntityManager em) {
        Planet planet = new Planet().name(UPDATED_NAME).distance(UPDATED_DISTANCE).type(UPDATED_TYPE).satellite(UPDATED_SATELLITE);
        return planet;
    }

    @BeforeEach
    public void initTest() {
        planet = createEntity(em);
    }

    @Test
    @Transactional
    void createPlanet() throws Exception {
        int databaseSizeBeforeCreate = planetRepository.findAll().size();
        // Create the Planet
        restPlanetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(planet)))
            .andExpect(status().isCreated());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeCreate + 1);
        Planet testPlanet = planetList.get(planetList.size() - 1);
        assertThat(testPlanet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlanet.getDistance()).isEqualTo(DEFAULT_DISTANCE);
        assertThat(testPlanet.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testPlanet.getSatellite()).isEqualTo(DEFAULT_SATELLITE);
    }

    @Test
    @Transactional
    void createPlanetWithExistingId() throws Exception {
        // Create the Planet with an existing ID
        planet.setId(1L);

        int databaseSizeBeforeCreate = planetRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlanetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(planet)))
            .andExpect(status().isBadRequest());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = planetRepository.findAll().size();
        // set the field null
        planet.setName(null);

        // Create the Planet, which fails.

        restPlanetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(planet)))
            .andExpect(status().isBadRequest());

        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDistanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = planetRepository.findAll().size();
        // set the field null
        planet.setDistance(null);

        // Create the Planet, which fails.

        restPlanetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(planet)))
            .andExpect(status().isBadRequest());

        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = planetRepository.findAll().size();
        // set the field null
        planet.setType(null);

        // Create the Planet, which fails.

        restPlanetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(planet)))
            .andExpect(status().isBadRequest());

        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlanets() throws Exception {
        // Initialize the database
        planetRepository.saveAndFlush(planet);

        // Get all the planetList
        restPlanetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(planet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].distance").value(hasItem(DEFAULT_DISTANCE.intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].satellite").value(hasItem(DEFAULT_SATELLITE.toString())));
    }

    @Test
    @Transactional
    void getPlanet() throws Exception {
        // Initialize the database
        planetRepository.saveAndFlush(planet);

        // Get the planet
        restPlanetMockMvc
            .perform(get(ENTITY_API_URL_ID, planet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(planet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.distance").value(DEFAULT_DISTANCE.intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.satellite").value(DEFAULT_SATELLITE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPlanet() throws Exception {
        // Get the planet
        restPlanetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlanet() throws Exception {
        // Initialize the database
        planetRepository.saveAndFlush(planet);

        int databaseSizeBeforeUpdate = planetRepository.findAll().size();

        // Update the planet
        Planet updatedPlanet = planetRepository.findById(planet.getId()).get();
        // Disconnect from session so that the updates on updatedPlanet are not directly saved in db
        em.detach(updatedPlanet);
        updatedPlanet.name(UPDATED_NAME).distance(UPDATED_DISTANCE).type(UPDATED_TYPE).satellite(UPDATED_SATELLITE);

        restPlanetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlanet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlanet))
            )
            .andExpect(status().isOk());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
        Planet testPlanet = planetList.get(planetList.size() - 1);
        assertThat(testPlanet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlanet.getDistance()).isEqualTo(UPDATED_DISTANCE);
        assertThat(testPlanet.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testPlanet.getSatellite()).isEqualTo(UPDATED_SATELLITE);
    }

    @Test
    @Transactional
    void putNonExistingPlanet() throws Exception {
        int databaseSizeBeforeUpdate = planetRepository.findAll().size();
        planet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, planet.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(planet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlanet() throws Exception {
        int databaseSizeBeforeUpdate = planetRepository.findAll().size();
        planet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(planet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlanet() throws Exception {
        int databaseSizeBeforeUpdate = planetRepository.findAll().size();
        planet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(planet)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlanetWithPatch() throws Exception {
        // Initialize the database
        planetRepository.saveAndFlush(planet);

        int databaseSizeBeforeUpdate = planetRepository.findAll().size();

        // Update the planet using partial update
        Planet partialUpdatedPlanet = new Planet();
        partialUpdatedPlanet.setId(planet.getId());

        partialUpdatedPlanet.distance(UPDATED_DISTANCE);

        restPlanetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlanet))
            )
            .andExpect(status().isOk());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
        Planet testPlanet = planetList.get(planetList.size() - 1);
        assertThat(testPlanet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPlanet.getDistance()).isEqualTo(UPDATED_DISTANCE);
        assertThat(testPlanet.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testPlanet.getSatellite()).isEqualTo(DEFAULT_SATELLITE);
    }

    @Test
    @Transactional
    void fullUpdatePlanetWithPatch() throws Exception {
        // Initialize the database
        planetRepository.saveAndFlush(planet);

        int databaseSizeBeforeUpdate = planetRepository.findAll().size();

        // Update the planet using partial update
        Planet partialUpdatedPlanet = new Planet();
        partialUpdatedPlanet.setId(planet.getId());

        partialUpdatedPlanet.name(UPDATED_NAME).distance(UPDATED_DISTANCE).type(UPDATED_TYPE).satellite(UPDATED_SATELLITE);

        restPlanetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlanet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlanet))
            )
            .andExpect(status().isOk());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
        Planet testPlanet = planetList.get(planetList.size() - 1);
        assertThat(testPlanet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlanet.getDistance()).isEqualTo(UPDATED_DISTANCE);
        assertThat(testPlanet.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testPlanet.getSatellite()).isEqualTo(UPDATED_SATELLITE);
    }

    @Test
    @Transactional
    void patchNonExistingPlanet() throws Exception {
        int databaseSizeBeforeUpdate = planetRepository.findAll().size();
        planet.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlanetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, planet.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(planet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlanet() throws Exception {
        int databaseSizeBeforeUpdate = planetRepository.findAll().size();
        planet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(planet))
            )
            .andExpect(status().isBadRequest());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlanet() throws Exception {
        int databaseSizeBeforeUpdate = planetRepository.findAll().size();
        planet.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlanetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(planet)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Planet in the database
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlanet() throws Exception {
        // Initialize the database
        planetRepository.saveAndFlush(planet);

        int databaseSizeBeforeDelete = planetRepository.findAll().size();

        // Delete the planet
        restPlanetMockMvc
            .perform(delete(ENTITY_API_URL_ID, planet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Planet> planetList = planetRepository.findAll();
        assertThat(planetList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
