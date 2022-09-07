package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.BookGenre;
import com.mycompany.myapp.repository.BookGenreRepository;
import com.mycompany.myapp.service.criteria.BookGenreCriteria;
import com.mycompany.myapp.service.dto.BookGenreDTO;
import com.mycompany.myapp.service.mapper.BookGenreMapper;
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
 * Service for executing complex queries for {@link BookGenre} entities in the database.
 * The main input is a {@link BookGenreCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BookGenreDTO} or a {@link Page} of {@link BookGenreDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookGenreQueryService extends QueryService<BookGenre> {

    private final Logger log = LoggerFactory.getLogger(BookGenreQueryService.class);

    private final BookGenreRepository bookGenreRepository;

    private final BookGenreMapper bookGenreMapper;

    public BookGenreQueryService(BookGenreRepository bookGenreRepository, BookGenreMapper bookGenreMapper) {
        this.bookGenreRepository = bookGenreRepository;
        this.bookGenreMapper = bookGenreMapper;
    }

    /**
     * Return a {@link List} of {@link BookGenreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BookGenreDTO> findByCriteria(BookGenreCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BookGenre> specification = createSpecification(criteria);
        return bookGenreMapper.toDto(bookGenreRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BookGenreDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookGenreDTO> findByCriteria(BookGenreCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookGenre> specification = createSpecification(criteria);
        return bookGenreRepository.findAll(specification, page).map(bookGenreMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookGenreCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BookGenre> specification = createSpecification(criteria);
        return bookGenreRepository.count(specification);
    }

    /**
     * Function to convert {@link BookGenreCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookGenre> createSpecification(BookGenreCriteria criteria) {
        Specification<BookGenre> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BookGenre_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), BookGenre_.name));
            }
            if (criteria.getBookId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getBookId(), root -> root.join(BookGenre_.books, JoinType.LEFT).get(Book_.id))
                    );
            }
        }
        return specification;
    }
}
