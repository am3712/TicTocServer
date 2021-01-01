package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mysql.jdbc.Driver;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import utility.GameRecord;
import utility.PlayerInfo;
import utility.PlayerView;

/**
 *
 * @author Mohamed Essam-Eldin
 */
public class DB {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/java_project?zeroDateTimeBehavior=convertToNull";
    private static final String DATABASE_NAME = "root";
    private static final String DATABASE_PASSWORD = "";

    public static Connection con;

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void checkConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(DATABASE_URL, DATABASE_NAME, DATABASE_PASSWORD);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    Author : Mohamed Essam
     */
    public static boolean insert(PlayerInfo player) {
        checkConnection();

        if (!isFound(player.getUsername())) {

            String q = "INSERT INTO PLAYER VALUES (?, ?, ?, ?, ?)";
            System.out.println(player.getNum_of_wins());
            System.out.println(player.isActive_status());
            PreparedStatement ps;
            try {
                ps = con.prepareStatement(q);
                ps.setString(1, player.getUsername());
                ps.setString(2, player.getPassword());
                ps.setBoolean(3, player.isActive_status());
                ps.setInt(4, player.getNum_of_games());
                ps.setInt(5, player.getNum_of_wins());
                ps.executeUpdate();
                Update(player.getUsername(), true);
                ps.close();
                con.close();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return false;
    }

    private static boolean isFound(String username) {
        boolean found = false;
        checkConnection();

        try {
            try (Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                ResultSet rs = statement.executeQuery("SELECT * FROM PLAYER WHERE USERNAME = '" + username + "'");

                if (rs.next()) {
                    found = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return found;
    }

    public static boolean checklogin(String username, String password) {
        try {
            checkConnection();
            Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery("SELECT * FROM PLAYER WHERE USERNAME = '" + username + "'");
            if (rs.next()) {
                if (rs.getString(2).equals(password)) {
                    Update(username, true);
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /*
    Author : MARIAM
     */
    public static boolean LogOut(String userName) {
        return Update(userName, false);
    }

    public static boolean Update(String userName, boolean active) {

        try {
            checkConnection();
            String query = "UPDATE PLAYER SET ACTIVE_STATUS=? WHERE USERNAME=? ";
            try (PreparedStatement pre = con.prepareStatement(query)) {
                pre.setBoolean(1, active);
                pre.setString(2, userName);
                pre.executeUpdate();
            }
            con.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public synchronized static int addRecord(Vector<String> moves, String winnerUserName, String loserUserName) {
        try {
            checkConnection();
            PreparedStatement pst = con.prepareStatement("insert into Record(date,winnerUserName,loserUserName) values (?,?,?);",
                    Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, getDate());
            pst.setString(2, winnerUserName);

            System.out.println("Winner User Name: " + winnerUserName);
            System.out.println("Loser user name: " + loserUserName);
            pst.setString(3, loserUserName);

            int affectedRows = pst.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            int record_id;
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    record_id = generatedKeys.getInt(1);
                    insertMoves(record_id, moves);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                pst.close();
                con.close();
                return record_id;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static boolean connectRecordIdToPlayers(int record_id, String playerUserName) {
        try {
            checkConnection();
            try (PreparedStatement pst = con.prepareStatement("insert into player_records values (?,?);")) {
                pst.setInt(2, record_id);
                pst.setString(1, playerUserName);

                pst.execute();
            }
            con.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        Date date = new Date();
        return formatter.format(date);
    }

    private static void insertMoves(int record_id, Vector<String> moves) {

        try {
            checkConnection();
            // 1. Turn off auto-commit
            con.setAutoCommit(false);
            // 3.Adding the calling statement batches
            try ( //2. Creating an instance of prepared Statement
                    PreparedStatement pst = con.prepareStatement("INSERT INTO record_moves VALUES ( ? , ? );")) {
                // 3.Adding the calling statement batches
                for (String move : moves) {
                    pst.setInt(1, record_id);
                    pst.setString(2, move);
                    pst.addBatch();
                }
                // 4.Submit the batch for execution
                pst.executeBatch();
                // 5. Commit
                con.commit();
            }
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<PlayerView> getOnlinePlayers() {
        ArrayList<PlayerView> onlinePlayers = new ArrayList<>();
        try {
            checkConnection();

            Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery("SELECT userName, active_status FROM PLAYER;");
            while (rs.next()) {
                onlinePlayers.add(new PlayerView(rs.getString(1), rs.getBoolean(2)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return onlinePlayers;
    }

    private static Vector<Integer> getRecordIds(String username) {
        Vector<Integer> v = new Vector<>();
        try {
            checkConnection();
            Statement statement = con.createStatement();
            String q = "SELECT RECORD_ID FROM PLAYER_RECORDS WHERE PLAYER_USERNAME = '" + username + "'";
            ResultSet re = statement.executeQuery(q);

            while (re.next()) {
                v.add(re.getInt("record_id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return v;

    }

    public static Vector<GameRecord> getGameRecords(String username) {
        Vector<GameRecord> v = new Vector<>();
        Vector<Integer> recordIDs = getRecordIds(username);

        try {
            checkConnection();
            Statement s = con.createStatement();
            for (int i = 0; i < recordIDs.size(); i++) {
                String q = "SELECT DATE, WINNERUSERNAME, LOSERUSERNAME FROM  RECORD WHERE ID = '" + recordIDs.get(i) + "'";
                ResultSet rs = s.executeQuery(q);

                while (rs.next()) {
                    GameRecord gr = new GameRecord(rs.getString("DATE"), rs.getString("WINNERUSERNAME"), rs.getString("LOSERUSERNAME"));

                    v.add(gr);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return v;
    }

    public static Vector<String> getMoves(int recordId) {
        Vector<String> moves = new Vector<>();
        try {
            checkConnection();
            String q = "SELECT * FROM RECORD_MOVES WHERE RECORD_ID = " + recordId + "";
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(q);

            while (rs.next()) {
                moves.add(rs.getString("move"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return moves;
    }

}
