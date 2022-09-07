package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookGenreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookGenre.class);
        BookGenre bookGenre1 = new BookGenre();
        bookGenre1.setId(1L);
        BookGenre bookGenre2 = new BookGenre();
        bookGenre2.setId(bookGenre1.getId());
        assertThat(bookGenre1).isEqualTo(bookGenre2);
        bookGenre2.setId(2L);
        assertThat(bookGenre1).isNotEqualTo(bookGenre2);
        bookGenre1.setId(null);
        assertThat(bookGenre1).isNotEqualTo(bookGenre2);
    }
}
