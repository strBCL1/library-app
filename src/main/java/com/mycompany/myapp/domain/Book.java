package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 200)
    @Column(name = "title", length = 200)
    private String title;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "release_date")
    private ZonedDateTime releaseDate;

    @ManyToMany
    @JoinTable(
        name = "rel_book__book_genre",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "book_genre_id")
    )
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<BookGenre> bookGenres = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Publisher publisher;

    @ManyToMany(mappedBy = "books")
    @JsonIgnoreProperties(value = { "user", "books" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Book description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getReleaseDate() {
        return this.releaseDate;
    }

    public Book releaseDate(ZonedDateTime releaseDate) {
        this.setReleaseDate(releaseDate);
        return this;
    }

    public void setReleaseDate(ZonedDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Set<BookGenre> getBookGenres() {
        return this.bookGenres;
    }

    public void setBookGenres(Set<BookGenre> bookGenres) {
        this.bookGenres = bookGenres;
    }

    public Book bookGenres(Set<BookGenre> bookGenres) {
        this.setBookGenres(bookGenres);
        return this;
    }

    public Book addBookGenre(BookGenre bookGenre) {
        this.bookGenres.add(bookGenre);
        bookGenre.getBooks().add(this);
        return this;
    }

    public Book removeBookGenre(BookGenre bookGenre) {
        this.bookGenres.remove(bookGenre);
        bookGenre.getBooks().remove(this);
        return this;
    }

    public Publisher getPublisher() {
        return this.publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Book publisher(Publisher publisher) {
        this.setPublisher(publisher);
        return this;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Author> authors) {
        if (this.authors != null) {
            this.authors.forEach(i -> i.removeBook(this));
        }
        if (authors != null) {
            authors.forEach(i -> i.addBook(this));
        }
        this.authors = authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
        return this;
    }

    public Book removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            "}";
    }
}
