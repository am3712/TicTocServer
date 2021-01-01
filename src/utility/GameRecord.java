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
public class GameRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private String date;
    private String winner;
    private String loser;

    public GameRecord(int id, String date, String winner, String loser) {
        this.id = id;
        this.date = date;
        this.winner = winner;
        this.loser = loser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

    @Override
    public String toString() {
        return "date: " + date + " winner: " + winner + " loser: " + loser;
    }

}
