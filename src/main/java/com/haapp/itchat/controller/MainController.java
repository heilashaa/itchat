package com.haapp.itchat.controller;

import com.haapp.itchat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private ChatMessageService chatMessageService;

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


    @GetMapping(value = "/temp")
    public String listProvider(Model model){

        model.addAttribute("messages", this.chatMessageService.getLastMessages());
        return "temp";
    }


}
