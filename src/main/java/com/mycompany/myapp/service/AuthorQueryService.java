package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.repository.AuthorRepository;
import com.mycompany.myapp.service.criteria.AuthorCriteria;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.mapper.AuthorMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Author} entities in the database.
 * The main input is a {@link AuthorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AuthorDTO} or a {@link Page} of {@link AuthorDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuthorQueryService extends QueryService<Author> {

    private final Logger log = LoggerFactory.getLogger(AuthorQueryService.class);

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    public AuthorQueryService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    /**
     * Return a {@link List} of {@link AuthorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AuthorDTO> findByCriteria(AuthorCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Author> specification = createSpecification(criteria);
        return authorMapper.toDto(authorRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AuthorDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuthorDTO> findByCriteria(AuthorCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Author> specification = createSpecification(criteria);
        return authorRepository.findAll(specification, page).map(authorMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuthorCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Author> specification = createSpecification(criteria);
        return authorRepository.count(specification);
    }

    /**
     * Function to convert {@link AuthorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Author> createSpecification(AuthorCriteria criteria) {
        Specification<Author> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Author_.id));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Author_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getBookId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getBookId(), root -> root.join(Author_.books, JoinType.LEFT).get(Book_.id))
                    );
            }
        }
        return specification;
    }
}
