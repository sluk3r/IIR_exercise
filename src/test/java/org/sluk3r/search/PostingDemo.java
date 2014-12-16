package org.sluk3r.search;

import org.junit.Test;
import org.sluk3r.search.index.DocPair;
import org.sluk3r.search.index.PostingContainer;
import org.sluk3r.search.index.PostingContainerMultimapImpl;

import java.util.Iterator;

public class PostingDemo {

    @Test
    public void test() {
        PostingContainer container = new PostingContainerMultimapImpl();
        DocPair.generator(DocPair.makeDocs(), container);

        Iterator it = container.iterator();

        while(it.hasNext()) {
            Object o = it.next();

            System.out.println(o);
        }
    }
}
