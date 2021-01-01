/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xogameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Abdulrahman Mustafa
 */
public class XOGameServer {

    ServerSocket server;
    private static final int PORT = 34522;

    public XOGameServer() {
        try {
            server = new ServerSocket(PORT);
            while (true) {
                Socket socket = server.accept(); // accepting a new client
                System.out.println("Client recieved");
                new ConnectionHandeller(socket);
            }
        } catch (IOException ex) {
            Logger.getLogger(XOGameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new XOGameServer();
    }

}
