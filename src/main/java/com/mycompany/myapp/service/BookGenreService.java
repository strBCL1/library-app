package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.BookGenre;
import com.mycompany.myapp.repository.BookGenreRepository;
import com.mycompany.myapp.service.dto.BookGenreDTO;
import com.mycompany.myapp.service.mapper.BookGenreMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BookGenre}.
 */
@Service
@Transactional
public class BookGenreService {

    private final Logger log = LoggerFactory.getLogger(BookGenreService.class);

    private final BookGenreRepository bookGenreRepository;

    private final BookGenreMapper bookGenreMapper;

    public BookGenreService(BookGenreRepository bookGenreRepository, BookGenreMapper bookGenreMapper) {
        this.bookGenreRepository = bookGenreRepository;
        this.bookGenreMapper = bookGenreMapper;
    }

    /**
     * Save a bookGenre.
     *
     * @param bookGenreDTO the entity to save.
     * @return the persisted entity.
     */
    public BookGenreDTO save(BookGenreDTO bookGenreDTO) {
        log.debug("Request to save BookGenre : {}", bookGenreDTO);
        BookGenre bookGenre = bookGenreMapper.toEntity(bookGenreDTO);
        bookGenre = bookGenreRepository.save(bookGenre);
        return bookGenreMapper.toDto(bookGenre);
    }

    /**
     * Update a bookGenre.
     *
     * @param bookGenreDTO the entity to save.
     * @return the persisted entity.
     */
    public BookGenreDTO update(BookGenreDTO bookGenreDTO) {
        log.debug("Request to update BookGenre : {}", bookGenreDTO);
        BookGenre bookGenre = bookGenreMapper.toEntity(bookGenreDTO);
        bookGenre = bookGenreRepository.save(bookGenre);
        return bookGenreMapper.toDto(bookGenre);
    }

    /**
     * Partially update a bookGenre.
     *
     * @param bookGenreDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookGenreDTO> partialUpdate(BookGenreDTO bookGenreDTO) {
        log.debug("Request to partially update BookGenre : {}", bookGenreDTO);

        return bookGenreRepository
            .findById(bookGenreDTO.getId())
            .map(existingBookGenre -> {
                bookGenreMapper.partialUpdate(existingBookGenre, bookGenreDTO);

                return existingBookGenre;
            })
            .map(bookGenreRepository::save)
            .map(bookGenreMapper::toDto);
    }

    /**
     * Get all the bookGenres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BookGenreDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BookGenres");
        return bookGenreRepository.findAll(pageable).map(bookGenreMapper::toDto);
    }

    /**
     * Get one bookGenre by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookGenreDTO> findOne(Long id) {
        log.debug("Request to get BookGenre : {}", id);
        return bookGenreRepository.findById(id).map(bookGenreMapper::toDto);
    }

    /**
     * Delete the bookGenre by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BookGenre : {}", id);
        bookGenreRepository.deleteById(id);
    }
}
