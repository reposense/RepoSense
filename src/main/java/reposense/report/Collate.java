package reposense.report;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;
import reposense.util.FileUtil;

public class Collate {

    private static final String MARKDOWN_TITLE = "# %s";
    private static final String MARKDOWN_H6 = "###### %s";
    private static final String MARKDOWN_CODE = "```";

    private HashMap<Author, HashMap<String, List<String>>> codeFromFileFromAuthor = new HashMap<>();
    private HashMap<Author, List<String>> codeFromAuthor = new HashMap<>();
    private List<Author> authors;
    private List<FileResult> fileResults;
    private Path saveDirectory;

    public Collate(List<Author> authors, List<FileResult> fileResults, Path saveDirectory) {
        this.authors = authors;
        this.fileResults = fileResults;
        this.saveDirectory = saveDirectory;
    }


    public void generateIndividualCollateFiles() {
        extractCodeFromFileResult();
        collateCodeFromFiles();
        collateCodeFromAuthors();
    }

    private void extractCodeFromFileResult() {
        for (FileResult fileResult : fileResults) {
            String file = fileResult.getPath();
            for (LineInfo lineInfo : fileResult.getLines()) {
                Author author = lineInfo.getAuthor();
                if (authors.contains(author)) {
                    addToCollation(author, file, lineInfo.getContent());
                }
            }
        }
    }

    private void addToCollation(Author author, String file, String line) {
        if (!codeFromFileFromAuthor.containsKey(author)) {
            codeFromFileFromAuthor.put(author, new HashMap<>());
        }
        if (!codeFromFileFromAuthor.get(author).containsKey(file)) {
            codeFromFileFromAuthor.get(author).put(file, new ArrayList<>());
        }
        codeFromFileFromAuthor.get(author).get(file).add(line);
    }

    private void collateCodeFromFiles() {
        for (Author author: authors) {
            codeFromAuthor.put(author, new ArrayList<>());
            HashMap<String, List<String>> filesWritten = codeFromFileFromAuthor.get(author);
            List<String> markUpLines = new ArrayList<>();
            if (filesWritten != null && !filesWritten.isEmpty()) {
                for (String file: filesWritten.keySet()) {
                    markUpLines.add(String.format(MARKDOWN_H6, file));
                    markUpLines.add(MARKDOWN_CODE);
                    for (String line: filesWritten.get(file)) {
                        markUpLines.add(line);
                    }
                    markUpLines.add(MARKDOWN_CODE);
                }
                codeFromAuthor.put(author, markUpLines);

            }
        }
    }

    private void collateCodeFromAuthors() {
        for (Author author: authors){
            List<String> codeByAuthor = codeFromAuthor.get(author);
            codeByAuthor.add(0, String.format(MARKDOWN_TITLE, author.getGitId()));
            FileUtil.writeToCollateFile(author.getGitId(), codeByAuthor, saveDirectory);
        }
    }

}
