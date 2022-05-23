package com.netcracker.controller;

import com.netcracker.file.FilePerson;
import com.netcracker.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;


@Controller
public class PersonController {

    @GetMapping(value = {"/", "/index"})
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return "/index";
    }

    @GetMapping("/person")
    public String personForm(Model model){
        model.addAttribute("person",new Person());
        return "person";
    }

    @GetMapping("/search")
    public String userSearchForm(Model model){
        model.addAttribute("person",new Person());
        return "search";
    }

    @GetMapping("/true-result")
    public String trueResult(@ModelAttribute Person person) {
        System.out.println(person.toString());
        return "true-result";
    }

    @PostMapping("/person")
    public String personSubmit(@ModelAttribute Person person) {
            FilePerson.fileWrite(person);
            return "resultPerson";

    }
    @PostMapping("/search")
    public String userSearchSubmit(@ModelAttribute Person person, Model model, HttpServletRequest request, HttpServletResponse response) {
        Person tmp = FilePerson.fileCheckPerson(person);
       if(tmp==null){
        return "false-resylt";
       } else {
           Date date = new Date();
           person.personSet(tmp);
           model.addAttribute("time",date.toString() );
           model.addAttribute("agent", request.getHeader("User-Agent"));
           Cookie cookie = new Cookie("mail", person.getEmail());
           cookie.setMaxAge(1000);
           cookie.setSecure(true);
           response.addCookie(cookie);//добавляем Cookie в запрос
           return "true-result";
       }
    }
    @Autowired
    public JavaMailSender emailSender;

    @GetMapping("/send-email")
    public String mail (@CookieValue("mail") String cook) {
        System.out.println(cook);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(cook);
        message.setSubject("Test Simple Email");
        message.setText("Hello, Im testing Simple Email");
        this.emailSender.send(message);
        return "send-email";
    }

}
