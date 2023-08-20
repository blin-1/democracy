package com.osi.democracy.web.rest;

import com.osi.democracy.repository.CandidateRepository;
import com.osi.democracy.service.CandidateService;
import com.osi.democracy.service.dto.CandidateDTO;
import com.osi.democracy.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.osi.democracy.domain.Candidate}.
 */
@RestController
@RequestMapping("/api")
public class CandidateResource {

    private final Logger log = LoggerFactory.getLogger(CandidateResource.class);

    private static final String ENTITY_NAME = "candidate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidateService candidateService;

    private final CandidateRepository candidateRepository;

    public CandidateResource(CandidateService candidateService, CandidateRepository candidateRepository) {
        this.candidateService = candidateService;
        this.candidateRepository = candidateRepository;
    }

    /**
     * {@code POST  /candidates} : Create a new candidate.
     *
     * @param candidateDTO the candidateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidateDTO, or with status {@code 400 (Bad Request)} if the candidate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/candidates")
    public ResponseEntity<CandidateDTO> createCandidate(@Valid @RequestBody CandidateDTO candidateDTO) throws URISyntaxException {
        log.debug("REST request to save Candidate : {}", candidateDTO);
        if (candidateDTO.getId() != null) {
            throw new BadRequestAlertException("A new candidate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CandidateDTO result = candidateService.save(candidateDTO);
        return ResponseEntity
            .created(new URI("/api/candidates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /candidates/:id} : Updates an existing candidate.
     *
     * @param id the id of the candidateDTO to save.
     * @param candidateDTO the candidateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidateDTO,
     * or with status {@code 400 (Bad Request)} if the candidateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/candidates/{id}")
    public ResponseEntity<CandidateDTO> updateCandidate(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CandidateDTO candidateDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Candidate : {}, {}", id, candidateDTO);
        if (candidateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CandidateDTO result = candidateService.update(candidateDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, candidateDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /candidates/:id} : Partial updates given fields of an existing candidate, field will ignore if it is null
     *
     * @param id the id of the candidateDTO to save.
     * @param candidateDTO the candidateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidateDTO,
     * or with status {@code 400 (Bad Request)} if the candidateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the candidateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the candidateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/candidates/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CandidateDTO> partialUpdateCandidate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CandidateDTO candidateDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Candidate partially : {}, {}", id, candidateDTO);
        if (candidateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CandidateDTO> result = candidateService.partialUpdate(candidateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, candidateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /candidates} : get all the candidates.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidates in body.
     */
    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateDTO>> getAllCandidates(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Candidates");
        Page<CandidateDTO> page = candidateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /candidates/:id} : get the "id" candidate.
     *
     * @param id the id of the candidateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/candidates/{id}")
    public ResponseEntity<CandidateDTO> getCandidate(@PathVariable Long id) {
        log.debug("REST request to get Candidate : {}", id);
        Optional<CandidateDTO> candidateDTO = candidateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(candidateDTO);
    }

    /**
     * {@code DELETE  /candidates/:id} : delete the "id" candidate.
     *
     * @param id the id of the candidateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        log.debug("REST request to delete Candidate : {}", id);
        candidateService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
