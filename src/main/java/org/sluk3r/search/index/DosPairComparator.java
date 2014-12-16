package org.sluk3r.search.index;

import java.util.Comparator;

/**
 * Created by baiing on 2014/10/20.
 */
public class DosPairComparator implements Comparator<DocPair> {

    @Override
    public int compare(DocPair d1, DocPair d2) {
        return d1.token.compareTo(d2.token);
    }
}
