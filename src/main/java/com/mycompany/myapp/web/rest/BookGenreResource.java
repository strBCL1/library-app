package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.BookGenreRepository;
import com.mycompany.myapp.service.BookGenreQueryService;
import com.mycompany.myapp.service.BookGenreService;
import com.mycompany.myapp.service.criteria.BookGenreCriteria;
import com.mycompany.myapp.service.dto.BookGenreDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.BookGenre}.
 */
@RestController
@RequestMapping("/api")
public class BookGenreResource {

    private final Logger log = LoggerFactory.getLogger(BookGenreResource.class);

    private static final String ENTITY_NAME = "bookGenre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookGenreService bookGenreService;

    private final BookGenreRepository bookGenreRepository;

    private final BookGenreQueryService bookGenreQueryService;

    public BookGenreResource(
        BookGenreService bookGenreService,
        BookGenreRepository bookGenreRepository,
        BookGenreQueryService bookGenreQueryService
    ) {
        this.bookGenreService = bookGenreService;
        this.bookGenreRepository = bookGenreRepository;
        this.bookGenreQueryService = bookGenreQueryService;
    }

    /**
     * {@code POST  /book-genres} : Create a new bookGenre.
     *
     * @param bookGenreDTO the bookGenreDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookGenreDTO, or with status {@code 400 (Bad Request)} if the bookGenre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/book-genres")
    public ResponseEntity<BookGenreDTO> createBookGenre(@Valid @RequestBody BookGenreDTO bookGenreDTO) throws URISyntaxException {
        log.debug("REST request to save BookGenre : {}", bookGenreDTO);
        if (bookGenreDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookGenre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookGenreDTO result = bookGenreService.save(bookGenreDTO);
        return ResponseEntity
            .created(new URI("/api/book-genres/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /book-genres/:id} : Updates an existing bookGenre.
     *
     * @param id the id of the bookGenreDTO to save.
     * @param bookGenreDTO the bookGenreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookGenreDTO,
     * or with status {@code 400 (Bad Request)} if the bookGenreDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookGenreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/book-genres/{id}")
    public ResponseEntity<BookGenreDTO> updateBookGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookGenreDTO bookGenreDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BookGenre : {}, {}", id, bookGenreDTO);
        if (bookGenreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookGenreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookGenreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BookGenreDTO result = bookGenreService.update(bookGenreDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookGenreDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /book-genres/:id} : Partial updates given fields of an existing bookGenre, field will ignore if it is null
     *
     * @param id the id of the bookGenreDTO to save.
     * @param bookGenreDTO the bookGenreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookGenreDTO,
     * or with status {@code 400 (Bad Request)} if the bookGenreDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookGenreDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookGenreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/book-genres/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookGenreDTO> partialUpdateBookGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookGenreDTO bookGenreDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BookGenre partially : {}, {}", id, bookGenreDTO);
        if (bookGenreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookGenreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookGenreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookGenreDTO> result = bookGenreService.partialUpdate(bookGenreDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bookGenreDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /book-genres} : get all the bookGenres.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookGenres in body.
     */
    @GetMapping("/book-genres")
    public ResponseEntity<List<BookGenreDTO>> getAllBookGenres(
        BookGenreCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get BookGenres by criteria: {}", criteria);
        Page<BookGenreDTO> page = bookGenreQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-genres/count} : count all the bookGenres.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/book-genres/count")
    public ResponseEntity<Long> countBookGenres(BookGenreCriteria criteria) {
        log.debug("REST request to count BookGenres by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookGenreQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /book-genres/:id} : get the "id" bookGenre.
     *
     * @param id the id of the bookGenreDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookGenreDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/book-genres/{id}")
    public ResponseEntity<BookGenreDTO> getBookGenre(@PathVariable Long id) {
        log.debug("REST request to get BookGenre : {}", id);
        Optional<BookGenreDTO> bookGenreDTO = bookGenreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookGenreDTO);
    }

    /**
     * {@code DELETE  /book-genres/:id} : delete the "id" bookGenre.
     *
     * @param id the id of the bookGenreDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/book-genres/{id}")
    public ResponseEntity<Void> deleteBookGenre(@PathVariable Long id) {
        log.debug("REST request to delete BookGenre : {}", id);
        bookGenreService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
