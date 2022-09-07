package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.repository.PublisherRepository;
import com.mycompany.myapp.service.criteria.PublisherCriteria;
import com.mycompany.myapp.service.dto.PublisherDTO;
import com.mycompany.myapp.service.mapper.PublisherMapper;
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
 * Integration tests for the {@link PublisherResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PublisherResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/publishers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private PublisherMapper publisherMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPublisherMockMvc;

    private Publisher publisher;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publisher createEntity(EntityManager em) {
        Publisher publisher = new Publisher()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .address(DEFAULT_ADDRESS)
            .phoneNumber(DEFAULT_PHONE_NUMBER);
        return publisher;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publisher createUpdatedEntity(EntityManager em) {
        Publisher publisher = new Publisher()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .address(UPDATED_ADDRESS)
            .phoneNumber(UPDATED_PHONE_NUMBER);
        return publisher;
    }

    @BeforeEach
    public void initTest() {
        publisher = createEntity(em);
    }

    @Test
    @Transactional
    void createPublisher() throws Exception {
        int databaseSizeBeforeCreate = publisherRepository.findAll().size();
        // Create the Publisher
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);
        restPublisherMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeCreate + 1);
        Publisher testPublisher = publisherList.get(publisherList.size() - 1);
        assertThat(testPublisher.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testPublisher.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testPublisher.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testPublisher.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void createPublisherWithExistingId() throws Exception {
        // Create the Publisher with an existing ID
        publisher.setId(1L);
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);

        int databaseSizeBeforeCreate = publisherRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublisherMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPublishers() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publisher.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @Test
    @Transactional
    void getPublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get the publisher
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL_ID, publisher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publisher.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    void getPublishersByIdFiltering() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        Long id = publisher.getId();

        defaultPublisherShouldBeFound("id.equals=" + id);
        defaultPublisherShouldNotBeFound("id.notEquals=" + id);

        defaultPublisherShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPublisherShouldNotBeFound("id.greaterThan=" + id);

        defaultPublisherShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPublisherShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPublishersByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where firstName equals to DEFAULT_FIRST_NAME
        defaultPublisherShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the publisherList where firstName equals to UPDATED_FIRST_NAME
        defaultPublisherShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultPublisherShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the publisherList where firstName equals to UPDATED_FIRST_NAME
        defaultPublisherShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where firstName is not null
        defaultPublisherShouldBeFound("firstName.specified=true");

        // Get all the publisherList where firstName is null
        defaultPublisherShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where firstName contains DEFAULT_FIRST_NAME
        defaultPublisherShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the publisherList where firstName contains UPDATED_FIRST_NAME
        defaultPublisherShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where firstName does not contain DEFAULT_FIRST_NAME
        defaultPublisherShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the publisherList where firstName does not contain UPDATED_FIRST_NAME
        defaultPublisherShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where lastName equals to DEFAULT_LAST_NAME
        defaultPublisherShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the publisherList where lastName equals to UPDATED_LAST_NAME
        defaultPublisherShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultPublisherShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the publisherList where lastName equals to UPDATED_LAST_NAME
        defaultPublisherShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where lastName is not null
        defaultPublisherShouldBeFound("lastName.specified=true");

        // Get all the publisherList where lastName is null
        defaultPublisherShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByLastNameContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where lastName contains DEFAULT_LAST_NAME
        defaultPublisherShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the publisherList where lastName contains UPDATED_LAST_NAME
        defaultPublisherShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where lastName does not contain DEFAULT_LAST_NAME
        defaultPublisherShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the publisherList where lastName does not contain UPDATED_LAST_NAME
        defaultPublisherShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where address equals to DEFAULT_ADDRESS
        defaultPublisherShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the publisherList where address equals to UPDATED_ADDRESS
        defaultPublisherShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPublishersByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultPublisherShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the publisherList where address equals to UPDATED_ADDRESS
        defaultPublisherShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPublishersByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where address is not null
        defaultPublisherShouldBeFound("address.specified=true");

        // Get all the publisherList where address is null
        defaultPublisherShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByAddressContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where address contains DEFAULT_ADDRESS
        defaultPublisherShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the publisherList where address contains UPDATED_ADDRESS
        defaultPublisherShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPublishersByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where address does not contain DEFAULT_ADDRESS
        defaultPublisherShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the publisherList where address does not contain UPDATED_ADDRESS
        defaultPublisherShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllPublishersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultPublisherShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the publisherList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultPublisherShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPublishersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultPublisherShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the publisherList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultPublisherShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPublishersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where phoneNumber is not null
        defaultPublisherShouldBeFound("phoneNumber.specified=true");

        // Get all the publisherList where phoneNumber is null
        defaultPublisherShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultPublisherShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the publisherList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultPublisherShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPublishersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        // Get all the publisherList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultPublisherShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the publisherList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultPublisherShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPublishersByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            publisherRepository.saveAndFlush(publisher);
            book = BookResourceIT.createEntity(em);
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        publisher.addBook(book);
        publisherRepository.saveAndFlush(publisher);
        Long bookId = book.getId();

        // Get all the publisherList where book equals to bookId
        defaultPublisherShouldBeFound("bookId.equals=" + bookId);

        // Get all the publisherList where book equals to (bookId + 1)
        defaultPublisherShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPublisherShouldBeFound(String filter) throws Exception {
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publisher.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));

        // Check, that the count call also returns 1
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPublisherShouldNotBeFound(String filter) throws Exception {
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPublisher() throws Exception {
        // Get the publisher
        restPublisherMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();

        // Update the publisher
        Publisher updatedPublisher = publisherRepository.findById(publisher.getId()).get();
        // Disconnect from session so that the updates on updatedPublisher are not directly saved in db
        em.detach(updatedPublisher);
        updatedPublisher
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .address(UPDATED_ADDRESS)
            .phoneNumber(UPDATED_PHONE_NUMBER);
        PublisherDTO publisherDTO = publisherMapper.toDto(updatedPublisher);

        restPublisherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publisherDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
        Publisher testPublisher = publisherList.get(publisherList.size() - 1);
        assertThat(testPublisher.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPublisher.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testPublisher.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testPublisher.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void putNonExistingPublisher() throws Exception {
        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();
        publisher.setId(count.incrementAndGet());

        // Create the Publisher
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publisherDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPublisher() throws Exception {
        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();
        publisher.setId(count.incrementAndGet());

        // Create the Publisher
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPublisher() throws Exception {
        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();
        publisher.setId(count.incrementAndGet());

        // Create the Publisher
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePublisherWithPatch() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();

        // Update the publisher using partial update
        Publisher partialUpdatedPublisher = new Publisher();
        partialUpdatedPublisher.setId(publisher.getId());

        partialUpdatedPublisher.address(UPDATED_ADDRESS).phoneNumber(UPDATED_PHONE_NUMBER);

        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublisher.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPublisher))
            )
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
        Publisher testPublisher = publisherList.get(publisherList.size() - 1);
        assertThat(testPublisher.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testPublisher.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testPublisher.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testPublisher.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void fullUpdatePublisherWithPatch() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();

        // Update the publisher using partial update
        Publisher partialUpdatedPublisher = new Publisher();
        partialUpdatedPublisher.setId(publisher.getId());

        partialUpdatedPublisher
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .address(UPDATED_ADDRESS)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublisher.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPublisher))
            )
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
        Publisher testPublisher = publisherList.get(publisherList.size() - 1);
        assertThat(testPublisher.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPublisher.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testPublisher.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testPublisher.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void patchNonExistingPublisher() throws Exception {
        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();
        publisher.setId(count.incrementAndGet());

        // Create the Publisher
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, publisherDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPublisher() throws Exception {
        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();
        publisher.setId(count.incrementAndGet());

        // Create the Publisher
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPublisher() throws Exception {
        int databaseSizeBeforeUpdate = publisherRepository.findAll().size();
        publisher.setId(count.incrementAndGet());

        // Create the Publisher
        PublisherDTO publisherDTO = publisherMapper.toDto(publisher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(publisherDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Publisher in the database
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePublisher() throws Exception {
        // Initialize the database
        publisherRepository.saveAndFlush(publisher);

        int databaseSizeBeforeDelete = publisherRepository.findAll().size();

        // Delete the publisher
        restPublisherMockMvc
            .perform(delete(ENTITY_API_URL_ID, publisher.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Publisher> publisherList = publisherRepository.findAll();
        assertThat(publisherList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
