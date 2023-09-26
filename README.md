## Palanquin test task

The goal was to implement `DateSorter#sortDates(List<LocalDate>)` method:
```
/**
 * Marking will be based upon producing a readable, well engineered solution rather than factors
 * such as speed of processing or other performance-based optimizations, which are less
 * important.
 *
 * Implement in single class. Don't chane constructor and signature method.
 */
public class DateSorter {
    /**
     * The implementation of this method should sort dates.
     * The output should be in the following order:
     * Dates with an 'r' in the month,
     * sorted ascending (first to last),
     * then dates without an 'r' in the month,
     * sorted descending (last to first).
     * For example, October dates would come before May dates,
     * because October has an 'r' in it.
     * thus: (2004-07-01, 2005-01-02, 2007-01-01, 2032-05-03)
     * would sort to
     * (2005-01-02, 2007-01-01, 2032-05-03, 2004-07-01)
     *
     * @param unsortedDates - an unsorted list of dates
     * @return the collection of dates now sorted as per the spec
     */
    public Collection<LocalDate> sortDates(List<LocalDate> unsortedDates) {
        ...
    }
}
```

Using provided example I also implemented a few test methods to make sure the implementation 
works as expected.

### Implementation details

Implemented `DateSorter` is configured using `List<SubsetRule>`.
Each `SubsetRule` object defines how to sort specific subset of dates. After being sorted, the subsets
are placed in the order of `SubsetRule` occurrence.

`SubsetSortRule` has two fields:
```
Predicate<LocalDate> belongsToSubset;
Comparator<LocalDate> subsetSortComparator;
```
* Predicate `belongsToSubet` defines whether an object should be treated as a part of this subset.
* Comparator `subsetSortComparator` is used to sort items of the subset.

Date objects that don't belong to any subset are handled by `FALLBACK_RULE` special case object
which is the last one in the list.
