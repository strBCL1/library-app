package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.BookGenre;
import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.BookGenreDTO;
import com.mycompany.myapp.service.dto.PublisherDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "bookGenres", source = "bookGenres", qualifiedByName = "bookGenreIdSet")
    @Mapping(target = "publisher", source = "publisher", qualifiedByName = "publisherId")
    BookDTO toDto(Book s);

    @Mapping(target = "removeBookGenre", ignore = true)
    Book toEntity(BookDTO bookDTO);

    @Named("bookGenreId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookGenreDTO toDtoBookGenreId(BookGenre bookGenre);

    @Named("bookGenreIdSet")
    default Set<BookGenreDTO> toDtoBookGenreIdSet(Set<BookGenre> bookGenre) {
        return bookGenre.stream().map(this::toDtoBookGenreId).collect(Collectors.toSet());
    }

    @Named("publisherId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PublisherDTO toDtoPublisherId(Publisher publisher);
}
