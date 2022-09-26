package com.cruz.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cruz.IntegrationTest;
import com.cruz.domain.Foo;
import com.cruz.repository.FooRepository;
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
 * Integration tests for the {@link FooResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FooResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/foos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FooRepository fooRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFooMockMvc;

    private Foo foo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Foo createEntity(EntityManager em) {
        Foo foo = new Foo().name(DEFAULT_NAME);
        return foo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Foo createUpdatedEntity(EntityManager em) {
        Foo foo = new Foo().name(UPDATED_NAME);
        return foo;
    }

    @BeforeEach
    public void initTest() {
        foo = createEntity(em);
    }

    @Test
    @Transactional
    void createFoo() throws Exception {
        int databaseSizeBeforeCreate = fooRepository.findAll().size();
        // Create the Foo
        restFooMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isCreated());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeCreate + 1);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createFooWithExistingId() throws Exception {
        // Create the Foo with an existing ID
        foo.setId(1L);

        int databaseSizeBeforeCreate = fooRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFooMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fooRepository.findAll().size();
        // set the field null
        foo.setName(null);

        // Create the Foo, which fails.

        restFooMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isBadRequest());

        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFoos() throws Exception {
        // Initialize the database
        fooRepository.saveAndFlush(foo);

        // Get all the fooList
        restFooMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(foo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getFoo() throws Exception {
        // Initialize the database
        fooRepository.saveAndFlush(foo);

        // Get the foo
        restFooMockMvc
            .perform(get(ENTITY_API_URL_ID, foo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(foo.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingFoo() throws Exception {
        // Get the foo
        restFooMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFoo() throws Exception {
        // Initialize the database
        fooRepository.saveAndFlush(foo);

        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Update the foo
        Foo updatedFoo = fooRepository.findById(foo.getId()).get();
        // Disconnect from session so that the updates on updatedFoo are not directly saved in db
        em.detach(updatedFoo);
        updatedFoo.name(UPDATED_NAME);

        restFooMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFoo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                put(ENTITY_API_URL_ID, foo.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFooWithPatch() throws Exception {
        // Initialize the database
        fooRepository.saveAndFlush(foo);

        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Update the foo using partial update
        Foo partialUpdatedFoo = new Foo();
        partialUpdatedFoo.setId(foo.getId());

        partialUpdatedFoo.name(UPDATED_NAME);

        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFoo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateFooWithPatch() throws Exception {
        // Initialize the database
        fooRepository.saveAndFlush(foo);

        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Update the foo using partial update
        Foo partialUpdatedFoo = new Foo();
        partialUpdatedFoo.setId(foo.getId());

        partialUpdatedFoo.name(UPDATED_NAME);

        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFoo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, foo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFoo() throws Exception {
        // Initialize the database
        fooRepository.saveAndFlush(foo);

        int databaseSizeBeforeDelete = fooRepository.findAll().size();

        // Delete the foo
        restFooMockMvc.perform(delete(ENTITY_API_URL_ID, foo.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
