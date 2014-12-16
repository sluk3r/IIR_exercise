package org.sluk3r.search.index;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by baiing on 2014/10/20.
 */
public class PostingContainerTreeSet  implements  PostingContainer{
    SortedSet<DocPair> container = new TreeSet<DocPair>();


    @Override
    public void add(DocPair docPair) {
        container.add(docPair);
    }

    @Override
    public Iterator iterator() {
        return container.iterator();
    }
}
