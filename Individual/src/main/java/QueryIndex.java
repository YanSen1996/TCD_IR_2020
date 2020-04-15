import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QueryIndex {

    // the location of the search index
    private static String INDEX_DIRECTORY = "../Individual/index";

    // Limit the number of search results we get
    private static int MAX_RESULTS = 1400;

    public static void main(String[] args) throws IOException, ParseException {

        // Analyzer used by the query parser.
        // Must be the same as the one used when creating the index
        Analyzer analyzer = new EnglishAnalyzer();

        // Open the folder that contains our search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Create objects to read and search across the index
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        indexSearcher.setSimilarity(new BM25Similarity());

        // Open the query file
        String file = "../cran/cran.qry";
        String content = new String(Files.readAllBytes(Paths.get(file)));
        String[] queries = content.split("\\.I ");

        // Delete the blanks
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < queries.length && queries.length > 0; i++) {
            if (queries[i] == null || "".equals(queries[i].trim().toString())) {
                continue;
            } else {
                list.add(queries[i]);
            }
        }
        String[] newQueries = new String[list.size()];
        for (int i = 0; i < newQueries.length; i++) {
            newQueries[i] = list.get(i);
        }

        String[] ID = new String[newQueries.length];
        String[] Queries = new String[newQueries.length];

        for (int i = 0; i < newQueries.length; i++) {
            // Load the text of the file
            String[] split = newQueries[i].split("\\.W");

            // Split the text and add the value into the array
            ID[i] = split[0];
            Queries[i] = split[1];
        }

        // Create the query parser. The default search field is "words"
        QueryParser parser = new QueryParser("Words", analyzer);

        String queryString = "";
        List<String> resultsList = new ArrayList<>();

        for (int j = 0; j < newQueries.length; j++) {

            // Trim leading and trailing whitespace from the query
            queryString = Queries[j].trim();

            // If the user entered a query string
            if (queryString.length() > 0) {

                // Parse the query with the parser
                Query query = parser.parse(QueryParser.escape(queryString));

                // Get the set of results
                ScoreDoc[] hits = indexSearcher.search(query, MAX_RESULTS).scoreDocs;

                // Save the results
                for (int i = 0; i < hits.length; i++) {
                    Document hitDoc = indexSearcher.doc(hits[i].doc);
                    String sResults = (j + 1) + " 0 " + hitDoc.get("ID").trim() + " " + (i + 1) + " " + hits[i].score
                            + " run-tag";
                    String outString = "Getting the result " + (i + 1) + " of query " + (j + 1) + "...";
                    resultsList.add(sResults);
                    System.out.println(outString);
                }
            }
        }

        // Create a results file
        System.out.println("\nCreating results file...");
        BufferedWriter resultsWriter = new BufferedWriter(new FileWriter("../trec_eval-9.0.7/individual/results"));
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
