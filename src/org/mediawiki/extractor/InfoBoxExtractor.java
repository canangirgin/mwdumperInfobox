package org.mediawiki.extractor;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 02.05.2013
 * Time: 03:20
 * To change this template use File | Settings | File Templates.
 */
public class InfoBoxExtractor {
   public static Connection baglanti=null;
    public static void main(String[] args)  {
        System.out.println("Bağlantı sağlanıyor...");
        try {
            try {
                ConnectDB();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } catch (SQLException e) {
            //TODO Canan Burayı yönet
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("Doğum yeri extract ediliyor...");
        BirthPlaceExtractor bExtractor= new BirthPlaceExtractor();
        try {
            bExtractor.Extract();
        } catch (SQLException e) {
            //TODO Canan Butayı yönet
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    private static void ConnectDB() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
       Class.forName("com.mysql.jdbc.Driver").newInstance();
        baglanti= DriverManager.getConnection("jdbc:mysql://localhost/wikidb", "root", "root");
    }


}
