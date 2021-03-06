package org.freeeed.html;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.freeeed.mail.EmailUtil;
import org.freeeed.mail.EmlParser;
import org.freeeed.print.OfficePrint;
import org.freeeed.services.Util;

import com.google.common.io.Files;

/**
 *
 * Class DocumentToHtml.
 *
 * Generate html from various type of documents
 *
 * @author ilazarov
 *
 */
public class DocumentToHtml {

    private static DocumentToHtml __instance;
    private static final String DEFAULT_HTML_CONTENT = "<div>No HTML available</div>";

    //singleton
    private DocumentToHtml() {
    }

    public static synchronized DocumentToHtml getInstance() {
        if (__instance == null) {
            __instance = new DocumentToHtml();
        }

        return __instance;
    }

    public void createHtml(File officeDocFile, File outputHtml, String originalFileName) throws Exception {
        String extension = Util.getExtension(officeDocFile.getPath());
        if (extension == null || extension.isEmpty()) {
            extension = Util.getExtension(originalFileName);
        }

        if ("txt".equalsIgnoreCase(extension)) {
            OfficePrint.getInstance().convertToPdfWithSOffice(officeDocFile, outputHtml);
        } else if ("eml".equalsIgnoreCase(extension)) {
            EmlParser emlParser = new EmlParser(officeDocFile);
            String emlHtmlContent = EmailUtil.createHtmlFromEmlFileNoCData(officeDocFile.getPath(), emlParser);
            Files.append(emlHtmlContent, outputHtml, Charset.defaultCharset());
        } else if ("doc".equalsIgnoreCase(extension) || "docx".equalsIgnoreCase(extension)) {
            try {
                OfficePrint.getInstance().convertToPdfWithSOffice(officeDocFile, outputHtml);
            } catch (Exception e) {
                printDefaultHtml(outputHtml);
            }
        } else {
            printDefaultHtml(outputHtml);
        }
    }

    private void printDefaultHtml(File outputHtml) throws IOException {
        Files.write(DEFAULT_HTML_CONTENT, outputHtml, Charset.defaultCharset());
    }
}
