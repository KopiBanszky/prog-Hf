package System.Tree.Comparators;

import System.Tree.Container;

import java.util.Comparator;

public class NameComparatorAsc implements Comparator<Container> {
    @Override
    public int compare(Container o1, Container o2) {
        return o1.getName().compareTo(o2.getName());
    }

    public boolean equals(Object obj) {
        return obj instanceof NameComparatorAsc;
    }
}
