package model;

import clienttictactoe.Client;
import clienttictactoe.ClientTicTacToe;
import clienttictactoe.Setting;
import clienttictactoe.WebViewController;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Worker;

/**
 *
 * @author terminator
 */
public class FacebookApi {

    private static String domain = "http://dolnii.com/requires/index.html";
    private static String appId = "385244185170219";
    private static String accessToken;
    private static String authUrl = "https://graph.Facebook.com/oauth/authorize?type=user_agent&client_id=" + appId + "&redirect_uri=" + domain + "&scope="
            + "                        user_photos,email,user_birthday,publish_actions"
            + "                       ,user_about_me,user_birthday,publish_actions,user_birthday"
            + "                       ,user_games_activity,user_likes,user_posts";
    private FacebookClient facebookClient;
    private com.restfb.types.User fbUser;
    private User user;

    public FacebookApi() {
        try {
            ClientTicTacToe.replaceSceneContent("webView.fxml", "Chat Menu");
            WebViewController.engine.load(authUrl);
            WebViewController.engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (Worker.State.SUCCEEDED.equals(newValue)) {
                    System.out.println(WebViewController.engine.getLocation());
                    if (!WebViewController.engine.getLocation().contains("facebook.com")) {
                        try {
                            System.out.println("url " + WebViewController.engine.getLocation());
                            String url = WebViewController.engine.getLocation();
                            url = url.replaceAll(".*#access_token=(.+)&.*", "$1");
                            System.out.println("accesstok " + url);
                            FacebookApi.setAccessToken(url);
                            this.facebookClient = new DefaultFacebookClient(accessToken);
                            this.fbUser = this.getClientFacebookInfo("id,name,email,picture");
                            user = new User(Long.parseLong(fbUser.getId()), fbUser.getName(), fbUser.getEmail(), fbUser.getPicture().getUrl());
                            Client c = new Client(user, Setting.FBLOG);
                            System.out.println("" + fbUser.getId() + "/n" + fbUser.getEmail() + "/n" + fbUser.getName() + "/n" + fbUser.getPicture().getUrl());
                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.main_XML, "Chat Menu");
                        } catch (Exception ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(FacebookApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String publishToTimeLine(String post) {
        FacebookType response = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", post));
        return response.getId();
    }

    ///////////////separate between every facebook field with , ////////////////////////////////////////////////
    public com.restfb.types.User getClientFacebookInfo(String fields) {
        com.restfb.types.User user = facebookClient.fetchObject("me", com.restfb.types.User.class, Parameter.with("fields", fields));
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

    public static void shareMSG(String quote, String link, String linkName, String linkDesc, String linkCaption, String display, String image) {
        try {
            ClientTicTacToe.replaceSceneContent("webView.fxml", "Chat Menu");
            WebViewController.engine.load("https://www.facebook.com/dialog/feed?app_id=385244185170219"
                    + "&display="+display
                    + "&caption="+linkCaption
                    + "&link="+link
                    + "&quote="+ quote
                    + "&name="+linkName
                    + "&redirect_uri=http://dolnii.com/requires/index.html"
                    + "&picture="+image
                    + "&description="+linkDesc
            );
            WebViewController.engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (Worker.State.SUCCEEDED.equals(newValue)) {

                    if (WebViewController.engine.getLocation().matches("http://dolnii.com/requires/index.html(.*)")) {
                        try {
                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.main_XML, "Chat Menu");
                        } catch (Exception ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(FacebookApi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
