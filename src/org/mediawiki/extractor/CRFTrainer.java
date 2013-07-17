package org.mediawiki.extractor;

import org.mediawiki.importer.SqlServerStream;
import org.mediawiki.importer.SqlWriter;
import org.mediawiki.importer.SqlWriter15;

import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 17.07.2013
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */

//CRF train datasını oluşturuyor.
public class CRFTrainer {
    private static ResultSet textTable = null;
    public static Blob textBlob = null;
    public static Connection conn = null;
    public static SqlWriter15 sqlWriter;
    public static  Infobox infoBox;

    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        System.out.println("Bağlantı sağlanıyor...");
        String revText;

        ConnectDB();
        sqlWriter = openWriter();
        while (getTextTable().next()) {
            try {
                infoBox= new Infobox();
                textBlob = getTextTable().getBlob("old_text");
                byte[] bdata = textBlob.getBytes(1, (int) textBlob.length());
                revText = new String(bdata);
                infoBox.rev_id = getTextTable().getInt("rev_id");
                setInfoProperties();
                writeFile(defineTrainString(revText));

            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("HATA!!!" + infoBox.rev_id);
            }
        }
    }

    private static void setInfoProperties() throws SQLException {
        infoBox.Propetys.put("p_dogum_tar",getTextTable().getString("p_dogum_tar"));
        infoBox.Propetys.put("p_dogum_yer",getTextTable().getString("p_dogum_yer"));
        infoBox.Propetys.put("p_olum_tar",getTextTable().getString("p_olum_tar"));
        infoBox.Propetys.put("p_olum_yer",getTextTable().getString("p_olum_yer"));
    }

    private static String defineTrainString(String revText) {
        return null;
    }

    public static ResultSet getTextTable() throws SQLException {
        if (textTable == null) {
            Statement statement = conn.createStatement();
            // Deneme amaçlı şimdilik 10 tane ile çalış
            // Şimdilik sadece infobox olanlara bak
            textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id ");
            // textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id LIMIT 0,10");
            //textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id and rev_id='9086735'");
        }
        return textTable;
    }

    private static void ConnectDB() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://localhost/wikidb", "root", "root");
    }

    protected static void writeFile(String trainTxt) {
     //TODO burada dosyaya yeni satırlar eklenecek.
    }

    private static SqlWriter15 openWriter() {
        SqlServerStream sqlStream = new SqlServerStream(conn);
        return new SqlWriter15(new SqlWriter.MySQLTraits(), sqlStream, "");
    }

}
