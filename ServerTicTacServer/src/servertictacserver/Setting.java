/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;

/**
 *
 * @author kazafy
 */
public class Setting {
    public static final int REG = 1001;
    public static final int REG_OK = 2001;
    public static final int REG_NO = 2002;
    public static final int LOGIN = 1002;
    public static final int FBLOG = 1003;
    public static final int LOGIN_OK = 2003;
    public static final int LOGIN_NO = 2004;   
    public static final int ADD_PLAYER_TO_AVAILABLE_LIST = 2005;
    
    public static final int AVAILABLE = 3001;
    public static final int BUSY = 3002;
    public static final int OFFLINE = 3003;
    public static final int OUT = 3000;
    
    public static final int SELECT_PLAYER_FROM_AVAILABLE_LIST = 2006;
    public static final int SEND_INVITATION_FOR_PLAYING = 2007;

    public static final int WINNER = 2011;
    public static final int LOSER = 2009;
    public static final int DRAW = 2017;
    public static final int MOVE = 2016;
    public static final int MOVEBACK = 2010;

    public static final int ACCEPT_INVITATION = 2008;
    public static final int REFUSE_INVITATION = 2012;
    public static final int UPDATE_PLAYER_IN_PLAYER_LIST = 2013;
    public static final int UPDATE_2PLAYER_IN_PLAYER_LIST = 2014;

    public static final int MESSAGE = 2020;
    public static final int RECIEVE_MESSAGE = 2021;
    public static final int UPDATE_NEWS = 2000;
    
    public static final String DEFAULT_IMAGE = "male.jpg";
    public static final int UPDATEPLAYER = 2015;
    public static final int POINTS = 3;    
}
