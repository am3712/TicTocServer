/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.io.Serializable;

/**
 *
 * @author Mohamed Essam-Eldin
 */
public class PlayerInfo extends PlayerView implements Serializable {

    private static final long serialVersionUID = 1L;

    private String password;
    private int num_of_games;
    private int num_of_wins;

    public PlayerInfo() {

    }

    public PlayerInfo(String username, String password, int num_of_games, int num_of_wins, boolean active_status) {

        super(username, active_status);
        this.password = password;
        this.num_of_games = num_of_games;
        this.num_of_wins = num_of_wins;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNum_of_games() {
        return num_of_games;
    }

    public void setNum_of_games(int num_of_games) {
        this.num_of_games = num_of_games;
    }

    public int getNum_of_wins() {
        return num_of_wins;
    }

    public void setNum_of_wins(int num_of_wins) {
        this.num_of_wins = num_of_wins;
    }

    @Override
    public String toString() {
        return "UserName: " + username
                + " ,Password: " + password
                + " ,Num Of Games:" + num_of_games
                + " ,Num Of Wins: " + num_of_wins
                + " ,Active Status: " + active_status;
    }

}
