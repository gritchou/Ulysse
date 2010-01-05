package org.qualipso.factory.jabuti.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JabutiConnection {
    private static java.sql.Connection CONN;
       
    public JabutiConnection() {
    }
    
    public static Connection getConnection(Properties props) {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
    	
    	if(CONN == null) {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
                CONN = DriverManager.getConnection(url, user, password);
                return CONN;
                
            } 
            catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        else
            return CONN;
    }
    
}
