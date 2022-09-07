package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Author} and its DTO {@link AuthorDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuthorMapper extends EntityMapper<AuthorDTO, Author> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "books", source = "books", qualifiedByName = "bookIdSet")
    AuthorDTO toDto(Author s);

    @Mapping(target = "removeBook", ignore = true)
    Author toEntity(AuthorDTO authorDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("bookId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoBookId(Book book);

    @Named("bookIdSet")
    default Set<BookDTO> toDtoBookIdSet(Set<Book> book) {
        return book.stream().map(this::toDtoBookId).collect(Collectors.toSet());
    }
}
