package sample;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DateSorterTest {
    private final DateSorter dateSorter = new DateSorter();

    /*
     * (2004-07-01, 2005-01-02, 2007-01-01, 2032-05-03)
     * would sort to
     * (2005-01-02, 2007-01-01, 2032-05-03, 2004-07-01)
     */
    @Test
    void exampleDataTest() {
        List<LocalDate> unsortedDates = List.of(
                LocalDate.of(2004, 7, 1),
                LocalDate.of(2005, 1, 2),
                LocalDate.of(2007, 1, 1),
                LocalDate.of(2032, 5, 3)
        );

        Collection<LocalDate> actualSortedDates = dateSorter.sortDates(unsortedDates);

        List<LocalDate> expectedSortedDates = List.of(
                LocalDate.of(2005, 1, 2),
                LocalDate.of(2007, 1, 1),
                LocalDate.of(2032, 5, 3),
                LocalDate.of(2004, 7, 1)
        );
        assertThat(actualSortedDates).containsExactlyElementsOf(expectedSortedDates);
    }
}