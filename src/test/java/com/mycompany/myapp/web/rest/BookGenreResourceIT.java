package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.BookGenre;
import com.mycompany.myapp.repository.BookGenreRepository;
import com.mycompany.myapp.service.criteria.BookGenreCriteria;
import com.mycompany.myapp.service.dto.BookGenreDTO;
import com.mycompany.myapp.service.mapper.BookGenreMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookGenreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookGenreResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/book-genres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookGenreRepository bookGenreRepository;

    @Autowired
    private BookGenreMapper bookGenreMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookGenreMockMvc;

    private BookGenre bookGenre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookGenre createEntity(EntityManager em) {
        BookGenre bookGenre = new BookGenre().name(DEFAULT_NAME);
        return bookGenre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookGenre createUpdatedEntity(EntityManager em) {
        BookGenre bookGenre = new BookGenre().name(UPDATED_NAME);
        return bookGenre;
    }

    @BeforeEach
    public void initTest() {
        bookGenre = createEntity(em);
    }

    @Test
    @Transactional
    void createBookGenre() throws Exception {
        int databaseSizeBeforeCreate = bookGenreRepository.findAll().size();
        // Create the BookGenre
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);
        restBookGenreMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeCreate + 1);
        BookGenre testBookGenre = bookGenreList.get(bookGenreList.size() - 1);
        assertThat(testBookGenre.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createBookGenreWithExistingId() throws Exception {
        // Create the BookGenre with an existing ID
        bookGenre.setId(1L);
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);

        int databaseSizeBeforeCreate = bookGenreRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookGenreMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookGenres() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        // Get all the bookGenreList
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookGenre.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getBookGenre() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        // Get the bookGenre
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL_ID, bookGenre.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookGenre.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getBookGenresByIdFiltering() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        Long id = bookGenre.getId();

        defaultBookGenreShouldBeFound("id.equals=" + id);
        defaultBookGenreShouldNotBeFound("id.notEquals=" + id);

        defaultBookGenreShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBookGenreShouldNotBeFound("id.greaterThan=" + id);

        defaultBookGenreShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBookGenreShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookGenresByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        // Get all the bookGenreList where name equals to DEFAULT_NAME
        defaultBookGenreShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the bookGenreList where name equals to UPDATED_NAME
        defaultBookGenreShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBookGenresByNameIsInShouldWork() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        // Get all the bookGenreList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBookGenreShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the bookGenreList where name equals to UPDATED_NAME
        defaultBookGenreShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBookGenresByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        // Get all the bookGenreList where name is not null
        defaultBookGenreShouldBeFound("name.specified=true");

        // Get all the bookGenreList where name is null
        defaultBookGenreShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllBookGenresByNameContainsSomething() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        // Get all the bookGenreList where name contains DEFAULT_NAME
        defaultBookGenreShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the bookGenreList where name contains UPDATED_NAME
        defaultBookGenreShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBookGenresByNameNotContainsSomething() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        // Get all the bookGenreList where name does not contain DEFAULT_NAME
        defaultBookGenreShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the bookGenreList where name does not contain UPDATED_NAME
        defaultBookGenreShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBookGenresByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            bookGenreRepository.saveAndFlush(bookGenre);
            book = BookResourceIT.createEntity(em);
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        bookGenre.addBook(book);
        bookGenreRepository.saveAndFlush(bookGenre);
        Long bookId = book.getId();

        // Get all the bookGenreList where book equals to bookId
        defaultBookGenreShouldBeFound("bookId.equals=" + bookId);

        // Get all the bookGenreList where book equals to (bookId + 1)
        defaultBookGenreShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookGenreShouldBeFound(String filter) throws Exception {
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookGenre.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookGenreShouldNotBeFound(String filter) throws Exception {
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookGenre() throws Exception {
        // Get the bookGenre
        restBookGenreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookGenre() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();

        // Update the bookGenre
        BookGenre updatedBookGenre = bookGenreRepository.findById(bookGenre.getId()).get();
        // Disconnect from session so that the updates on updatedBookGenre are not directly saved in db
        em.detach(updatedBookGenre);
        updatedBookGenre.name(UPDATED_NAME);
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(updatedBookGenre);

        restBookGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookGenreDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
        BookGenre testBookGenre = bookGenreList.get(bookGenreList.size() - 1);
        assertThat(testBookGenre.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingBookGenre() throws Exception {
        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();
        bookGenre.setId(count.incrementAndGet());

        // Create the BookGenre
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookGenreDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookGenre() throws Exception {
        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();
        bookGenre.setId(count.incrementAndGet());

        // Create the BookGenre
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookGenre() throws Exception {
        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();
        bookGenre.setId(count.incrementAndGet());

        // Create the BookGenre
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookGenreWithPatch() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();

        // Update the bookGenre using partial update
        BookGenre partialUpdatedBookGenre = new BookGenre();
        partialUpdatedBookGenre.setId(bookGenre.getId());

        partialUpdatedBookGenre.name(UPDATED_NAME);

        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookGenre.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookGenre))
            )
            .andExpect(status().isOk());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
        BookGenre testBookGenre = bookGenreList.get(bookGenreList.size() - 1);
        assertThat(testBookGenre.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateBookGenreWithPatch() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();

        // Update the bookGenre using partial update
        BookGenre partialUpdatedBookGenre = new BookGenre();
        partialUpdatedBookGenre.setId(bookGenre.getId());

        partialUpdatedBookGenre.name(UPDATED_NAME);

        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookGenre.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookGenre))
            )
            .andExpect(status().isOk());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
        BookGenre testBookGenre = bookGenreList.get(bookGenreList.size() - 1);
        assertThat(testBookGenre.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingBookGenre() throws Exception {
        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();
        bookGenre.setId(count.incrementAndGet());

        // Create the BookGenre
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookGenreDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookGenre() throws Exception {
        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();
        bookGenre.setId(count.incrementAndGet());

        // Create the BookGenre
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookGenre() throws Exception {
        int databaseSizeBeforeUpdate = bookGenreRepository.findAll().size();
        bookGenre.setId(count.incrementAndGet());

        // Create the BookGenre
        BookGenreDTO bookGenreDTO = bookGenreMapper.toDto(bookGenre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookGenreDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookGenre in the database
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookGenre() throws Exception {
        // Initialize the database
        bookGenreRepository.saveAndFlush(bookGenre);

        int databaseSizeBeforeDelete = bookGenreRepository.findAll().size();

        // Delete the bookGenre
        restBookGenreMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookGenre.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BookGenre> bookGenreList = bookGenreRepository.findAll();
        assertThat(bookGenreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
