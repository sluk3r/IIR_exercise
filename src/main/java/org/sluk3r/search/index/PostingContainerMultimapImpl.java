package org.sluk3r.search.index;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.util.Iterator;

/**
 * Created by baiing on 2014/10/20.
 */
public class PostingContainerMultimapImpl implements PostingContainer {
    Multimap<String,DocPair> scoreMultimap = TreeMultimap.create();
    @Override
    public void add(DocPair docPair) {
        scoreMultimap.put(docPair.getToken(), docPair);
    }

    @Override
    public Iterator iterator() {
        return scoreMultimap.entries().iterator();
    }
}
