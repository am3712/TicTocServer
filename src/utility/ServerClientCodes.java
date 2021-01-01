/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Abdulrahman Mustafa
 */
public interface ServerClientCodes {

    int LOGIN_CODE = 1;
    int SIGN_UP_CODE = 2;
    int LOGOUT_CODE = 3;
    int CLIENT_CLOSE = 4;

    //playing
    int REQUEST_PLAYERS_DATA = 5;
    int REQUEST_PLAYING = 6;
    int RESPONSE_PLAYING = 7;
    int ALL_PLAYERS_CODE = 8;

    int GAME_RECORDS_CODE = 9;

    int PLAYING_RESULT = 10;
    int MOVE_CODE = 11;
    int ACCEPTING_CODE = 12;
    int REJECTION_CODE = 13;
    int ASKING_GAME_RECORD = 14;
    
    int GET_MOVES = 15;

    int SAVE_RECORD = 16;
}
