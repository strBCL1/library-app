package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BookGenre;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookGenre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookGenreRepository extends JpaRepository<BookGenre, Long>, JpaSpecificationExecutor<BookGenre> {}
