package org.sluk3r.search.index;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * Created by baiing on 2014/10/20.
 */
public class DocPair implements  Comparable<DocPair>{
    String token;
    int docId;

    public DocPair(String token, int docId) {
        this.token = token;
        this.docId = docId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocPair)) return false;

        DocPair docPair = (DocPair) o;

        if (docId != docPair.docId) return false;
        if (!token.equals(docPair.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + docId;
        return result;
    }

    public static void  generator(List<String> docs, final PostingContainer postingContainer) {
        if (docs == null || docs.isEmpty()) {
            return ;
        }

        //排序和归并都是用这个Set来做的。
        int docId = 0;
        for (String doc : docs) {
            StringTokenizer st = new StringTokenizer(doc);

            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                postingContainer.add(new DocPair(token, docId));
            }
            docId++;
        }
    }

    @Override
    public int compareTo(DocPair o) {
        return o.docId - this.docId;
    }


    public static List<String> makeDocs() {
        return Arrays.asList(new String[]{"The most memorable moment came during the Q and A. What, asked a Googler to the politician, \\\"is the most efficient way to sort a million 32-bit integers?\\\". It was a hard-core programming question an engineer might be asked in a job interview at Google. But the candidate squinched up his face in concentration, as if racing through various programming alternatives. \\\"Well,\\\" he finally said, \\\"I think the bubble sort would be the wrong way to go", "Because John McCain was asked the same question by then Google CEO Eric Schmidt earlier in the year (May 2007), when he was invited to Google.", "President Obama's aides noticed that and prepped him for it. President Obama could have gone prepared with a better answer but Dr.  Eric Schmidt coulda followed up with a harder question, which is common in interviews for software development or engineering positions. So, he decided to use humor as a response."});
    }
}
