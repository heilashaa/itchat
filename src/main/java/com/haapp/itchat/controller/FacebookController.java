package com.haapp.itchat.controller;

import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FacebookController {

    private FacebookConnectionFactory factory = new FacebookConnectionFactory("526762058188362", "b01c60f2ef80147051454f4ceadcf1b6");

//    private FacebookServiceProvider serviceProvider = new FacebookServiceProvider(
//            "526762058188362",
//            "b01c60f2ef80147051454f4ceadcf1b6",
//            null);

//    @RequestMapping("/")
//    public ModelAndView firstPage() {
//        return new ModelAndView("welcome");
//    }

    @GetMapping(value = "/login/facebook")
    public String producer() {
        OAuth2Operations operations = factory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("http://localhost:8080/forward/facebook");
//        params.setScope("email,public_profile");
        String url = operations.buildAuthenticateUrl(params);
        return "redirect:" + url;
    }

    @RequestMapping(value = "/forward/facebook")
    public String prodducer(@RequestParam("code") String authorizationCode, RedirectAttributes redirectAttributes) {

        OAuth2Operations operations = factory.getOAuthOperations();
        AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, "http://localhost:8080/forward/facebook",
                null);

//        Facebook facebook = new FacebookTemplate(accessToken.getAccessToken());
//        User userProfile = facebook.userOperations().getUserProfile();


        Connection<Facebook> connection = factory.createConnection(accessToken);
        Facebook facebook = connection.getApi();
        String[] fields = { "id", "first_name", "last_name" };
        User userProfile = facebook.fetchObject("me", User.class, fields);




//        ModelAndView model = new ModelAndView("details");
//        model.addObject("userName", userProfile.getFirstName() + " " + userProfile.getLastName());

//        return model;

        redirectAttributes.addFlashAttribute("userName", userProfile.getFirstName() + "_" + userProfile.getLastName());

        return "redirect:http://localhost:8080";
    }

}
