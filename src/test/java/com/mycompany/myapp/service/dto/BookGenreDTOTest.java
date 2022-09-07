package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookGenreDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookGenreDTO.class);
        BookGenreDTO bookGenreDTO1 = new BookGenreDTO();
        bookGenreDTO1.setId(1L);
        BookGenreDTO bookGenreDTO2 = new BookGenreDTO();
        assertThat(bookGenreDTO1).isNotEqualTo(bookGenreDTO2);
        bookGenreDTO2.setId(bookGenreDTO1.getId());
        assertThat(bookGenreDTO1).isEqualTo(bookGenreDTO2);
        bookGenreDTO2.setId(2L);
        assertThat(bookGenreDTO1).isNotEqualTo(bookGenreDTO2);
        bookGenreDTO1.setId(null);
        assertThat(bookGenreDTO1).isNotEqualTo(bookGenreDTO2);
    }
}
