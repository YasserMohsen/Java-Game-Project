/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 * @author kazafy
 */
public class Request implements Serializable{
    private static final long serialVersionUID = 1L;
    private static String clientID;
    private int type;
    private Object object;

    public int getType() {
        return type;
    }

    public void setClientID(String clientID) {
        Request.clientID = clientID;
    }
    
    public String getClientID() {
        return clientID;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}