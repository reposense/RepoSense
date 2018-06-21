package reposense.authorship.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.LineInfo;
import reposense.authorship.model.MethodInfo;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

public class MethodAnalyzer {

    private static final Logger logger = LogsManager.getLogger(MethodAnalyzer.class);

    public static void aggregateMethodInfo(FileInfo fileInfo, RepoConfiguration config) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(config.getRepoRoot() + "/" + fileInfo.getPath());
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        CompilationUnit cu;
        try {
            cu = JavaParser.parse(in);
        } catch (Exception e) {
            return;
        }

        MethodVisitor methodVistor = new MethodVisitor();
        methodVistor.visit(cu, null);
        ArrayList<MethodInfo> methods = methodVistor.getMethods();
        for (MethodInfo methodInfo : methods) {
            HashMap<Author, Integer> contributorMap = new HashMap<>();
            for (int lineNum = methodInfo.getStart(); lineNum <= methodInfo.getEnd(); lineNum++) {
                LineInfo line = fileInfo.getLine(lineNum);
                Author author = line.getAuthor();
                if (config.getAuthorList().isEmpty() || config.getAuthorList().contains(author)) {
                    int authorLineCount = contributorMap.getOrDefault(author, 0);
                    contributorMap.put(author, authorLineCount + 1);
                }
                //line.setMethodInfo(methodInfo);
            }
            if (!contributorMap.isEmpty()) {
                Author owner = Collections.max(
                        contributorMap.entrySet(), (author1, author2) -> (author1.getValue() - author2.getValue())
                ).getKey();
                methodInfo.setOwner(owner);
            } else {
                methodInfo.setOwner(new Author(""));
            }
        }
        //fileInfo.setMethodInfos(methods);
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {

        private ArrayList<MethodInfo> methods = new ArrayList<MethodInfo>();

        private int identifierCounter = 0;

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            methods.add(new MethodInfo(n.getBegin().get().line, n.getEnd().get().line, n.getName().toString()));
            super.visit(n, arg);
        }

        @Override
        public void visit(ConstructorDeclaration n, Void arg) {
            methods.add(new MethodInfo(n.getBegin().get().line, n.getEnd().get().line, n.getName().toString()));
            super.visit(n, arg);
        }

        public ArrayList<MethodInfo> getMethods() {
            return methods;
        }

    }

}
