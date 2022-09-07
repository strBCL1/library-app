package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Book;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class BookRepositoryWithBagRelationshipsImpl implements BookRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Book> fetchBagRelationships(Optional<Book> book) {
        return book.map(this::fetchBookGenres);
    }

    @Override
    public Page<Book> fetchBagRelationships(Page<Book> books) {
        return new PageImpl<>(fetchBagRelationships(books.getContent()), books.getPageable(), books.getTotalElements());
    }

    @Override
    public List<Book> fetchBagRelationships(List<Book> books) {
        return Optional.of(books).map(this::fetchBookGenres).orElse(Collections.emptyList());
    }

    Book fetchBookGenres(Book result) {
        return entityManager
            .createQuery("select book from Book book left join fetch book.bookGenres where book is :book", Book.class)
            .setParameter("book", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Book> fetchBookGenres(List<Book> books) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, books.size()).forEach(index -> order.put(books.get(index).getId(), index));
        List<Book> result = entityManager
            .createQuery("select distinct book from Book book left join fetch book.bookGenres where book in :books", Book.class)
            .setParameter("books", books)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
