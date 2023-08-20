package com.osi.democracy.web.rest;

import com.osi.democracy.repository.OfficeRepository;
import com.osi.democracy.service.OfficeService;
import com.osi.democracy.service.dto.OfficeDTO;
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
 * REST controller for managing {@link com.osi.democracy.domain.Office}.
 */
@RestController
@RequestMapping("/api")
public class OfficeResource {

    private final Logger log = LoggerFactory.getLogger(OfficeResource.class);

    private static final String ENTITY_NAME = "office";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OfficeService officeService;

    private final OfficeRepository officeRepository;

    public OfficeResource(OfficeService officeService, OfficeRepository officeRepository) {
        this.officeService = officeService;
        this.officeRepository = officeRepository;
    }

    /**
     * {@code POST  /offices} : Create a new office.
     *
     * @param officeDTO the officeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new officeDTO, or with status {@code 400 (Bad Request)} if the office has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/offices")
    public ResponseEntity<OfficeDTO> createOffice(@Valid @RequestBody OfficeDTO officeDTO) throws URISyntaxException {
        log.debug("REST request to save Office : {}", officeDTO);
        if (officeDTO.getId() != null) {
            throw new BadRequestAlertException("A new office cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OfficeDTO result = officeService.save(officeDTO);
        return ResponseEntity
            .created(new URI("/api/offices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /offices/:id} : Updates an existing office.
     *
     * @param id the id of the officeDTO to save.
     * @param officeDTO the officeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated officeDTO,
     * or with status {@code 400 (Bad Request)} if the officeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the officeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/offices/{id}")
    public ResponseEntity<OfficeDTO> updateOffice(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OfficeDTO officeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Office : {}, {}", id, officeDTO);
        if (officeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, officeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!officeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OfficeDTO result = officeService.update(officeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, officeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /offices/:id} : Partial updates given fields of an existing office, field will ignore if it is null
     *
     * @param id the id of the officeDTO to save.
     * @param officeDTO the officeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated officeDTO,
     * or with status {@code 400 (Bad Request)} if the officeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the officeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the officeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/offices/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OfficeDTO> partialUpdateOffice(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OfficeDTO officeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Office partially : {}, {}", id, officeDTO);
        if (officeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, officeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!officeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OfficeDTO> result = officeService.partialUpdate(officeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, officeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /offices} : get all the offices.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offices in body.
     */
    @GetMapping("/offices")
    public ResponseEntity<List<OfficeDTO>> getAllOffices(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Offices");
        Page<OfficeDTO> page = officeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /offices/:id} : get the "id" office.
     *
     * @param id the id of the officeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the officeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/offices/{id}")
    public ResponseEntity<OfficeDTO> getOffice(@PathVariable Long id) {
        log.debug("REST request to get Office : {}", id);
        Optional<OfficeDTO> officeDTO = officeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(officeDTO);
    }

    /**
     * {@code DELETE  /offices/:id} : delete the "id" office.
     *
     * @param id the id of the officeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/offices/{id}")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        log.debug("REST request to delete Office : {}", id);
        officeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
