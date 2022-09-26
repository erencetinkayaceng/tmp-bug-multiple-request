package com.cruz.web.rest;

import com.cruz.domain.Foo;
import com.cruz.repository.FooRepository;
import com.cruz.service.FooService;
import com.cruz.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.cruz.domain.Foo}.
 */
@RestController
@RequestMapping("/api")
public class FooResource {

    private final Logger log = LoggerFactory.getLogger(FooResource.class);

    private static final String ENTITY_NAME = "foo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FooService fooService;

    private final FooRepository fooRepository;

    public FooResource(FooService fooService, FooRepository fooRepository) {
        this.fooService = fooService;
        this.fooRepository = fooRepository;
    }

    /**
     * {@code POST  /foos} : Create a new foo.
     *
     * @param foo the foo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new foo, or with status {@code 400 (Bad Request)} if the foo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/foos")
    public ResponseEntity<Foo> createFoo(@Valid @RequestBody Foo foo) throws URISyntaxException {
        log.debug("REST request to save Foo : {}", foo);
        if (foo.getId() != null) {
            throw new BadRequestAlertException("A new foo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Foo result = fooService.save(foo);
        return ResponseEntity
            .created(new URI("/api/foos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /foos/:id} : Updates an existing foo.
     *
     * @param id the id of the foo to save.
     * @param foo the foo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated foo,
     * or with status {@code 400 (Bad Request)} if the foo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the foo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/foos/{id}")
    public ResponseEntity<Foo> updateFoo(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Foo foo)
        throws URISyntaxException {
        log.debug("REST request to update Foo : {}, {}", id, foo);
        if (foo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, foo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fooRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Foo result = fooService.update(foo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, foo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /foos/:id} : Partial updates given fields of an existing foo, field will ignore if it is null
     *
     * @param id the id of the foo to save.
     * @param foo the foo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated foo,
     * or with status {@code 400 (Bad Request)} if the foo is not valid,
     * or with status {@code 404 (Not Found)} if the foo is not found,
     * or with status {@code 500 (Internal Server Error)} if the foo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/foos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Foo> partialUpdateFoo(@PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody Foo foo)
        throws URISyntaxException {
        log.debug("REST request to partial update Foo partially : {}, {}", id, foo);
        if (foo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, foo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fooRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Foo> result = fooService.partialUpdate(foo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, foo.getId().toString())
        );
    }

    /**
     * {@code GET  /foos} : get all the foos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of foos in body.
     */
    @GetMapping("/foos")
    public ResponseEntity<List<Foo>> getAllFoos(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Foos");
        Page<Foo> page = fooService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /foos/:id} : get the "id" foo.
     *
     * @param id the id of the foo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the foo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/foos/{id}")
    public ResponseEntity<Foo> getFoo(@PathVariable Long id) {
        log.debug("REST request to get Foo : {}", id);
        Optional<Foo> foo = fooService.findOne(id);
        return ResponseUtil.wrapOrNotFound(foo);
    }

    /**
     * {@code DELETE  /foos/:id} : delete the "id" foo.
     *
     * @param id the id of the foo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/foos/{id}")
    public ResponseEntity<Void> deleteFoo(@PathVariable Long id) {
        log.debug("REST request to delete Foo : {}", id);
        fooService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
