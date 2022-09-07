package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.BookGenre} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookGenreDTO implements Serializable {

    private Long id;

    @Size(max = 50)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookGenreDTO)) {
            return false;
        }

        BookGenreDTO bookGenreDTO = (BookGenreDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookGenreDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookGenreDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
