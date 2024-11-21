package System.Tree.Comparators;

import System.Tree.Container;
import System.Tree.Folder;
import System.Tree.Item;

import java.util.Comparator;

public class TypeComparator implements Comparator<Container> {
    @Override
    public int compare(Container o1, Container o2) {
        // o1 < o2 -> -1
        // o1 = o2 -> 0
        // o1 > o2 -> 1
        if(o1 instanceof Folder && o2 instanceof Folder) {
            return 0;
        } else if(o1 instanceof Folder && o2 instanceof Item) {
            return -1;
        } else if(o1 instanceof Item && o2 instanceof Folder) {
            return 1;
        } else {
            return 0;
        }
    }
}
