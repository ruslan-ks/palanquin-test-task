package sample;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DateSorter {
    public static class SubsetSortRule {
        private final Predicate<LocalDate> subsetDetectionPredicate;
        private final Comparator<LocalDate> subsetSortComparator;

        /**
         * @param subsetDetectionPredicate defines whether an item belongs to this subset
         * @param subsetSortComparator Comparator used to sort an items inside the subset
         */
        public SubsetSortRule(Predicate<LocalDate> subsetDetectionPredicate,
                              Comparator<LocalDate> subsetSortComparator) {
            this.subsetDetectionPredicate = subsetDetectionPredicate;
            this.subsetSortComparator = subsetSortComparator;
        }
    }

    private static final Predicate<LocalDate> MONTH_NAME_CONTAINS_R = date -> monthNameContainsLetter(date, 'r');

    // 2 subsets by default (according to the task):
    // First contains dates with month containing 'r' letter and is ascending
    // Second contains all the other dates in descending order
    private List<SubsetSortRule> subsetSortRules = List.of(
            new SubsetSortRule(MONTH_NAME_CONTAINS_R, Comparator.naturalOrder()),
            new SubsetSortRule(MONTH_NAME_CONTAINS_R.negate(), Comparator.reverseOrder())
    );

    private static boolean monthNameContainsLetter(LocalDate date, char letter) {
        String uppercaseLetter = String.valueOf(letter).toUpperCase();
        return date.getMonth().name().contains(uppercaseLetter);
    }

    /**
     * @param subsetSortRules the rules that define how to sort each subset;
     *                        sorted subsets will be concatenated in order of rule occurrence
     */
    public void setSubsetSortRules(List<SubsetSortRule> subsetSortRules) {
        this.subsetSortRules = subsetSortRules;
    }

    /**
     * Sort dates according to the subsetSortRules
     * @param unsortedDates - an unsorted list of dates
     * @return the collection of dates now sorted as per the spec
     */
    public Collection<LocalDate> sortDates(List<LocalDate> unsortedDates) {
        Stream<Stream<LocalDate>> sortedSubsets = extractSortedSubsets(unsortedDates);
        return concatSubsets(sortedSubsets);
    }

    private Stream<Stream<LocalDate>> extractSortedSubsets(List<LocalDate> dates) {
        return subsetSortRules.stream()
                .map(subsetSortRule -> extractAndSortSubset(dates, subsetSortRule));
    }

    private Stream<LocalDate> extractAndSortSubset(List<LocalDate> dates, SubsetSortRule subsetSortRule) {
        return dates.stream()
                .filter(subsetSortRule.subsetDetectionPredicate)
                .sorted(subsetSortRule.subsetSortComparator);
    }

    private <T> List<T> concatSubsets(Stream<Stream<T>> subsets) {
        return subsets
                .flatMap(Function.identity())
                .toList();
    }
}