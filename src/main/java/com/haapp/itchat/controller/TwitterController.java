package com.haapp.itchat.controller;

import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TwitterController {

    private TwitterServiceProvider serviceProvider = new TwitterServiceProvider("uM2mWowm8xq8o6gp6buywbb51", "GSgplBaE8JJ3N0WkbZHZwRnNvvibTrWJw89AqzeFqBC3BvTgfp");

    private OAuthToken requestToken;

//    private TwitterConnectionFactory factory = new TwitterConnectionFactory("uM2mWowm8xq8o6gp6buywbb51", "GSgplBaE8JJ3N0WkbZHZwRnNvvibTrWJw89AqzeFqBC3BvTgfp");

//    @RequestMapping("/")
//    public ModelAndView firstPage() {
//        return new ModelAndView("welcome");
//    }

    @GetMapping(value = "/login/twitter")
    public String producer() {
        OAuth1Operations oauthOperations = serviceProvider.getOAuthOperations();
        requestToken = oauthOperations.fetchRequestToken( "http://localhost:8080/forward/twitter", null );
        String authorUrl = oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE );
        return "redirect:" + authorUrl;
    }

    @GetMapping(value = "/forward/twitter")
    public String prodducer(@RequestParam("oauth_token") String oauthToken, @RequestParam("oauth_verifier") String oauthVerifier, RedirectAttributes redirectAttributes) {
        OAuth1Operations operations = serviceProvider.getOAuthOperations();
        OAuthToken accessToken = operations.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, oauthVerifier), null);
        Twitter twitter = serviceProvider.getApi(accessToken.getValue(), accessToken.getSecret());
        TwitterProfile profile = twitter.userOperations().getUserProfile();
        redirectAttributes.addFlashAttribute("userName", profile.getName());
        return "redirect:http://localhost:8080";
    }
}
