package com.haapp.itchat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class MainController {

    @RequestMapping("/")
    public String main (HttpServletRequest request, Model model){
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if(flashMap !=null){
            String name = (String) flashMap.get("userName");
            model.addAttribute("name" , name);
        }else{
            model.addAttribute("name" , "");
        }

        return "main";
    }
}
