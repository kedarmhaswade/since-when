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
        File srcZip = new File(args[0]);

        final ZipFile file = new ZipFile(srcZip);
        try {
            final Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                System.out.println(entry.getName());
                CompilationUnit cu = JavaParser.parse(file.getInputStream(entry));
                MethodVisitor mv = new MethodVisitor();
                mv.visit(cu, null);
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
                if ("1.8".equals(v)) {
                    System.out.println((num + 1) + ": " + md.getDeclarationAsString() + " since: " + v);
                    num += 1;
                }
            }
        }
    }
}
