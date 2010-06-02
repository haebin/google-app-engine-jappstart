/*
 *  Copyright (C) 2010 Taylor Leese (tleese22@gmail.com)
 *
 *  This file is part of jappstart.
 *
 *  jappstart is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  jappstart is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jappstart.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jappstart.service.mail;

import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.jappstart.model.auth.UserAccount;

/**
 * The mail service.
 */
@Service
public class MailService {

    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MailService.class);

    /**
     * The error message.
     */
    private static final String ERROR_MSG =
        "Error sending activation e-mail.";

    /**
     * The from address.
     */
    private String fromAddress;

    /**
     * The hostname.
     */
    private String hostname;

    /**
     * The message source.
     */
    private MessageSource messageSource;

    /**
     * Gets the from address.
     *
     * @return the from address
     */
    public final String getFromAddress() {
        return fromAddress;
    }

    /**
     * Sets the from address.
     *
     * @param fromAddress the from address
     */
    public final void setFromAddress(final String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * Gets the hostname.
     *
     * @return the hostname
     */
    public final String getHostname() {
        return hostname;
    }

    /**
     * Sets the hostname.
     *
     * @param hostname the hostname
     */
    public final void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     * Gets the message source.
     *
     * @return the message source
     */
    public final MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * Sets the message source.
     *
     * @param messageSource the message source
     */
    public final void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Sends the activation e-mail to the given user.
     *
     * @param user the user
     */
    public final void sendActivationEmail(final UserAccount user) {
        final Properties props = new Properties();
        final Session session = Session.getDefaultInstance(props, null);
        final Message message = new MimeMessage(session);
        final Multipart multipart = new MimeMultipart();
        final MimeBodyPart htmlPart = new MimeBodyPart();
        final MimeBodyPart textPart = new MimeBodyPart();
        final Locale locale = LocaleContextHolder.getLocale();

        try {
            message.setFrom(new InternetAddress(getFromAddress()));

            message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(user.getUsername()));

            message.setSubject(messageSource.getMessage("mail.subject", null,
                locale));

            textPart.setContent(messageSource.getMessage("mail.body.txt",
                new Object[] {getHostname(), user.getActivationKey()}, locale),
                "text/plain");

            htmlPart.setContent(messageSource.getMessage("mail.body.html",
                new Object[] {getHostname(), user.getActivationKey()}, locale),
                "text/html");

            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.warn(ERROR_MSG, e);
        }
    }

}
