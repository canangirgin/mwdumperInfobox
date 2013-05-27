package org.mediawiki.extractor;

import org.mediawiki.importer.SqlServerStream;
import org.mediawiki.importer.SqlWriter;
import org.mediawiki.importer.SqlWriter15;

import java.io.IOException;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 02.05.2013
 * Time: 03:20
 * To change this template use File | Settings | File Templates.
 */
public class InfoBoxExtractor {
    private static ResultSet textTable = null;
    public static Blob textBlob = null;
    public static Connection baglanti = null;
    public static SqlWriter15 sqlWriter;

    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        System.out.println("Bağlantı sağlanıyor...");
        Template template = null;


        ConnectDB();
        sqlWriter = openWriter();
        while (getTextTable().next()) {
            try {
                template = new Template();
                textBlob = getTextTable().getBlob("old_text");
                byte[] bdata = textBlob.getBytes(1, (int) textBlob.length());
                template.text = new String(bdata);
                template.name = getTextTable().getString("p_ad");
                template.revisionId = getTextTable().getInt("rev_id");
                BirthPlaceExtractor bExtractor = new BirthPlaceExtractor();
                template.Propetys.put("p_dogum_yer", bExtractor.Extract(template));
                System.out.println(template.name + " doğum yer = " + template.Propetys.get("p_dogum_yer"));
                sqlWriter.writeTemplate(template);

            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e4) {
                e4.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e5) {
                e5.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("HATA!!!" + template.revisionId);
            }
        }
        try {
            sqlWriter.writeEndWiki();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("HATA!!! son commit" );
        }
    }

    public static ResultSet getTextTable() throws SQLException {
        if (textTable == null) {
            Statement statement = InfoBoxExtractor.baglanti.createStatement();
            // TODO burayı incele
            // Deneme amaçlı şimdilik 10 tane ile çalış
            // Şimdilik sadece infobox olanlara bak
            textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id ");
           // textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id LIMIT 0,10");
            //textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id and rev_id='9086735'");
        }
        return textTable;
    }

    private static void ConnectDB() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        baglanti = DriverManager.getConnection("jdbc:mysql://localhost/wikidb", "root", "root");
    }

    protected void writeDB(Template template) {
        try {
            sqlWriter.writeTemplate(template);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("HATA!!!" + template.revisionId);
        }
    }

    private static SqlWriter15 openWriter() {
        SqlServerStream sqlStream = new SqlServerStream(InfoBoxExtractor.baglanti);
        return new SqlWriter15(new SqlWriter.MySQLTraits(), sqlStream, "");
    }


}
