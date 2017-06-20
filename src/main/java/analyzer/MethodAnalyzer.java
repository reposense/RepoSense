package analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import data.Author;
import data.FileInfo;
import data.MethodInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by matanghao1 on 19/6/17.
 */
public class MethodAnalyzer {
    public static void aggregateMethodInfo(FileInfo fileInfo, String rootRepo){
        FileInputStream in = null;
        try {
            in = new FileInputStream(rootRepo + "/" + fileInfo.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CompilationUnit cu = JavaParser.parse(in);

        MethodVisitor methodVistor = new MethodVisitor();
        methodVistor.visit(cu, null);
        ArrayList<MethodInfo> methods = methodVistor.getMethods();
        for (MethodInfo methodInfo : methods){
            HashMap<Author, Integer> countributorMap = new HashMap<>();
            for (int line = methodInfo.getStart(); line<=methodInfo.getEnd();line++){
                Author author = fileInfo.getLineByNumber(line).getAuthor();
                int authorLineCount = countributorMap.getOrDefault(author,0);
                countributorMap.put(author , authorLineCount+1);
            }
            Author owner = Collections.max(countributorMap.entrySet(),(author1,author2) -> (author1.getValue() - author2.getValue())).getKey();
            methodInfo.setOwner(owner);
        }
        fileInfo.setMethodInfos(methods);
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {

        ArrayList<MethodInfo> methods = new ArrayList<MethodInfo>();

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            methods.add(new MethodInfo(n.getBegin().get().line,n.getEnd().get().line,n.getName().toString()));
            super.visit(n, arg);
        }

        public ArrayList<MethodInfo> getMethods(){
            return methods;
        }
    }

}
