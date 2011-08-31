package be.dieterdemeyer.metro.online.util;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PdfUtil {

    public static void merge(List<InputStream> streamOfPDFFiles, OutputStream outputStream) {
        try {
            int pdfFileIndex = 0;
            PdfReader reader = new PdfReader(streamOfPDFFiles.get(pdfFileIndex));
            int numberOfPages = reader.getNumberOfPages();
            Document document = new Document(reader.getPageSizeWithRotation(1));
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfImportedPage page;
            int rotation;
            while (pdfFileIndex < streamOfPDFFiles.size()) {
                int i = 0;
                while (i < numberOfPages) {
                    i++;
                    document.setPageSize(reader.getPageSizeWithRotation(i));
                    document.newPage();
                    page = writer.getImportedPage(reader, i);
                    rotation = reader.getPageRotation(i);
                    if (rotation == 90 || rotation == 270) {
                        cb.addTemplate(page, 0, -1f, 1f, 0, 0, reader.getPageSizeWithRotation(i).getHeight());
                    } else {
                        cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                    }
                }

                pdfFileIndex++;

                if (pdfFileIndex < streamOfPDFFiles.size()) {
                    reader = new PdfReader(streamOfPDFFiles.get(pdfFileIndex));
                    numberOfPages = reader.getNumberOfPages();
                }
            }

            document.close();
            outputStream.close();
        } catch (Exception e) {
        }
    }

    public static void concat(List<InputStream> streamOfPDFFiles, OutputStream outputStream, boolean paginate) {
        Document document = new Document();

        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            PdfContentByte cb = writer.getDirectContent();

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);
                }
                pageOfCurrentReaderPDF = 0;
            }

            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static void insert(InputStream firstPdfFile, InputStream secondPdfFile, int insertAfter, OutputStream outputStream) {
        try {
            PdfReader firstPdfReader = new PdfReader(firstPdfFile);
            PdfReader secondPdfReader = new PdfReader(secondPdfFile);

            int numberOfPagesFirstPdf = firstPdfReader.getNumberOfPages();
            int numberOfPagesSecondPdf = secondPdfReader.getNumberOfPages();

            Document document = new Document(firstPdfReader.getPageSizeWithRotation(1));

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();

            PdfContentByte cb = writer.getDirectContent();

            PdfImportedPage page;
            int rotation;
            int i = 0;
            int j = 0;
            while (i < numberOfPagesFirstPdf) {
                if (i == insertAfter) {
                    while (j < numberOfPagesSecondPdf) {
                        j++;
                        document
                                .setPageSize(secondPdfReader.getPageSizeWithRotation(j));
                        document.newPage();
                        page = writer.getImportedPage(secondPdfReader, 1);

                        rotation = secondPdfReader.getPageRotation(j);
                        if (rotation == 90 || rotation == 270) {
                            cb.addTemplate(page, 0, -1f, 1f, 0, 0, secondPdfReader.getPageSizeWithRotation(j).getHeight());
                        } else {
                            cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                        }

                    }
                }

                i++;

                document.setPageSize(firstPdfReader.getPageSizeWithRotation(i));
                document.newPage();

                page = writer.getImportedPage(firstPdfReader, i);

                rotation = firstPdfReader.getPageRotation(i);
                if (rotation == 90 || rotation == 270) {
                    cb.addTemplate(page, 0, -1f, 1f, 0, 0, firstPdfReader.getPageSizeWithRotation(i).getHeight());
                } else {
                    cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                }
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PdfUtil() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate a static utility class");
    }

}