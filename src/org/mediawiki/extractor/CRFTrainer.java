package org.mediawiki.extractor;

import org.mediawiki.importer.KeyValue;
import org.mediawiki.importer.SqlServerStream;
import org.mediawiki.importer.SqlWriter;
import org.mediawiki.importer.SqlWriter15;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

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
    public static Infobox infoBox;
    private static FileWriter fstream;
    private static BufferedWriter out;

    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        System.out.println("Bağlantı sağlanıyor...");
        String revText;

        ConnectDB();
        sqlWriter = openWriter();
        fstream = new FileWriter("out.txt");
        out = new BufferedWriter(fstream);
        while (getTextTable().next()) {
            try {
                infoBox = new Infobox();
                textBlob = getTextTable().getBlob("old_text");
                byte[] bdata = textBlob.getBytes(1, (int) textBlob.length());
                revText = new String(bdata);
                infoBox.rev_id = getTextTable().getInt("rev_id");
                setInfoProperties();
                writeFile(defineTrainString(revText));

            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("HATA!!!" +e1.getStackTrace()+e1.getMessage()+ infoBox.rev_id);
            }
        }
        out.close();
    }

    private static void setInfoProperties() throws SQLException {
        //infoBox.Propetys.put("p_dogum_tar", getTextTable().getString("p_dogum_tar"));
        infoBox.Propetys.put("p_dogum_yer", getTextTable().getString("p_dogum_yer"));
        //infoBox.Propetys.put("p_olum_tar", getTextTable().getString("p_olum_tar"));
        infoBox.Propetys.put("p_olum_yer", getTextTable().getString("p_olum_yer"));
    }

    private static String defineTrainString(String revText) {
        Set entries = infoBox.Propetys.entrySet();
        Iterator entriesIterator = entries.iterator();
        String defined = "";
        while (entriesIterator.hasNext()) {
            Map.Entry mapping = (Map.Entry) entriesIterator.next();
            String key = mapping.getKey().toString();
            String valueList = mapping.getValue().toString();
            if (!valueList.isEmpty()) {
                HashSet<String> values = new HashSet<String>((Arrays.asList(valueList.split(","))));
                for (String value : values) {
                    if (defined.contains(value)) {
                        defined = defined.substring(0, defined.indexOf(value)) + value + " [B_" + key + "] " + defined.substring(defined.indexOf(value) + value.length());
                                         }else if (revText.contains(value)) {
                        //TODO Şimdilik ilk bulduğunu alıyor!!!
                        int startIndex=  revText.indexOf(value)>75?revText.indexOf(value) - 75:0;
                        int stopIndex=revText.length()-revText.indexOf(value)>75?revText.indexOf(value) + value.length()+ 75:revText.length();
                        String temp = revText.substring(startIndex, revText.indexOf(value));
                        defined += System.getProperty("line.separator") + temp.substring(temp.indexOf(" ")>0?temp.indexOf(" "):0) + value + " [B_" + key + "] ";
                        temp = revText.substring(revText.indexOf(value) + value.length(),stopIndex);
                        defined += temp.substring(0, temp.lastIndexOf(" ")>0?temp.lastIndexOf(" "):temp.length());

                   }
                }
            }

        }
        System.out.println(defined);
        return defined;
    }

    public static ResultSet getTextTable() throws SQLException {
        if (textTable == null) {
            Statement statement = conn.createStatement();

            textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id ");
            // textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id LIMIT 0,10");
            //  textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id and rev_id='12750560'");
        }
        return textTable;
    }

    private static void ConnectDB() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://localhost/wikidb", "root", "root");
    }

    protected static void writeFile(String trainTxt) throws IOException {
        String parseText=ParseString(trainTxt);
        out.write(parseText);

    }

    private static String ParseString(String trainTxt) {
        String[] stringList= trainTxt.split(" ");
        String newTxt="";
        for (String s:stringList)
        {
            if (!isProperty(s))
            newTxt+= System.getProperty("line.separator")+s;
            else
            {
                newTxt+= " "+s;
            }
        }
        return newTxt;
    }

    private static boolean isProperty(String s) {
       for(KeyValue key: InfoBoxConst.PropetyBulunacak)
       {
           if (s.equals("[B_"+key.dbKey+"]"))
           {
               return true;
           }
       }
        return false;
    }

    private static SqlWriter15 openWriter() {
        SqlServerStream sqlStream = new SqlServerStream(conn);
        return new SqlWriter15(new SqlWriter.MySQLTraits(), sqlStream, "");
    }

}
