import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryIndex {

    // The location of the search index
    // private static String INDEX_DIRECTORY = "../Group/index/siqi";  // Siqi's Index
    private static String INDEX_DIRECTORY = "../Group/index/ankita";  // Ankita's Index

    // Limit the number of search results we get
    private static int MAX_RESULTS = 1000;

    public static void main(String[] args)
            throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {

        // Analyzer used by the query parser, the same as the one used in index

        // Analyzer analyzer = new EnglishAnalyzer();  // EnglishAnalyzer
    	Analyzer analyzer = new CustomAnalyzer();  // CustomAnalyzer

        // Open the folder that contains our search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Create objects to read and search across the index
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        indexSearcher.setSimilarity(new BM25Similarity());

        // Open the topic file and split
        String file = "topics";
        String content = new String(Files.readAllBytes(Paths.get(file)));
        String[] topics = content.split("<top>");

        // Delete the blank items
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < topics.length && topics.length > 0; i++) {
            if (topics[i] == null || "".equals(topics[i].trim().toString())) {
                continue;
            } else {
                list.add(topics[i]);
            }
        }
        String[] newTopics = new String[list.size()];
        for (int i = 0; i < newTopics.length; i++) {
            newTopics[i] = list.get(i);
        }

        // Divide the topic into four parts - num, title, desc and narrative
        String[] NUMBs = new String[newTopics.length];
        String[] TITLs = new String[newTopics.length];
        String[] DESCs = new String[newTopics.length];
        String[] NARRs = new String[newTopics.length];

        for (int i = 0; i < newTopics.length; i++) {
            // Load the text of this topic
            String[] split = newTopics[i]
                    .split("<num> Number: |<title> |<desc> Description: |<narr> Narrative: |</top>");

            NUMBs[i] = split[1].trim();
            TITLs[i] = split[2].trim();
            DESCs[i] = split[3].trim();
            NARRs[i] = split[4].trim();
        }

        // Detect if the words in title has been adopted in the description part
        // Define a query based on title and description parts
        String[] simpleQueries = new String[newTopics.length];
        for (int i = 0; i < newTopics.length; i++) {
            if (TITLs[i].contains(", ")) {
                String[] split = TITLs[i].split(", ");
                for (String txt : split) {
                    if (DESCs[i].contains(txt.trim())) {
                        simpleQueries[i] = DESCs[i];
                    } else {
                        simpleQueries[i] = "Considering " + txt.trim() + ", " + DESCs[i];
                    }
                }
            } else {
                if (DESCs[i].contains(TITLs[i].trim())) {
                    simpleQueries[i] = DESCs[i];
                } else {
                    simpleQueries[i] = "Considering " + TITLs[i].trim() + ", " + DESCs[i];
                }
            }
        }

        // Find "not relevant", "unrelated to" and "irrelevant" in narrative part.
        // Then, complete the query with the narrative part.
        String[] Queries = new String[newTopics.length];
        for (int i = 0; i < newTopics.length; i++) {
            String[] split = NARRs[i].split("\\.|\\. |\\?|\\? |; ");
            for (String txt : split) {
                if (txt.contains("not relevant") || txt.contains("irrelevant") || txt.contains(("unrelated to"))) {
                    Queries[i] = simpleQueries[i] + " But attention, " + txt.trim() + ".";
                } else {
                    Queries[i] = simpleQueries[i].toLowerCase().trim();
                }
            }
        }

        // Create the query parser. The default search field is "words"
        // QueryParser parser = new QueryParser("TEXT", analyzer); // single-field
        QueryParser parser = new MultiFieldQueryParser(new String[] {LuceneConstants.HEADLINE,
            LuceneConstants.TEXT, LuceneConstants.BYLINE, LuceneConstants.HEADING, LuceneConstants.HEADER,
            LuceneConstants.FOOTNOTE, LuceneConstants.ADDRESS, LuceneConstants.AGENCY, LuceneConstants.SUMMARY,
            LuceneConstants.TITLE, LuceneConstants.US_BUREAU, LuceneConstants.US_DEPARTMRNT, LuceneConstants.DATE_LINE,
            LuceneConstants.SUBJECT}, analyzer); // multi-field

        String queryString = "";
        List<String> resultsList = new ArrayList<>();

        for (int j = 0; j < Queries.length; j++) {

            // Trim leading and trailing whitespace from the query
            queryString = Queries[j].trim();

            // If the user entered a query string
            if (queryString.length() > 0) {

                // Parse the query with the parser
                Query query = parser.parse(QueryParser.escape(queryString));

                // Get the set of results
                ScoreDoc[] hits = indexSearcher.search(query, MAX_RESULTS).scoreDocs;

                // Save the results
                ArrayList<String> docNums = new ArrayList<String>();
                for (int i = 0; i < hits.length; i++) {
                    Document hitDoc = indexSearcher.doc(hits[i].doc);

                    if(docNums.contains(hitDoc.get("DOCNO").trim())) {
                    	continue;
                    }
                    else {
                        String sResults = (j + 401) + " 0 " + hitDoc.get("DOCNO").trim() + " " + (i + 1) + " " + hits[i].score + " ankita-cus-single";
                        String outString = "Getting the result " + (i + 1) + " of query " + (j + 1) + "...";
                        resultsList.add(sResults);
                        System.out.println(outString);
                        docNums.add(hitDoc.get("DOCNO").trim());
                    }
                }
            }
        }

        // Create a results file
        System.out.println("\nCreating results file...");
        String RESULTS_DIRECTOTY = "../trec_eval-9.0.7/group/results";
        File result = new File(RESULTS_DIRECTOTY);
        if (!result.exists()){
            File dir = new File(result.getParent());
            dir.mkdirs();
            result.createNewFile();
        }
        BufferedWriter resultsWriter = new BufferedWriter(new FileWriter(RESULTS_DIRECTOTY));
        for (String item : resultsList) {
            resultsWriter.write(item);
            resultsWriter.newLine();
        }
        System.out.println("\nResults file is successfully created!");

        resultsWriter.close();
        directoryReader.close();
        directory.close();

    }
}
