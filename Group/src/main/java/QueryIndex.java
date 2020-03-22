import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class QueryIndex {

    // The location of the search index
    private static String INDEX_DIRECTORY = "../Group/index";

    // Limit the number of search results we get
    private static int MAX_RESULTS = 1000;

    public static void main(String[] args) throws IOException, ParseException {

        // Analyzer used by the query parser, the same as the one used in index
        // EnglishAnalyzer shall be changed later
        Analyzer analyzer = new EnglishAnalyzer();

        // The code below are used when index has been finished
//
//        // Open the folder that contains our search index
//        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
//
//        // Create objects to read and search across the index
//        DirectoryReader directoryReader = DirectoryReader.open(directory);
//        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
//        indexSearcher.setSimilarity(new BM25Similarity());

        // Open the topic file and split
        String file = "topics";
        String content = new String(Files.readAllBytes(Paths.get(file)));
        String[] topics = content.split("<top>");

        // Delete the blank items
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < topics.length && topics.length > 0; i++) {
            if (topics[i] == null || "".equals(topics[i].trim().toString())) { continue; }
            else { list.add(topics[i]); }
        }
        String[] newTopics = new String[list.size()];
        for (int i = 0; i < newTopics.length; i++) { newTopics[i] = list.get(i); }

        // ArrayList of documents in the corpus
        ArrayList<Document> queries = new ArrayList<Document>();

        String[] NUMBs = new String[newTopics.length];
        String[] TITLs = new String[newTopics.length];
        String[] DESCs = new String[newTopics.length];
        String[] NARRs = new String[newTopics.length];

        // Add all the individual topics to the list
        for (String txt:newTopics)
        {
            // Load the text of the file
            String[] split = newTopics[0].split("<num> Number: |<title> |<desc> Description: |<narr> Narrative: |</top>");

            String num = split[1];
            String title = split[2];
            String desc = split[3];
            String narr = split[4];

            // Create a new document and the file's information
            Document query = new Document();
            query.add(new StringField("Num", num, Field.Store.YES));
            query.add(new TextField("Title", title, Field.Store.YES));
            query.add(new TextField("Desc", desc, Field.Store.YES));
            query.add(new TextField("Narr", narr, Field.Store.YES));

            // Add the file to our linked list
            System.out.printf("Indexing  File " + num);
            queries.add(query);
        }
    }
}
