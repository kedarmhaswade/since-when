package org.kedar.tools.since;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.File;

/**
 * Created by kmhaswade on 2/18/16.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // todo this is *very rudimentary*
        if (args.length != 2) {
            System.out.println("Usage: run the jar with two parameters: <path to src.zip> <some-part-of-class-name>");
            return;
        }
        File srcZip = new File(args[0]);
        String className = args[1];
        final ZipFile file = new ZipFile(srcZip);
        try {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.getName().indexOf(className) != -1) {
                    System.out.println("Class: " + entry.getName());
                    CompilationUnit cu = JavaParser.parse(file.getInputStream(entry));
                    MethodVisitor mv = new MethodVisitor();
                    mv.visit(cu, null);
                    System.out.println();
                }
            }
        } finally {
            file.close();
        }
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        int num = 0;

        @Override
        public void visit(MethodDeclaration md, Object arg) {
            if (md.getComment() == null)
                return;
            String comment = md.getComment().toString();
            Pattern p = Pattern.compile("(@since )(\\d.\\d)");
            Matcher matcher = p.matcher(comment);
            if (matcher.find()) {
                String v = matcher.group(2);
                if ("1.8".equals(v)) { //todo 1.8 is hardcoded
                    System.out.println("\t" + (num + 1) + ": " + md.getDeclarationAsString() + " since: " + v);
                    num += 1;
                }
            }
        }
    }
}
