package campus.academy.exofinal.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import campus.academy.exofinal.IntegrationTest;
import campus.academy.exofinal.domain.Mission;
import campus.academy.exofinal.repository.MissionRepository;
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
 * Integration tests for the {@link MissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MissionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/missions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMissionMockMvc;

    private Mission mission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createEntity(EntityManager em) {
        Mission mission = new Mission().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return mission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mission createUpdatedEntity(EntityManager em) {
        Mission mission = new Mission().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return mission;
    }

    @BeforeEach
    public void initTest() {
        mission = createEntity(em);
    }

    @Test
    @Transactional
    void createMission() throws Exception {
        int databaseSizeBeforeCreate = missionRepository.findAll().size();
        // Create the Mission
        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mission)))
            .andExpect(status().isCreated());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeCreate + 1);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMission.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createMissionWithExistingId() throws Exception {
        // Create the Mission with an existing ID
        mission.setId(1L);

        int databaseSizeBeforeCreate = missionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mission)))
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = missionRepository.findAll().size();
        // set the field null
        mission.setName(null);

        // Create the Mission, which fails.

        restMissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mission)))
            .andExpect(status().isBadRequest());

        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMissions() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get all the missionList
        restMissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mission.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getMission() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        // Get the mission
        restMissionMockMvc
            .perform(get(ENTITY_API_URL_ID, mission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mission.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingMission() throws Exception {
        // Get the mission
        restMissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMission() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeUpdate = missionRepository.findAll().size();

        // Update the mission
        Mission updatedMission = missionRepository.findById(mission.getId()).get();
        // Disconnect from session so that the updates on updatedMission are not directly saved in db
        em.detach(updatedMission);
        updatedMission.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMission.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mission.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mission))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mission))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mission)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeUpdate = missionRepository.findAll().size();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        partialUpdatedMission.description(UPDATED_DESCRIPTION);

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateMissionWithPatch() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeUpdate = missionRepository.findAll().size();

        // Update the mission using partial update
        Mission partialUpdatedMission = new Mission();
        partialUpdatedMission.setId(mission.getId());

        partialUpdatedMission.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMission.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMission))
            )
            .andExpect(status().isOk());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
        Mission testMission = missionList.get(missionList.size() - 1);
        assertThat(testMission.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mission.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mission))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mission))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMission() throws Exception {
        int databaseSizeBeforeUpdate = missionRepository.findAll().size();
        mission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(mission)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mission in the database
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMission() throws Exception {
        // Initialize the database
        missionRepository.saveAndFlush(mission);

        int databaseSizeBeforeDelete = missionRepository.findAll().size();

        // Delete the mission
        restMissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, mission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Mission> missionList = missionRepository.findAll();
        assertThat(missionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
