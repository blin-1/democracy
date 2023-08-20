package com.osi.democracy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.osi.democracy.IntegrationTest;
import com.osi.democracy.domain.Issue;
import com.osi.democracy.repository.IssueRepository;
import com.osi.democracy.service.dto.IssueDTO;
import com.osi.democracy.service.mapper.IssueMapper;
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

/**
 * Integration tests for the {@link IssueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IssueResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/issues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueMapper issueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIssueMockMvc;

    private Issue issue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createEntity(EntityManager em) {
        Issue issue = new Issue().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return issue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createUpdatedEntity(EntityManager em) {
        Issue issue = new Issue().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return issue;
    }

    @BeforeEach
    public void initTest() {
        issue = createEntity(em);
    }

    @Test
    @Transactional
    void createIssue() throws Exception {
        int databaseSizeBeforeCreate = issueRepository.findAll().size();
        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);
        restIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(issueDTO)))
            .andExpect(status().isCreated());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate + 1);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIssue.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createIssueWithExistingId() throws Exception {
        // Create the Issue with an existing ID
        issue.setId(1L);
        IssueDTO issueDTO = issueMapper.toDto(issue);

        int databaseSizeBeforeCreate = issueRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(issueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().size();
        // set the field null
        issue.setName(null);

        // Create the Issue, which fails.
        IssueDTO issueDTO = issueMapper.toDto(issue);

        restIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(issueDTO)))
            .andExpect(status().isBadRequest());

        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIssues() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList
        restIssueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(issue.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getIssue() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get the issue
        restIssueMockMvc
            .perform(get(ENTITY_API_URL_ID, issue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(issue.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingIssue() throws Exception {
        // Get the issue
        restIssueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIssue() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        int databaseSizeBeforeUpdate = issueRepository.findAll().size();

        // Update the issue
        Issue updatedIssue = issueRepository.findById(issue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIssue are not directly saved in db
        em.detach(updatedIssue);
        updatedIssue.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        IssueDTO issueDTO = issueMapper.toDto(updatedIssue);

        restIssueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, issueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(issueDTO))
            )
            .andExpect(status().isOk());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIssueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, issueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(issueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIssueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(issueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIssueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(issueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIssueWithPatch() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        int databaseSizeBeforeUpdate = issueRepository.findAll().size();

        // Update the issue using partial update
        Issue partialUpdatedIssue = new Issue();
        partialUpdatedIssue.setId(issue.getId());

        restIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIssue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIssue))
            )
            .andExpect(status().isOk());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIssue.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateIssueWithPatch() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        int databaseSizeBeforeUpdate = issueRepository.findAll().size();

        // Update the issue using partial update
        Issue partialUpdatedIssue = new Issue();
        partialUpdatedIssue.setId(issue.getId());

        partialUpdatedIssue.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIssue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIssue))
            )
            .andExpect(status().isOk());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, issueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(issueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(issueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();
        issue.setId(count.incrementAndGet());

        // Create the Issue
        IssueDTO issueDTO = issueMapper.toDto(issue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIssueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(issueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIssue() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        int databaseSizeBeforeDelete = issueRepository.findAll().size();

        // Delete the issue
        restIssueMockMvc
            .perform(delete(ENTITY_API_URL_ID, issue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
