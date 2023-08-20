package com.osi.democracy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.osi.democracy.IntegrationTest;
import com.osi.democracy.domain.Candidate;
import com.osi.democracy.repository.CandidateRepository;
import com.osi.democracy.service.dto.CandidateDTO;
import com.osi.democracy.service.mapper.CandidateMapper;
import jakarta.persistence.EntityManager;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link CandidateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CandidateResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PIC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PIC = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PIC_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PIC_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/candidates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateMapper candidateMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCandidateMockMvc;

    private Candidate candidate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidate createEntity(EntityManager em) {
        Candidate candidate = new Candidate()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .pic(DEFAULT_PIC)
            .picContentType(DEFAULT_PIC_CONTENT_TYPE);
        return candidate;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidate createUpdatedEntity(EntityManager em) {
        Candidate candidate = new Candidate()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .pic(UPDATED_PIC)
            .picContentType(UPDATED_PIC_CONTENT_TYPE);
        return candidate;
    }

    @BeforeEach
    public void initTest() {
        candidate = createEntity(em);
    }

    @Test
    @Transactional
    void createCandidate() throws Exception {
        int databaseSizeBeforeCreate = candidateRepository.findAll().size();
        // Create the Candidate
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);
        restCandidateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candidateDTO)))
            .andExpect(status().isCreated());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeCreate + 1);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCandidate.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCandidate.getPic()).isEqualTo(DEFAULT_PIC);
        assertThat(testCandidate.getPicContentType()).isEqualTo(DEFAULT_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createCandidateWithExistingId() throws Exception {
        // Create the Candidate with an existing ID
        candidate.setId(1L);
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        int databaseSizeBeforeCreate = candidateRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCandidateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candidateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateRepository.findAll().size();
        // set the field null
        candidate.setFirstName(null);

        // Create the Candidate, which fails.
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        restCandidateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candidateDTO)))
            .andExpect(status().isBadRequest());

        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateRepository.findAll().size();
        // set the field null
        candidate.setLastName(null);

        // Create the Candidate, which fails.
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        restCandidateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candidateDTO)))
            .andExpect(status().isBadRequest());

        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = candidateRepository.findAll().size();
        // set the field null
        candidate.setEmail(null);

        // Create the Candidate, which fails.
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        restCandidateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candidateDTO)))
            .andExpect(status().isBadRequest());

        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCandidates() throws Exception {
        // Initialize the database
        candidateRepository.saveAndFlush(candidate);

        // Get all the candidateList
        restCandidateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidate.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].picContentType").value(hasItem(DEFAULT_PIC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pic").value(hasItem(Base64Utils.encodeToString(DEFAULT_PIC))));
    }

    @Test
    @Transactional
    void getCandidate() throws Exception {
        // Initialize the database
        candidateRepository.saveAndFlush(candidate);

        // Get the candidate
        restCandidateMockMvc
            .perform(get(ENTITY_API_URL_ID, candidate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(candidate.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.picContentType").value(DEFAULT_PIC_CONTENT_TYPE))
            .andExpect(jsonPath("$.pic").value(Base64Utils.encodeToString(DEFAULT_PIC)));
    }

    @Test
    @Transactional
    void getNonExistingCandidate() throws Exception {
        // Get the candidate
        restCandidateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCandidate() throws Exception {
        // Initialize the database
        candidateRepository.saveAndFlush(candidate);

        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();

        // Update the candidate
        Candidate updatedCandidate = candidateRepository.findById(candidate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCandidate are not directly saved in db
        em.detach(updatedCandidate);
        updatedCandidate
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .pic(UPDATED_PIC)
            .picContentType(UPDATED_PIC_CONTENT_TYPE);
        CandidateDTO candidateDTO = candidateMapper.toDto(updatedCandidate);

        restCandidateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(candidateDTO))
            )
            .andExpect(status().isOk());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCandidate.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCandidate.getPic()).isEqualTo(UPDATED_PIC);
        assertThat(testCandidate.getPicContentType()).isEqualTo(UPDATED_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();
        candidate.setId(count.incrementAndGet());

        // Create the Candidate
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(candidateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();
        candidate.setId(count.incrementAndGet());

        // Create the Candidate
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(candidateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();
        candidate.setId(count.incrementAndGet());

        // Create the Candidate
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(candidateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCandidateWithPatch() throws Exception {
        // Initialize the database
        candidateRepository.saveAndFlush(candidate);

        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();

        // Update the candidate using partial update
        Candidate partialUpdatedCandidate = new Candidate();
        partialUpdatedCandidate.setId(candidate.getId());

        partialUpdatedCandidate.pic(UPDATED_PIC).picContentType(UPDATED_PIC_CONTENT_TYPE);

        restCandidateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCandidate))
            )
            .andExpect(status().isOk());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCandidate.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCandidate.getPic()).isEqualTo(UPDATED_PIC);
        assertThat(testCandidate.getPicContentType()).isEqualTo(UPDATED_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateCandidateWithPatch() throws Exception {
        // Initialize the database
        candidateRepository.saveAndFlush(candidate);

        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();

        // Update the candidate using partial update
        Candidate partialUpdatedCandidate = new Candidate();
        partialUpdatedCandidate.setId(candidate.getId());

        partialUpdatedCandidate
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .pic(UPDATED_PIC)
            .picContentType(UPDATED_PIC_CONTENT_TYPE);

        restCandidateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidate.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCandidate))
            )
            .andExpect(status().isOk());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
        Candidate testCandidate = candidateList.get(candidateList.size() - 1);
        assertThat(testCandidate.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCandidate.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCandidate.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCandidate.getPic()).isEqualTo(UPDATED_PIC);
        assertThat(testCandidate.getPicContentType()).isEqualTo(UPDATED_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();
        candidate.setId(count.incrementAndGet());

        // Create the Candidate
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, candidateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(candidateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();
        candidate.setId(count.incrementAndGet());

        // Create the Candidate
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(candidateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCandidate() throws Exception {
        int databaseSizeBeforeUpdate = candidateRepository.findAll().size();
        candidate.setId(count.incrementAndGet());

        // Create the Candidate
        CandidateDTO candidateDTO = candidateMapper.toDto(candidate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidateMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(candidateDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidate in the database
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCandidate() throws Exception {
        // Initialize the database
        candidateRepository.saveAndFlush(candidate);

        int databaseSizeBeforeDelete = candidateRepository.findAll().size();

        // Delete the candidate
        restCandidateMockMvc
            .perform(delete(ENTITY_API_URL_ID, candidate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Candidate> candidateList = candidateRepository.findAll();
        assertThat(candidateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
