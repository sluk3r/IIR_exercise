package org.sluk3r.search.index;

import java.util.Iterator;

/**
 * Created by baiing on 2014/10/20.
 */
public interface PostingContainer {
    void add(DocPair docPair);

    Iterator iterator();
}



