package campus.academy.exofinal.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import campus.academy.exofinal.IntegrationTest;
import campus.academy.exofinal.domain.SpaceEvent;
import campus.academy.exofinal.domain.enumeration.SpaceEventType;
import campus.academy.exofinal.repository.SpaceEventRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link SpaceEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SpaceEventResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final SpaceEventType DEFAULT_TYPE = SpaceEventType.LAUNCH;
    private static final SpaceEventType UPDATED_TYPE = SpaceEventType.LANDING;

    private static final String ENTITY_API_URL = "/api/space-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpaceEventRepository spaceEventRepository;

    @Mock
    private SpaceEventRepository spaceEventRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpaceEventMockMvc;

    private SpaceEvent spaceEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpaceEvent createEntity(EntityManager em) {
        SpaceEvent spaceEvent = new SpaceEvent()
            .name(DEFAULT_NAME)
            .date(DEFAULT_DATE)
            .description(DEFAULT_DESCRIPTION)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .type(DEFAULT_TYPE);
        return spaceEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SpaceEvent createUpdatedEntity(EntityManager em) {
        SpaceEvent spaceEvent = new SpaceEvent()
            .name(UPDATED_NAME)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE);
        return spaceEvent;
    }

    @BeforeEach
    public void initTest() {
        spaceEvent = createEntity(em);
    }

    @Test
    @Transactional
    void createSpaceEvent() throws Exception {
        int databaseSizeBeforeCreate = spaceEventRepository.findAll().size();
        // Create the SpaceEvent
        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaceEvent)))
            .andExpect(status().isCreated());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeCreate + 1);
        SpaceEvent testSpaceEvent = spaceEventList.get(spaceEventList.size() - 1);
        assertThat(testSpaceEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpaceEvent.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testSpaceEvent.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSpaceEvent.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testSpaceEvent.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testSpaceEvent.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createSpaceEventWithExistingId() throws Exception {
        // Create the SpaceEvent with an existing ID
        spaceEvent.setId(1L);

        int databaseSizeBeforeCreate = spaceEventRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaceEvent)))
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = spaceEventRepository.findAll().size();
        // set the field null
        spaceEvent.setName(null);

        // Create the SpaceEvent, which fails.

        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaceEvent)))
            .andExpect(status().isBadRequest());

        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = spaceEventRepository.findAll().size();
        // set the field null
        spaceEvent.setDate(null);

        // Create the SpaceEvent, which fails.

        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaceEvent)))
            .andExpect(status().isBadRequest());

        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = spaceEventRepository.findAll().size();
        // set the field null
        spaceEvent.setType(null);

        // Create the SpaceEvent, which fails.

        restSpaceEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaceEvent)))
            .andExpect(status().isBadRequest());

        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSpaceEvents() throws Exception {
        // Initialize the database
        spaceEventRepository.saveAndFlush(spaceEvent);

        // Get all the spaceEventList
        restSpaceEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spaceEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpaceEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(spaceEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpaceEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(spaceEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpaceEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(spaceEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpaceEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(spaceEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getSpaceEvent() throws Exception {
        // Initialize the database
        spaceEventRepository.saveAndFlush(spaceEvent);

        // Get the spaceEvent
        restSpaceEventMockMvc
            .perform(get(ENTITY_API_URL_ID, spaceEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(spaceEvent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSpaceEvent() throws Exception {
        // Get the spaceEvent
        restSpaceEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSpaceEvent() throws Exception {
        // Initialize the database
        spaceEventRepository.saveAndFlush(spaceEvent);

        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();

        // Update the spaceEvent
        SpaceEvent updatedSpaceEvent = spaceEventRepository.findById(spaceEvent.getId()).get();
        // Disconnect from session so that the updates on updatedSpaceEvent are not directly saved in db
        em.detach(updatedSpaceEvent);
        updatedSpaceEvent
            .name(UPDATED_NAME)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE);

        restSpaceEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSpaceEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSpaceEvent))
            )
            .andExpect(status().isOk());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
        SpaceEvent testSpaceEvent = spaceEventList.get(spaceEventList.size() - 1);
        assertThat(testSpaceEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpaceEvent.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testSpaceEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSpaceEvent.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testSpaceEvent.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testSpaceEvent.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingSpaceEvent() throws Exception {
        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();
        spaceEvent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, spaceEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spaceEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpaceEvent() throws Exception {
        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();
        spaceEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(spaceEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpaceEvent() throws Exception {
        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();
        spaceEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(spaceEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpaceEventWithPatch() throws Exception {
        // Initialize the database
        spaceEventRepository.saveAndFlush(spaceEvent);

        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();

        // Update the spaceEvent using partial update
        SpaceEvent partialUpdatedSpaceEvent = new SpaceEvent();
        partialUpdatedSpaceEvent.setId(spaceEvent.getId());

        partialUpdatedSpaceEvent
            .description(UPDATED_DESCRIPTION)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE);

        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaceEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpaceEvent))
            )
            .andExpect(status().isOk());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
        SpaceEvent testSpaceEvent = spaceEventList.get(spaceEventList.size() - 1);
        assertThat(testSpaceEvent.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpaceEvent.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testSpaceEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSpaceEvent.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testSpaceEvent.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testSpaceEvent.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateSpaceEventWithPatch() throws Exception {
        // Initialize the database
        spaceEventRepository.saveAndFlush(spaceEvent);

        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();

        // Update the spaceEvent using partial update
        SpaceEvent partialUpdatedSpaceEvent = new SpaceEvent();
        partialUpdatedSpaceEvent.setId(spaceEvent.getId());

        partialUpdatedSpaceEvent
            .name(UPDATED_NAME)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .type(UPDATED_TYPE);

        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpaceEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpaceEvent))
            )
            .andExpect(status().isOk());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
        SpaceEvent testSpaceEvent = spaceEventList.get(spaceEventList.size() - 1);
        assertThat(testSpaceEvent.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpaceEvent.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testSpaceEvent.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSpaceEvent.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testSpaceEvent.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testSpaceEvent.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingSpaceEvent() throws Exception {
        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();
        spaceEvent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, spaceEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spaceEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpaceEvent() throws Exception {
        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();
        spaceEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(spaceEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpaceEvent() throws Exception {
        int databaseSizeBeforeUpdate = spaceEventRepository.findAll().size();
        spaceEvent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpaceEventMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(spaceEvent))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SpaceEvent in the database
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpaceEvent() throws Exception {
        // Initialize the database
        spaceEventRepository.saveAndFlush(spaceEvent);

        int databaseSizeBeforeDelete = spaceEventRepository.findAll().size();

        // Delete the spaceEvent
        restSpaceEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, spaceEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SpaceEvent> spaceEventList = spaceEventRepository.findAll();
        assertThat(spaceEventList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
