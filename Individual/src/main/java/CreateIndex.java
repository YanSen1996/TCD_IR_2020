import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateIndex {

    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "../Individual/index";

    private Analyzer analyzer;
    private Directory directory;

    public static void main(String[] args) throws IOException {
        // Open the data
        String dataPath = "../cran/cran.all.1400";

        // Analyzer that is used to process TextField
        Analyzer analyzer = new EnglishAnalyzer();
//        Analyzer analyzer = new StandaardAnalyzer();
//        Analyzer analyzer = new ClassicAnalyzer();

        // ArrayList of documents in the corpus
        ArrayList<Document> documents = new ArrayList<Document>();

        // Open the directory that contains the search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Set up an index writer to add process and save documents to the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setSimilarity(new BM25Similarity());
//        config.setSimilarity(new ClassicSimilarity());
//        config.setSimilarity(new BooleanSimilarity());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Split the data into several documents
        String content = new String(Files.readAllBytes(Paths.get(dataPath)));
        String[] docs = content.split("\\.I ");

        // Delete the blank items
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < docs.length && docs.length > 0; i++) {
            if (docs[i] == null || "".equals(docs[i].trim().toString())) {
                continue;
            } else {
                list.add(docs[i]);
            }
        }
        String[] newDocs = new String[list.size()];
        for (int i = 0; i < newDocs.length; i++) {
            newDocs[i] = list.get(i);
        }

        // Add all the individual documents to the list
        for (String txt : newDocs) {
            // Load the text of the file
            String[] split = txt.split("\\.T|\\.A|\\.B|\\.W");

            // Split the text
            String id = split[0];
            String title = split[1];
            String author = split[2];
            String bib = split[3];
            String words = split[4];

            // Create a new document and the file's information
            Document doc = new Document();
            doc.add(new StringField("ID", id, Field.Store.YES));
            doc.add(new TextField("Title", title, Field.Store.YES));
            doc.add(new TextField("Author", author, Field.Store.YES));
            doc.add(new TextField("Bibliotheca", bib, Field.Store.YES));
            doc.add(new TextField("Words", words, Field.Store.YES));

            // Add the file to our linked list
            System.out.printf("Indexing  File " + id);
            documents.add(doc);
        }

        // Write all the documents in the linked list to the search index
        indexWriter.addDocuments(documents);
        System.out.println("\nIndex is successfully created!");

        // Commit everything and close
        indexWriter.close();
        directory.close();
    }
}
