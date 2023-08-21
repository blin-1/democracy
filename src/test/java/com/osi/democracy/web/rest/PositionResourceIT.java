package com.osi.democracy.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.osi.democracy.IntegrationTest;
import com.osi.democracy.domain.Position;
import com.osi.democracy.repository.PositionRepository;
import com.osi.democracy.service.dto.PositionDTO;
import com.osi.democracy.service.mapper.PositionMapper;
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
 * Integration tests for the {@link PositionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PositionResourceIT {

    private static final String DEFAULT_STATEMENT = "AAAAAAAAAA";
    private static final String UPDATED_STATEMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/positions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPositionMockMvc;

    private Position position;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createEntity(EntityManager em) {
        Position position = new Position().statement(DEFAULT_STATEMENT);
        return position;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createUpdatedEntity(EntityManager em) {
        Position position = new Position().statement(UPDATED_STATEMENT);
        return position;
    }

    @BeforeEach
    public void initTest() {
        position = createEntity(em);
    }

    @Test
    @Transactional
    void createPosition() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();
        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);
        restPositionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isCreated());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate + 1);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getStatement()).isEqualTo(DEFAULT_STATEMENT);
    }

    @Test
    @Transactional
    void createPositionWithExistingId() throws Exception {
        // Create the Position with an existing ID
        position.setId(1L);
        PositionDTO positionDTO = positionMapper.toDto(position);

        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPositionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPositions() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList
        restPositionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].statement").value(hasItem(DEFAULT_STATEMENT.toString())));
    }

    @Test
    @Transactional
    void getPosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get the position
        restPositionMockMvc
            .perform(get(ENTITY_API_URL_ID, position.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(position.getId().intValue()))
            .andExpect(jsonPath("$.statement").value(DEFAULT_STATEMENT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPosition() throws Exception {
        // Get the position
        restPositionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Update the position
        Position updatedPosition = positionRepository.findById(position.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPosition are not directly saved in db
        em.detach(updatedPosition);
        updatedPosition.statement(UPDATED_STATEMENT);
        PositionDTO positionDTO = positionMapper.toDto(updatedPosition);

        restPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, positionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(positionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getStatement()).isEqualTo(UPDATED_STATEMENT);
    }

    @Test
    @Transactional
    void putNonExistingPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();
        position.setId(count.incrementAndGet());

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, positionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(positionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();
        position.setId(count.incrementAndGet());

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(positionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();
        position.setId(count.incrementAndGet());

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePositionWithPatch() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Update the position using partial update
        Position partialUpdatedPosition = new Position();
        partialUpdatedPosition.setId(position.getId());

        restPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosition.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPosition))
            )
            .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getStatement()).isEqualTo(DEFAULT_STATEMENT);
    }

    @Test
    @Transactional
    void fullUpdatePositionWithPatch() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Update the position using partial update
        Position partialUpdatedPosition = new Position();
        partialUpdatedPosition.setId(position.getId());

        partialUpdatedPosition.statement(UPDATED_STATEMENT);

        restPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPosition.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPosition))
            )
            .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getStatement()).isEqualTo(UPDATED_STATEMENT);
    }

    @Test
    @Transactional
    void patchNonExistingPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();
        position.setId(count.incrementAndGet());

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, positionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(positionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();
        position.setId(count.incrementAndGet());

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(positionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();
        position.setId(count.incrementAndGet());

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPositionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(positionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        int databaseSizeBeforeDelete = positionRepository.findAll().size();

        // Delete the position
        restPositionMockMvc
            .perform(delete(ENTITY_API_URL_ID, position.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
