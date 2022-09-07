package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A BookGenre.
 */
@Entity
@Table(name = "book_genre")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookGenre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @ManyToMany(mappedBy = "bookGenres")
    @JsonIgnoreProperties(value = { "bookGenres", "publisher", "authors" }, allowSetters = true)
    private Set<Book> books = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookGenre id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public BookGenre name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return this.books;
    }

    public void setBooks(Set<Book> books) {
        if (this.books != null) {
            this.books.forEach(i -> i.removeBookGenre(this));
        }
        if (books != null) {
            books.forEach(i -> i.addBookGenre(this));
        }
        this.books = books;
    }

    public BookGenre books(Set<Book> books) {
        this.setBooks(books);
        return this;
    }

    public BookGenre addBook(Book book) {
        this.books.add(book);
        book.getBookGenres().add(this);
        return this;
    }

    public BookGenre removeBook(Book book) {
        this.books.remove(book);
        book.getBookGenres().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookGenre)) {
            return false;
        }
        return id != null && id.equals(((BookGenre) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookGenre{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
