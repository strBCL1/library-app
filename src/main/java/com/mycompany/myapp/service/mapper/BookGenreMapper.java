package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BookGenre;
import com.mycompany.myapp.service.dto.BookGenreDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookGenre} and its DTO {@link BookGenreDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookGenreMapper extends EntityMapper<BookGenreDTO, BookGenre> {}
