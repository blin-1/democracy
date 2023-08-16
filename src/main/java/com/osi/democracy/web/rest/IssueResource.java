package com.osi.democracy.web.rest;

import com.osi.democracy.domain.Issue;
import com.osi.democracy.repository.IssueRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.osi.democracy.domain.Issue}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class IssueResource {

    private final Logger log = LoggerFactory.getLogger(IssueResource.class);

    private static final String ENTITY_NAME = "issue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IssueRepository issueRepository;

    public IssueResource(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    /**
     * {@code POST  /issues} : Create a new issue.
     *
     * @param issue the issue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new issue, or with status {@code 400 (Bad Request)} if the issue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/issues")
    public ResponseEntity<Issue> createIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        log.debug("REST request to save Issue : {}", issue);
        if (issue.getId() != null) {
            throw new BadRequestAlertException("A new issue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Issue result = issueRepository.save(issue);
        return ResponseEntity
            .created(new URI("/api/issues/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /issues/:id} : Updates an existing issue.
     *
     * @param id the id of the issue to save.
     * @param issue the issue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated issue,
     * or with status {@code 400 (Bad Request)} if the issue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the issue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/issues/{id}")
    public ResponseEntity<Issue> updateIssue(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Issue issue)
        throws URISyntaxException {
        log.debug("REST request to update Issue : {}, {}", id, issue);
        if (issue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, issue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!issueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Issue result = issueRepository.save(issue);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, issue.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /issues/:id} : Partial updates given fields of an existing issue, field will ignore if it is null
     *
     * @param id the id of the issue to save.
     * @param issue the issue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated issue,
     * or with status {@code 400 (Bad Request)} if the issue is not valid,
     * or with status {@code 404 (Not Found)} if the issue is not found,
     * or with status {@code 500 (Internal Server Error)} if the issue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/issues/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Issue> partialUpdateIssue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Issue issue
    ) throws URISyntaxException {
        log.debug("REST request to partial update Issue partially : {}, {}", id, issue);
        if (issue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, issue.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!issueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Issue> result = issueRepository
            .findById(issue.getId())
            .map(existingIssue -> {
                if (issue.getName() != null) {
                    existingIssue.setName(issue.getName());
                }
                if (issue.getDescription() != null) {
                    existingIssue.setDescription(issue.getDescription());
                }

                return existingIssue;
            })
            .map(issueRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, issue.getId().toString())
        );
    }

    /**
     * {@code GET  /issues} : get all the issues.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of issues in body.
     */
    @GetMapping("/issues")
    public List<Issue> getAllIssues() {
        log.debug("REST request to get all Issues");
        return issueRepository.findAll();
    }

    /**
     * {@code GET  /issues/:id} : get the "id" issue.
     *
     * @param id the id of the issue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the issue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/issues/{id}")
    public ResponseEntity<Issue> getIssue(@PathVariable Long id) {
        log.debug("REST request to get Issue : {}", id);
        Optional<Issue> issue = issueRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(issue);
    }

    /**
     * {@code DELETE  /issues/:id} : delete the "id" issue.
     *
     * @param id the id of the issue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/issues/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        log.debug("REST request to delete Issue : {}", id);
        issueRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
