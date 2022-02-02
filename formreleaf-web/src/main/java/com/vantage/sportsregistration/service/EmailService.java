package com.vantage.sportsregistration.service;

import com.formreleaf.domain.Registration;
import com.formreleaf.domain.ReportShareSchedule;
import com.formreleaf.domain.User;
import com.formreleaf.domain.enums.ReportFormat;
import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/22/15.
 */
@Service
public class EmailService {
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * System default email address that sends the e-mails.
     */
    private String from;
    private String phone;
    private String email;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("mail.from");
        this.phone = env.getProperty("mail.phone");
        this.email = env.getProperty("mail.email");
    }

    @Async
    private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}'", isMultipart, isHtml, to, subject);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);

            // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
            URL loadedResource = this.getClass().getClassLoader().getResource("static/img/logo.png");

            final InputStreamSource imageSource = new ByteArrayResource(getByteArray(loadedResource));
            message.addInline("logo", imageSource, "image/png");

            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.error("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage(), e);
        }
    }

    @Async
    public void sendRegistrationNotification(Registration registration) {

        String to = registration.getRegistrant().getEmail();

        String subject = "Confirmation of Registration";
        String parentUserName = registration.getRegistrant().getFullName();
        String organizationName = registration.getProgram().getOrganization().getName();
        String participantName = registration.getRegistrantName();
        String programName = registration.getProgram().getName();
        String sectionName = registration.getSectionNames();

        Optional<String> organizationEmail = registration.getProgram().getOrganization().getUsers().stream().map(User::getEmail).findFirst();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        String dateStr = sdf.format(registration.getRegistrationDate());

        String fax = registration.getProgram().getOrganization().getFax();

        Context context = new Context(Locale.US);
        context.setVariable("mailTitle", subject);
        context.setVariable("dateOfRegistration", dateStr);
        context.setVariable("participantName", participantName);
        context.setVariable("parentUserName", parentUserName);
        context.setVariable("programName", programName);
        context.setVariable("sectionName", sectionName);
        context.setVariable("organizationName", organizationName);
        context.setVariable("fax", fax);
        context.setVariable("supportPhone", phone);
        context.setVariable("supportEmail", email);
        context.setVariable("imageResourceName", "logo"); // so that we can reference it from HTML

        String content = templateEngine.process("sendRegistrationNotification", context);
        sendEmail(to, subject, content, true, true);
    }

    @Async
    public void sendAdminNotification(Registration registration) {

        registration.getProgram().getOrganization().getUsers().forEach(user -> Optional.ofNullable(user.getEmail())
                .ifPresent(to -> {
                    String subject = "New Registration Submission";
                    String adminUser = user.getFirstName();
                    String registrantName = registration.getRegistrant().getFirstName();
                    String participantName = registration.getRegistrantName();
                    String programName = registration.getProgram().getName();
                    String sectionName = registration.getSectionNames();

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
                    String dateStr = sdf.format(registration.getRegistrationDate());
                    String parentEmail = registration.getRegistrant().getEmail();

                    Context context = new Context(Locale.US);
                    context.setVariable("adminUser", adminUser);
                    context.setVariable("registrantName", registrantName);
                    context.setVariable("participantName", participantName);
                    context.setVariable("programName", programName);
                    context.setVariable("sectionName", sectionName);
                    context.setVariable("dateStr", dateStr);
                    context.setVariable("parentEmail", parentEmail);
                    context.setVariable("mailTitle", subject);
                    context.setVariable("imageResourceName", "logo"); // so that we can reference it from HTML
                    String content = templateEngine.process("sendAdminNotification", context);

                    sendEmail(to, subject, content, true, true);
                }));
    }

    @Async
    public void sendResetPasswordNotification(User user, String appUrl, String token) {

        String confirmationUrl = appUrl + "/change-password?token=" + token;
        String message = "Please go to the following page and choose a new password:";
        String text = message + " \r\n" + confirmationUrl;

        String subject = "Formreleaf Reset Password";
        String userName = user.getFirstName();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
        String dateStr = sdf.format(new Date());
        String email = user.getEmail();

        Context context = new Context(Locale.US);
        context.setVariable("userName", userName);
        context.setVariable("dateStr", dateStr);
        context.setVariable("email", email);
        context.setVariable("mailTitle", subject);
        context.setVariable("imageResourceName", "logo"); // so that we can reference it from HTML
        context.setVariable("text", text);
        String content = templateEngine.process("sendPasswordResetNotification", context);

        sendEmail(email, subject, content, true, true);
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
            log.error("unable to convert url to byte array ", e);
            return null;
        }

        return outputStream.toByteArray();
    }

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
        context.setVariable("supportEmail", email);
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
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.error("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage(), e);
        }
    }
}
