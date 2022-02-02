package com.formreleaf.scheduler.service;

import com.formreleaf.domain.ReportShareSchedule;
import com.formreleaf.domain.enums.ReportFormat;
import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

/**
 * @author Bazlur Rahman Rokon
 * @since 10/7/15.
 */

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.phone}")
    private String phone;

    @Value("${mail.email}")
    private String supportedEmail;

    @Async
    public void sendReport(ReportShareSchedule reportShareSchedule, byte[] contents) {
        String fileName = reportShareSchedule.getReport().getName();
        String mimeType = "";

        ReportFormat reportFormat = reportShareSchedule.getReportFormat();

        if (reportFormat == ReportFormat.CSV) {
            fileName += ".csv";
            mimeType = "text/csv";
        } else if (reportFormat == ReportFormat.PDF) {
            fileName += ".pdf";
            mimeType = "application/pdf";
        }
        String subject = "FormReleaf: " + reportShareSchedule.getReportSharingFrequency().getLabel()
                + " " + reportShareSchedule.getReport().getName() + " Report";

        Context context = new Context(Locale.US);
        context.setVariable("mailTitle", subject);
        context.setVariable("supportPhone", phone);
        context.setVariable("supportEmail", supportedEmail);
        context.setVariable("imageResourceName", "logo"); // so that we can reference it from HTML
        String content = templateEngine.process("sendReport", context);

        for (String email : reportShareSchedule.getRecipientEmails()) {
            sendEmail(email, subject, content, true, true, contents, fileName, mimeType);
        }
    }

    @Async
    private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml, byte[] contents, String attachmentName, String mimeType) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);

            ByteArrayDataSource dataSource = new ByteArrayDataSource(contents, mimeType);

            message.addAttachment(attachmentName, dataSource);
            // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
            URL loadedResource = this.getClass().getClassLoader().getResource("static/img/logo.png");

            final InputStreamSource imageSource = new ByteArrayResource(getByteArray(loadedResource));
            message.addInline("logo", imageSource, "image/png");

            javaMailSender.send(mimeMessage);
            LOGGER.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            LOGGER.error("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage(), e);
        }
    }

    private byte[] getByteArray(URL url) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = url.openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

        } catch (IOException e) {
            LOGGER.error("unable to convert url to byte array ", e);

            return null;
        }

        return outputStream.toByteArray();
    }
}
