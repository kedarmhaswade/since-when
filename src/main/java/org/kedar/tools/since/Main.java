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
        if (args.length != 3) {
            System.out.println("Usage: run the jar with two parameters: <path to src.zip> <some-part-of-class-name> <sinceVersion>");
            return;
        }
        File srcZip = new File(args[0]);
        String className = args[1];
        String sinceVersion = args[2];
        System.out.println("Methods since: " + sinceVersion);
        try (ZipFile file = new ZipFile(srcZip)) {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.getName().contains(className)) {
                    System.out.println("Class: " + entry.getName());
                    CompilationUnit cu = JavaParser.parse(file.getInputStream(entry));
                    MethodVisitor mv = new MethodVisitor(sinceVersion);
                    mv.visit(cu, null);
                    System.out.println();
                }
            }
        }
    }

    private static class MethodVisitor extends VoidVisitorAdapter {
        private final String sinceVersion;
        int num = 0;

        public MethodVisitor(String sinceVersion) {
            this.sinceVersion = sinceVersion;
        }

        @Override
        public void visit(MethodDeclaration md, Object arg) {
            if (md.getComment() == null)
                return;
            String comment = md.getComment().toString();
            Pattern p = Pattern.compile("(@since )(\\d.\\d)");
            Matcher matcher = p.matcher(comment);
            if (matcher.find()) {
                String v = matcher.group(2);
                if (sinceVersion.equals(v)) {
                    System.out.println("\t" + (num + 1) + ": " + md.getDeclarationAsString() + " since: " + v);
                    num += 1;
                }
            }
        }
    }
}
