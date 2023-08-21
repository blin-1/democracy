package com.osi.democracy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.osi.democracy.IntegrationTest;
import com.osi.democracy.domain.Office;
import com.osi.democracy.domain.enumeration.State;
import com.osi.democracy.repository.OfficeRepository;
import com.osi.democracy.service.dto.OfficeDTO;
import com.osi.democracy.service.mapper.OfficeMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OfficeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OfficeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MUNICIPALITY = "AAAAAAAAAA";
    private static final String UPDATED_MUNICIPALITY = "BBBBBBBBBB";

    private static final State DEFAULT_STATE = State.AL;
    private static final State UPDATED_STATE = State.NJ;

    private static final Instant DEFAULT_ELECTION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ELECTION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/offices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private OfficeMapper officeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOfficeMockMvc;

    private Office office;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Office createEntity(EntityManager em) {
        Office office = new Office()
            .name(DEFAULT_NAME)
            .municipality(DEFAULT_MUNICIPALITY)
            .state(DEFAULT_STATE)
            .electionDate(DEFAULT_ELECTION_DATE);
        return office;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Office createUpdatedEntity(EntityManager em) {
        Office office = new Office()
            .name(UPDATED_NAME)
            .municipality(UPDATED_MUNICIPALITY)
            .state(UPDATED_STATE)
            .electionDate(UPDATED_ELECTION_DATE);
        return office;
    }

    @BeforeEach
    public void initTest() {
        office = createEntity(em);
    }

    @Test
    @Transactional
    void createOffice() throws Exception {
        int databaseSizeBeforeCreate = officeRepository.findAll().size();
        // Create the Office
        OfficeDTO officeDTO = officeMapper.toDto(office);
        restOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(officeDTO)))
            .andExpect(status().isCreated());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeCreate + 1);
        Office testOffice = officeList.get(officeList.size() - 1);
        assertThat(testOffice.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOffice.getMunicipality()).isEqualTo(DEFAULT_MUNICIPALITY);
        assertThat(testOffice.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testOffice.getElectionDate()).isEqualTo(DEFAULT_ELECTION_DATE);
    }

    @Test
    @Transactional
    void createOfficeWithExistingId() throws Exception {
        // Create the Office with an existing ID
        office.setId(1L);
        OfficeDTO officeDTO = officeMapper.toDto(office);

        int databaseSizeBeforeCreate = officeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOfficeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(officeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOffices() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        // Get all the officeList
        restOfficeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(office.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].municipality").value(hasItem(DEFAULT_MUNICIPALITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].electionDate").value(hasItem(DEFAULT_ELECTION_DATE.toString())));
    }

    @Test
    @Transactional
    void getOffice() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        // Get the office
        restOfficeMockMvc
            .perform(get(ENTITY_API_URL_ID, office.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(office.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.municipality").value(DEFAULT_MUNICIPALITY))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.electionDate").value(DEFAULT_ELECTION_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOffice() throws Exception {
        // Get the office
        restOfficeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOffice() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        int databaseSizeBeforeUpdate = officeRepository.findAll().size();

        // Update the office
        Office updatedOffice = officeRepository.findById(office.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOffice are not directly saved in db
        em.detach(updatedOffice);
        updatedOffice.name(UPDATED_NAME).municipality(UPDATED_MUNICIPALITY).state(UPDATED_STATE).electionDate(UPDATED_ELECTION_DATE);
        OfficeDTO officeDTO = officeMapper.toDto(updatedOffice);

        restOfficeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, officeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(officeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
        Office testOffice = officeList.get(officeList.size() - 1);
        assertThat(testOffice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOffice.getMunicipality()).isEqualTo(UPDATED_MUNICIPALITY);
        assertThat(testOffice.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOffice.getElectionDate()).isEqualTo(UPDATED_ELECTION_DATE);
    }

    @Test
    @Transactional
    void putNonExistingOffice() throws Exception {
        int databaseSizeBeforeUpdate = officeRepository.findAll().size();
        office.setId(count.incrementAndGet());

        // Create the Office
        OfficeDTO officeDTO = officeMapper.toDto(office);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOfficeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, officeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(officeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOffice() throws Exception {
        int databaseSizeBeforeUpdate = officeRepository.findAll().size();
        office.setId(count.incrementAndGet());

        // Create the Office
        OfficeDTO officeDTO = officeMapper.toDto(office);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(officeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOffice() throws Exception {
        int databaseSizeBeforeUpdate = officeRepository.findAll().size();
        office.setId(count.incrementAndGet());

        // Create the Office
        OfficeDTO officeDTO = officeMapper.toDto(office);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(officeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOfficeWithPatch() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        int databaseSizeBeforeUpdate = officeRepository.findAll().size();

        // Update the office using partial update
        Office partialUpdatedOffice = new Office();
        partialUpdatedOffice.setId(office.getId());

        partialUpdatedOffice.name(UPDATED_NAME).state(UPDATED_STATE);

        restOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOffice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOffice))
            )
            .andExpect(status().isOk());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
        Office testOffice = officeList.get(officeList.size() - 1);
        assertThat(testOffice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOffice.getMunicipality()).isEqualTo(DEFAULT_MUNICIPALITY);
        assertThat(testOffice.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOffice.getElectionDate()).isEqualTo(DEFAULT_ELECTION_DATE);
    }

    @Test
    @Transactional
    void fullUpdateOfficeWithPatch() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        int databaseSizeBeforeUpdate = officeRepository.findAll().size();

        // Update the office using partial update
        Office partialUpdatedOffice = new Office();
        partialUpdatedOffice.setId(office.getId());

        partialUpdatedOffice.name(UPDATED_NAME).municipality(UPDATED_MUNICIPALITY).state(UPDATED_STATE).electionDate(UPDATED_ELECTION_DATE);

        restOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOffice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOffice))
            )
            .andExpect(status().isOk());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
        Office testOffice = officeList.get(officeList.size() - 1);
        assertThat(testOffice.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOffice.getMunicipality()).isEqualTo(UPDATED_MUNICIPALITY);
        assertThat(testOffice.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOffice.getElectionDate()).isEqualTo(UPDATED_ELECTION_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingOffice() throws Exception {
        int databaseSizeBeforeUpdate = officeRepository.findAll().size();
        office.setId(count.incrementAndGet());

        // Create the Office
        OfficeDTO officeDTO = officeMapper.toDto(office);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, officeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(officeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOffice() throws Exception {
        int databaseSizeBeforeUpdate = officeRepository.findAll().size();
        office.setId(count.incrementAndGet());

        // Create the Office
        OfficeDTO officeDTO = officeMapper.toDto(office);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(officeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOffice() throws Exception {
        int databaseSizeBeforeUpdate = officeRepository.findAll().size();
        office.setId(count.incrementAndGet());

        // Create the Office
        OfficeDTO officeDTO = officeMapper.toDto(office);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOfficeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(officeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Office in the database
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOffice() throws Exception {
        // Initialize the database
        officeRepository.saveAndFlush(office);

        int databaseSizeBeforeDelete = officeRepository.findAll().size();

        // Delete the office
        restOfficeMockMvc
            .perform(delete(ENTITY_API_URL_ID, office.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Office> officeList = officeRepository.findAll();
        assertThat(officeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
