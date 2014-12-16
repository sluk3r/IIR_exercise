package org.sluk3r.algo;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

/**
 * Created by baiing on 2014/10/27.
 */
public class SPIMIImpl {

    private List<Object> files;
    private final String pathBlocks = "D:\\work2\\";
    private final String pathInvIndex = "./inverted_index.txt";

    public SPIMIImpl (final File folderPath, final String[] fileExtentions, final boolean checkSubfolders) {
        this.files = new ArrayList<Object>(FileUtils.listFiles(folderPath, fileExtentions, checkSubfolders));
        createInvertedIndex(fileExtentions);
    }

    public void createInvertedIndex(final String[] fileExtentions) {
        createBlocks();
        mergeBlocks(fileExtentions);
    }

    private void mergeBlocks(final String[] fileExtentions) {
        File fileBlocks = new File(this.pathBlocks);
        List<Object> blocks = new ArrayList<Object>(FileUtils.listFiles(fileBlocks, fileExtentions, false));
        int size = blocks.size();

        Container[] containers = new Container[size];
        Map<String, Container> tmpTreeMap = new TreeMap<String, Container>();
        boolean[] b = new boolean[size];

        Scanner[] scArr = new Scanner[size];
        for(int i = 0; i < size; i++) {
            try {
                System.out.println("Block:" + blocks.get(i).toString());
                scArr[i] = new Scanner(new File(blocks.get(i).toString()));
                b[i] = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        while(areScannersOpen(b)) {
            for(int i = 0; i < size; i++) {
                if(containers[i] == null) {
                    if(scArr[i].hasNextLine()) {
                        Set<Integer> blockIds = new HashSet<Integer>();
                        blockIds.add(i);
                        Container tmpContainer = new Container();
                        containers[i] = tmpContainer.stringToContainer(scArr[i].nextLine(), blockIds);
                    }
                    else { // has no next line
                        // file is read to the end
                        b[i] = false;
                    }
                }
            }

            for(int i = 0; i < size; i++) {
                if(containers[i] != null ) {
                    if(tmpTreeMap.containsKey(containers[i].term)) {
                        Container c = containers[i];
                        String term = c.term;
                        tmpTreeMap.put(term, c.intersectContainers(tmpTreeMap.get(term), c));
                    }
                    else {
                        tmpTreeMap.put(containers[i].term, containers[i]);
                    }
                }
            }

            if(!tmpTreeMap.isEmpty()) {
                String minTerm = ((TreeMap<String, Container>) tmpTreeMap).firstKey();
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.pathInvIndex, true)));
                    out.println(minTerm + "=" + tmpTreeMap.get(minTerm).docID);
                    out.close();
                } catch(IOException e) {
                }

                Set<Integer> setTmp = tmpTreeMap.get(minTerm).blockID;
                for(Integer indexToDelete : setTmp) {
                    containers[indexToDelete] = null;
                }

                tmpTreeMap.remove(minTerm);
            }
        }
    }

    private void createBlocks() {
        Map<String, Set<Integer>> block = new TreeMap<String, Set<Integer>>(); //直接用TreeMap做倒排索引的存储？
        int blockNum = 0;
        boolean isLastBlock = true;
        for (int i = 0; i < this.files.size(); i++) {
            try {

                Scanner sc = new Scanner(new File(this.files.get(i).toString()));
                System.out.println("#" + i + " " + this.files.get(i));
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] tokensArray = lineToTokensArray(line);
                    for (String token : tokensArray) {
                        if (Runtime.getRuntime().freeMemory() < 20000) {
                            System.out.println("Writing a block to disk in order to free the memory");
                            blockNum++;
                            isLastBlock = false;
                            writeBlockToDisk(block, this.pathBlocks + "block"
                                    + blockNum + ".txt");
                            block = new TreeMap<String, Set<Integer>>();
                            isLastBlock = false;
                        } else {
                            isLastBlock = true;
                            if (block.containsKey(token)) {
                                Set<Integer> docIDs = block.get(token);
                                docIDs.add(i);
                                block.put(token, docIDs);
                            } else {
                                Set<Integer> docIDs = new HashSet<Integer>();
                                docIDs.add(i);
                                block.put(token, docIDs);
                            }
                        }
                    }
                }
                sc.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (isLastBlock) { //这个isLastBlock代表着什么？
            blockNum++;
            writeBlockToDisk(block, this.pathBlocks + "block"
                    + blockNum + ".txt");
        }
    }

    private boolean areScannersOpen(boolean[] b) {
        for (int i = 0; i < b.length; i++) {
            if (b[i]) {
                return true;
            }
        }
        return false;
    }

    private void writeBlockToDisk(Map<String, Set<Integer>> dictionary,
                                  String pathBlock) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(pathBlock);
            Collection entrySet = dictionary.entrySet();
            Iterator it = entrySet.iterator();
            while (it.hasNext())
                writer.println(it.next());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String[] lineToTokensArray(String line) {
        String[] splited = line.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s : splited) {
            s = s.toLowerCase().replaceAll("[^a-z0-9]+", ""); //不是字母和数字的都直接替换掉了。
            if (!s.equals("")) {
                sb.append(s + " ");
            }
        }
        String[] tokensArray = sb.toString().split(" ");
        return tokensArray;
    }

    private class Container {
        public Set<Integer> blockID;
        public String term;
        public Set<Integer> docID;

        public Container(Set<Integer> blockId, String term, Set<Integer> docId) {
            this.blockID = blockId;
            this.term = term;
            this.docID = docId;
        }

        public Container() {

        }

        public Container intersectContainers(Container a, Container b) {
            Set<Integer> setDocIds = new HashSet<Integer>();
            setDocIds.addAll(a.docID);
            setDocIds.addAll(b.docID);
            Set<Integer> setBlockIds = new HashSet<Integer>();
            setBlockIds.addAll(a.blockID);
            setBlockIds.addAll(b.blockID);
            return new Container(setBlockIds, a.term, setDocIds);
        }

        public Container stringToContainer(String s, Set<Integer> blockId) {
            String[] splitKeyValue = s.split("=");
            String term = splitKeyValue[0];
            String[] valueDirty = splitKeyValue[1].replace("[", "").replace("]", "").replace(" ", "").split(",");
            Set<Integer> set = new HashSet<Integer>();
            for(String v : valueDirty) {
                set.add(Integer.parseInt(v));
            }
            return new Container(blockId, term, set);
        }

        @Override
        public boolean equals(Object other){
            if (other == null)
                return false;
            if (other == this)
                return true;
            if (!(other instanceof Container))
                return false;
            Container c = (Container) other;
            if(c.term.equals(this.term))
                return true;
            else
                return false;
        }
    }



    public static void main(String[] args) {
        File folder = new File("D:\\work2\\search-lib\\");
        String[] extentions = {"txt", "java"};
        SPIMIImpl spimi = new SPIMIImpl(folder, extentions, true);
    }
}
