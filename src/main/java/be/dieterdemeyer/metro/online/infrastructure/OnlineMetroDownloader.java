package be.dieterdemeyer.metro.online.infrastructure;

import be.dieterdemeyer.metro.online.util.Dates;
import be.dieterdemeyer.metro.online.util.Messages;
import be.dieterdemeyer.metro.online.util.PdfUtil;
import be.dieterdemeyer.metro.online.util.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OnlineMetroDownloader {

    private static final String BASE_URL = "http://www.metrotime.be/UserFiles/DigiPaper/nl/";

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private static final String MVLMP_PREFIX = "/MVLMP-0-";

    private static final String SLASH = "/";
    private static final String DASH = "-";

    private static final String METRO_FILENAME_PREFIX = "MetroOnline_";

    private static final String EXTENSION = ".pdf";

    private static final String TODAY = Dates.format(Dates.today(), "yyyyMMdd");

    private static final String METRO_DIR = System.getProperty("user.home") + "/Downloads/MetroOnline";
    private static final String PDF_MERGE_DIR = METRO_DIR + "/pdf_merge";

    private static Logger LOGGER = Logger.getLogger(OnlineMetroDownloader.class);

    private JavaMailSender mailSender;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // @Scheduled(cron = "0 * * * * ?")
    public void sendMail(String pathToFile) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setFrom(MailConstants.FROM);
        messageHelper.setTo(MailConstants.TO);
        messageHelper.setSubject(MailConstants.SUBJECT);
        messageHelper.setText(Messages.format(MailConstants.BODY, pathToFile, Dates.format(Dates.today(), "dd/MM/yyyy")), true);

        LOGGER.info("Sending mail...");
        mailSender.send(mimeMessage);
        LOGGER.info("Mail sent...");
    }

    @Scheduled(cron = "0 0 8 ? * MON-FRI")
    public void execute() throws Exception {
        long startTime = Dates.currentMillis();

        cleanDirs();

        List<String> pdfNames = new ArrayList<String>();
        for (int page = 1; page <= 40; page++) {
            URL url = new URL(BASE_URL + TODAY + SLASH + page + MVLMP_PREFIX + TODAY + DASH + Strings.addLeadingZero(String.valueOf(page)) + EXTENSION);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url.toURI());
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null && CONTENT_TYPE_PDF.equals(httpEntity.getContentType().getValue())) {

                File file = new File(PDF_MERGE_DIR + MVLMP_PREFIX + TODAY + DASH + Strings.addLeadingZero(String.valueOf(page)) + EXTENSION);

                try {
                    InputStream from = httpEntity.getContent();
                    LOGGER.info("Downloading " + url.toString() + "...");

                    FileOutputStream to = new FileOutputStream(file);

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = from.read(buffer)) != -1) {
                        to.write(buffer, 0, bytesRead);
                    }

                    from.close();
                    to.close();

                    pdfNames.add(file.getAbsolutePath());
                } catch (FileNotFoundException exception) {
                    LOGGER.info(url.toString() + " not found...");
                }
            }
        }

        File outputFile = mergePDFs(pdfNames);
        if (outputFile != null) {
            sendMail(outputFile.getAbsolutePath());
        }

        printElapsedTime(startTime, Dates.currentMillis());
    }

    private static File mergePDFs(List<String> pdfNames) {
        File outputFile = null;

        try {
            List<InputStream> pdfInputStreams = new ArrayList<InputStream>();
            for (String pdfName : pdfNames) {
                pdfInputStreams.add(new FileInputStream(pdfName));
            }

            if (pdfInputStreams.size() > 0) {
                outputFile = new File(METRO_DIR + SLASH + METRO_FILENAME_PREFIX + TODAY + EXTENSION);
                OutputStream output = new FileOutputStream(outputFile);

                PdfUtil.merge(pdfInputStreams, output);
                LOGGER.info("Merging of " + pdfNames.size() + " PDF files has been completed...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    private static void cleanDirs() throws IOException {
        File metroDir = new File(METRO_DIR);
        File pdfMergeDir = new File(PDF_MERGE_DIR);

        if (metroDir.mkdir())
            LOGGER.info(METRO_DIR + " created...");
        if (pdfMergeDir.mkdir())
            LOGGER.info(PDF_MERGE_DIR + " created...");

        FileUtils.cleanDirectory(pdfMergeDir);
        FileUtils.cleanDirectory(metroDir);
    }

    private static void printElapsedTime(long startTime, long endTime) {
        Period period = new Interval(startTime, endTime).toPeriod();
        System.out.printf("Elapsed Time: %d hours, %d minutes, %d seconds%n", period.getHours(), period.getMinutes(), period.getSeconds());
    }

}
