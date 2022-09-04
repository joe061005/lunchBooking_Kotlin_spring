package com.project.lunchBooking.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMailMessage
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailSenderService(
    @Autowired private val mailSender: JavaMailSender
) {

    fun sendEmail(toEmail: String, subject: String, URL: String, username: String){
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "utf-8")
        val htmlMsg = "Dear ${username},<br/><br/>" +
                "Please activate your account by clicking the URL <a href=\"${URL}\">${URL}</a> within 10 minutes.<br/><br/>" +
                "Best Regards,<br/>" +
                "Restaurant Booking Platform"

        message.setContent(htmlMsg, "text/html")
        helper.setFrom("HKrestbooking@gmail.com")
        helper.setTo(toEmail)
        helper.setSubject(subject)
        mailSender.send(message)
    }

}