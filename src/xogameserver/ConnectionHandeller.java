/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xogameserver;

import dataBase.DB;
import utility.OperationStatus;
import utility.PlayerInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.GameRecord;
import static utility.ServerClientCodes.*;

/**
 *
 * @author Abdulrahman Mustafa
 */
public class ConnectionHandeller extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String userName;
    private String anotherUserName;
    private Socket Socket;
    private int lastRecordId;
    static final Vector<ConnectionHandeller> clientsVector
            = new Vector<ConnectionHandeller>();

    public ConnectionHandeller(Socket clientSocket) {
        try {
            Socket = clientSocket;
            out = new ObjectOutputStream(Socket.getOutputStream());
            in = new ObjectInputStream(Socket.getInputStream());
            clientsVector.add(ConnectionHandeller.this);
            lastRecordId = -1;
            start();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandeller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            int inputLine;
            while ((inputLine = in.readInt()) != -1) {
                if (CLIENT_CLOSE == inputLine) {
                    if (userName != null) {
                        DB.Update(userName, false);
                    }
                    Socket.close();
                    clientsVector.remove(ConnectionHandeller.this);
                    System.out.println("Socket is Closed");
                    break;
                }
                ParsMessage(inputLine);
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ConnectionHandeller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ParsMessage(int code) throws IOException, ClassNotFoundException {

        //make sign up or login
        switch (code) {
            case LOGIN_CODE:
                loginOperation();
                break;
            case SIGN_UP_CODE:
                signUpOperation();
                break;
            case LOGOUT_CODE:
                logOutOperation();
                break;
            case REQUEST_PLAYERS_DATA:
                requestOnlinePlayers();
                break;
            case REQUEST_PLAYING:
                requestPlaying();
                break;
            case ACCEPTING_CODE:
                acceptPlaying();
                break;
            case REJECTION_CODE:
                rejectPlaying();
                break;
            case MOVE_CODE:
                sentMove();
                break;
            case GAME_RECORDS_CODE:
                sendGameRecords();
                break;
            case SAVE_RECORD:
                saveRecord();
                break;
            default:
                break;
        }
    }

    private void loginOperation() throws IOException {
        out.writeInt(LOGIN_CODE);
        String user_Name = in.readUTF();
        if (DB.checklogin(user_Name, in.readUTF())) {
            out.writeObject(new OperationStatus(true, "Login Success"));
            userName = user_Name;
        } else {
            out.writeObject(new OperationStatus(false, "Login Failed, username or Password is wrong !!."));
        }
        out.flush();
    }

    private void signUpOperation() throws IOException, ClassNotFoundException {
        out.writeInt(SIGN_UP_CODE);
        PlayerInfo pi = (PlayerInfo) in.readObject();
        if (DB.insert(pi)) {
            out.writeObject(new OperationStatus(true, "Sign Up Success"));
            userName = pi.getUsername();
        } else {
            out.writeObject(new OperationStatus(false, "UserName Reserved, please choose another one !!."));
        }
        out.flush();
    }

    private void logOutOperation() throws IOException {
        out.writeInt(LOGOUT_CODE);
        if (DB.LogOut(userName)) {
            out.writeObject(new OperationStatus(true, "Logout Success"));
            userName = null;
        } else {
            out.writeObject(new OperationStatus(false, "Logout not Success !!."));
        }
        out.flush();
    }

    private void requestOnlinePlayers() throws IOException {
        out.writeInt(REQUEST_PLAYERS_DATA);
        out.writeObject(DB.getOnlinePlayers());
        out.flush();
    }

    private void requestPlaying() throws IOException {
        System.out.println("Request recieve to server");
        String targetUsrName = in.readUTF();
        System.out.println("targetUsrName: " + targetUsrName);
        ConnectionHandeller anotherPlayer = findPlayer(targetUsrName);
        System.out.println("anotherPlayer name: " + anotherPlayer.userName);
        if (anotherPlayer.anotherUserName == null) {
            anotherPlayer.out.writeInt(RESPONSE_PLAYING);
            anotherPlayer.out.writeUTF(userName);
            anotherPlayer.out.flush();
        }
    }

    private ConnectionHandeller findPlayer(String targetUserName) throws IOException {
        for (ConnectionHandeller ch : clientsVector) {
            if (ch.userName.equals(targetUserName)) {
                return ch;
            }
        }
        return null;
    }

    private void sentMove() throws IOException {
        System.out.println("server : recieve move");

        String move = in.readUTF();
        System.out.println("Move : " + move);

        ConnectionHandeller anotherPlayer = findPlayer(anotherUserName);
        System.out.println("anotherPlayer name: " + anotherPlayer.userName);

        anotherPlayer.out.writeInt(MOVE_CODE);
        anotherPlayer.out.writeUTF(move);
        anotherPlayer.out.flush();
    }

    private void acceptPlaying() throws IOException {
        System.out.println("server : recieve Acceopting Code");

        String targetUsrName = in.readUTF();
        System.out.println("targetUsrName: " + targetUsrName);

        ConnectionHandeller anotherPlayer = findPlayer(targetUsrName);
        System.out.println("anotherPlayer name: " + anotherPlayer.userName);

        //linking players usernames 
        anotherUserName = anotherPlayer.userName;
        anotherPlayer.anotherUserName = userName;

        anotherPlayer.out.writeInt(ACCEPTING_CODE);
        anotherPlayer.out.writeUTF(userName);
        anotherPlayer.out.flush();

    }

    private void rejectPlaying() throws IOException {
        System.out.println("server : recieve Rejection Code");

        String targetUsrName = in.readUTF();
        System.out.println("targetUsrName: " + targetUsrName);

        ConnectionHandeller anotherPlayer = findPlayer(targetUsrName);
        System.out.println("anotherPlayer name: " + anotherPlayer.userName);

        anotherPlayer.out.writeInt(REJECTION_CODE);
        anotherPlayer.out.writeUTF(userName);
        anotherPlayer.out.flush();
    }

    private void sendGameRecords() throws IOException {
        out.writeInt(GAME_RECORDS_CODE);
        String name = in.readUTF();
        Vector<GameRecord> records = DB.getGameRecords(name);
        System.out.println("Records : " + records);
        out.writeObject(records);
        out.flush();
    }

    private void saveRecord() throws IOException, ClassNotFoundException {
        out.writeInt(SAVE_RECORD);
        String winner = in.readUTF();
        String loser = in.readUTF();
        Vector<String> moves = (Vector<String>) in.readObject();
        int id = findPlayer(anotherUserName).lastRecordId;
        if (id != -1) {
            if (DB.connectRecordIdToPlayers(id, userName)) {
                System.out.println("i am " + userName + " only connect record id to me");
                out.writeUTF("Game Record Saved Successfully");
            } else {
                out.writeUTF("Game Record Not Saved Successfully");
            }
        } else {
            lastRecordId = DB.addRecord(moves, winner, loser);
            if (lastRecordId != -1) {
                System.out.println("i am " + userName + " insert new record id to me");
                DB.connectRecordIdToPlayers(lastRecordId, userName);
                out.writeUTF("Game Record Saved Successfully");
            } else {
                out.writeUTF("Game Record Not Saved Successfully");
            }
        }
        out.flush();
    }
}
