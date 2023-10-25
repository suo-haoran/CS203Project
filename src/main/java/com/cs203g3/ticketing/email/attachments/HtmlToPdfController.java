package com.cs203g3.ticketing.email.attachments;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/v1/test/htmlToPdf")
@RestController
public class HtmlToPdfController {
    
    @GetMapping
    public ModelAndView test(Model model) {
        return new ModelAndView("ticket-template");
    }
}
