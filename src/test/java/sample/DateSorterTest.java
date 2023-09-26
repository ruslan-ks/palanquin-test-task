package sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DateSorterTest {
    private DateSorter dateSorter;

    @BeforeEach
    void setUp() {
        dateSorter = new DateSorter();
    }

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

    @Test
    void datesNotBelongingToAnySubsetArePlacedInTheEnd() {
        List<DateSorter.SubsetRule> subsetRules = List.of(
                new DateSorter.SubsetRule(this::isBefore2007, Comparator.reverseOrder())
        );
        dateSorter.setSubsetSortRules(subsetRules);

        List<LocalDate> unsortedDates = List.of(
                LocalDate.of(2004, 7, 1),
                LocalDate.of(2005, 1, 2),
                LocalDate.of(2007, 1, 1),
                LocalDate.of(2032, 5, 3)
        );
        Collection<LocalDate> actualSortedDates = dateSorter.sortDates(unsortedDates);

        List<LocalDate> expectedSortedDates = List.of(
                LocalDate.of(2005, 1, 2), // subset 1
                LocalDate.of(2004, 7, 1), // subset 1
                LocalDate.of(2007, 1, 1), // no subset
                LocalDate.of(2032, 5, 3)  // no subset
        );
        assertThat(actualSortedDates).containsExactlyElementsOf(expectedSortedDates);
    }

    private boolean isBefore2007(LocalDate date) {
        return date.isBefore(LocalDate.of(2007, 1, 1));
    }

    @Test
    void dateBelongsOnlyToTheFirstSuitableSubset() {
        List<DateSorter.SubsetRule> subsetRules = List.of(
                new DateSorter.SubsetRule(this::is2005, Comparator.naturalOrder()),
                new DateSorter.SubsetRule(this::isJanuary, Comparator.reverseOrder())
        );
        dateSorter.setSubsetSortRules(subsetRules);

        List<LocalDate> unsortedDates = List.of(
                LocalDate.of(2004, 1, 1),
                LocalDate.of(2005, 1, 2),
                LocalDate.of(2007, 1, 1),
                LocalDate.of(2032, 1, 3)
        );
        Collection<LocalDate> actualSortedDates = dateSorter.sortDates(unsortedDates);

        List<LocalDate> expectedSortedDates = List.of(
                LocalDate.of(2005, 1, 2), // subset 1
                LocalDate.of(2032, 1, 3), // subset 2
                LocalDate.of(2007, 1, 1), // subset 2
                LocalDate.of(2004, 1, 1)  // subset 2
        );
        assertThat(actualSortedDates).containsExactlyElementsOf(expectedSortedDates);
    }

    private boolean is2005(LocalDate date) {
        return date.getYear() == 2005;
    }

    private boolean isJanuary(LocalDate date) {
        return date.getMonth().equals(Month.JANUARY);
    }
}