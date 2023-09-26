package sample;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DateSorter {
    public static class SubsetRule {
        private final Predicate<LocalDate> belongsToSubset;
        private final Comparator<LocalDate> subsetSortComparator;

        /**
         * @param belongsToSubset      Defines whether an item belongs to this subset
         * @param subsetSortComparator Comparator used to sort items inside the subset
         */
        public SubsetRule(Predicate<LocalDate> belongsToSubset, Comparator<LocalDate> subsetSortComparator) {
            this.belongsToSubset = belongsToSubset;
            this.subsetSortComparator = subsetSortComparator;
        }
    }

    // Defines how to sort dates that don't belong to any subset.
    private static final SubsetRule FALLBACK_RULE = new SubsetRule(date -> true, Comparator.naturalOrder());

    private List<SubsetRule> subsetRules;

    public DateSorter() {
        setTaskSubsetRules();
    }

    private void setTaskSubsetRules() {
        Predicate<LocalDate> monthNameContainsR = date -> monthNameContainsLetter(date, 'r');

        // First dates with month containing 'r' letter in ascending order
        // Then all the other dates in descending order
        List<SubsetRule> taskRules = List.of(
                new SubsetRule(monthNameContainsR, Comparator.naturalOrder()),
                new SubsetRule(monthNameContainsR.negate(), Comparator.reverseOrder())
        );
        setSubsetSortRules(taskRules);
    }

    private static boolean monthNameContainsLetter(LocalDate date, char letter) {
        String uppercaseLetter = String.valueOf(letter).toUpperCase();
        return date.getMonth().name().contains(uppercaseLetter);
    }

    /**
     * @param subsetRules Rules that define how to sort each subset.
     *                    Date object belongs to the first subset whose {@link SubsetRule#belongsToSubset}
     *                    returns true.
     *                    Sorted subsets will be concatenated in order of rule occurrence.
     *                    Dates that don't belong to any subset will be placed in the end and sorted with
     *                    {@link DateSorter#FALLBACK_RULE}
     */
    public void setSubsetSortRules(List<SubsetRule> subsetRules) {
        this.subsetRules = new ArrayList<>(subsetRules);
        this.subsetRules.add(FALLBACK_RULE);
    }

    /**
     * Sort dates according to subsetRules
     * @param unsortedDates - an unsorted list of dates
     * @return the collection of dates now sorted as per the spec
     */
    public Collection<LocalDate> sortDates(List<LocalDate> unsortedDates) {
        List<LocalDate> datesCopy = new ArrayList<>(unsortedDates);

        List<List<LocalDate>> sortedSubsets = extractSortedSubsets(datesCopy);
        return concatSubsets(sortedSubsets);
    }

    private List<List<LocalDate>> extractSortedSubsets(List<LocalDate> dates) {
        return subsetRules.stream()
                .map(subsetRule -> extractAndSortSubset(dates, subsetRule))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<LocalDate> extractAndSortSubset(List<LocalDate> dates, SubsetRule subsetRule) {
        List<LocalDate> subset = extractSubset(dates, subsetRule);
        subset.sort(subsetRule.subsetSortComparator);
        return subset;
    }

    /**
     * Extract date objects from dates (updating list) into new subset according to subsetRule
     */
    private List<LocalDate> extractSubset(List<LocalDate> dates, SubsetRule subsetRule) {
        List<LocalDate> subset = dates.stream()
                .filter(subsetRule.belongsToSubset)
                .collect(Collectors.toCollection(ArrayList::new));
        dates.removeAll(subset);
        return subset;
    }

    private List<LocalDate> concatSubsets(List<List<LocalDate>> subsets) {
        return subsets.stream()
                .flatMap(Collection::stream)
                .toList();
    }
}