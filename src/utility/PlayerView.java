/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.io.Serializable;

/**
 *
 * @author Abdulrahman Mustafa
 */
public class PlayerView implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String username;
    protected boolean active_status;

    public PlayerView() {
    }

    public PlayerView(String username, boolean active_status) {
        this.username = username;
        this.active_status = active_status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive_status() {
        return active_status;
    }

    public void setActive_status(boolean active_status) {
        this.active_status = active_status;
    }

}
