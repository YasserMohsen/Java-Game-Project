/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author terminator
 */
public class FacebookApi {
    private static  String domain = "http://dolnii.com/requires/index.html";
    private static  String appId = "385244185170219" ;
    private static  String accessToken;
    private static  String authUrl = "https://graph.Facebook.com/oauth/authorize?type=user_agent&client_id="+appId+"&redirect_uri="+domain+"&scope=email,user_birthday,publish_actions";
    private static WebDriver driver;
    private FacebookClient facebookClient;
    
    
    public FacebookApi() {

        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
        driver.get(authUrl);
        while (true) {
            if (!driver.getCurrentUrl().contains("facebook.com")) {
                String url = driver.getCurrentUrl();
                url = url.replaceAll(".*#access_token=(.+)&.*", "$1");
                FacebookApi.setAccessToken(url);
                driver.close();
                this.facebookClient = new DefaultFacebookClient(accessToken);
                break;

            }
        } 
    }
    
    public String publishToTimeLine(String post){
        FacebookType response =  facebookClient.publish("me/feed",FacebookType.class, Parameter.with("message", post));
        return response.getId();
    }
    
    ///////////////separate between every facebook field with , ////////////////////////////////////////////////
    public com.restfb.types.User getClientFacebookInfo(String fields){
        com.restfb.types.User user = facebookClient.fetchObject("me",com.restfb.types.User.class,Parameter.with("fields",fields));
        return user;
    }

    public static void setDomain(String domain) {
        FacebookApi.domain = domain;
    }

    public static void setAppId(String appId) {
        FacebookApi.appId = appId;
    }

    public static void setAuthUrl(String authUrl) {
        FacebookApi.authUrl = authUrl;
    }

    public void setFacebookClient(FacebookClient facebookClient) {
        this.facebookClient = facebookClient;
    }
    
    public static void setAccessToken(String accessToken) {
        FacebookApi.accessToken = accessToken;
    }

    public static void setDriver(WebDriver driver) {
        FacebookApi.driver = driver;
    }

    public FacebookClient getFacebookClient() {
        return facebookClient;
    }
    
    public static String getDomain() {
        return domain;
    }

    public static String getAppId() {
        return appId;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static String getAuthUrl() {
        return authUrl;
    }

    public static WebDriver getDriver() {
        return driver;
    }
    
    
}
