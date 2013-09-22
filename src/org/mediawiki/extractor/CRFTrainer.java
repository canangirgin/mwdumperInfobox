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
    private static  Set entries;
    private static Iterator entriesIterator ;
    private static String defined = "";
    private static String revText;



    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        System.out.println("Bağlantı sağlanıyor...");
        String revText;

        ConnectDB();
        sqlWriter = openWriter();
        fstream = new FileWriter("out.txt");
        out = new BufferedWriter(fstream);

        while (getTextInfoTable().next()) {
            try {
                infoBox = new Infobox();
                textBlob = getTextInfoTable().getBlob("old_text");
                byte[] bdata = textBlob.getBytes(1, (int) textBlob.length());
                revText = new String(bdata);
                infoBox.rev_id = getTextInfoTable().getInt("rev_id");
                setInfoProperties();
                entries = infoBox.Propetys.entrySet();
                entriesIterator = entries.iterator();
                defined = "";
                writeFile(defineTrainString(revText));

            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("HATA!!!" + e1.getStackTrace() + e1.getMessage() + infoBox.rev_id);
            }
        }
        out.close();
    }

    private static void setInfoProperties() throws SQLException {
        //infoBox.Propetys.put("p_dogum_tar", getTextTable().getString("p_dogum_tar"));
        infoBox.Propetys.put("p_dogum_yer", getTextInfoTable().getString("p_dogum_yer"));
        infoBox.Propetys.put("p_ad", getTextInfoTable().getString("p_ad"));
        //infoBox.Propetys.put("p_olum_tar", getTextTable().getString("p_olum_tar"));
       // infoBox.Propetys.put("p_olum_yer", getTextInfoTable().getString("p_olum_yer"));
    }

    private static String defineTrainString(String revText) {
        Template template=null;
        while (entriesIterator.hasNext()) {
            try {
                IExtractor iExtractor=null ;
                Map.Entry mapping = (Map.Entry) entriesIterator.next();
                //TODO burası factory olacak!!!
                String mappingKey=   mapping.getKey().toString();
                if(mappingKey.equals("p_dogum_yer"))
                {
                    iExtractor = new BirthPlaceExtractor();
                }
                if (iExtractor!=null)
                {
                template = new Template();
                textBlob = getTextInfoTable().getBlob("old_text");
                byte[] bdata = textBlob.getBytes(1, (int) textBlob.length());
                template.text = new String(bdata);
                template.name = getTextInfoTable().getString("p_ad");
                template.revisionId = getTextInfoTable().getInt("rev_id");



                String summary =  iExtractor.Extract(template);
               if (summary != null && template.Propetys.get(mappingKey).toString().length()<4) {
                   if(infoBox.Propetys.get(mappingKey).toString().toLowerCase().contains(template.Propetys.get(mappingKey).toString().toLowerCase() ))
                    defined += summary;
               }else
                if (summary != null && infoBox.Propetys.get(mappingKey).toString().toLowerCase().contains(template.Propetys.get(mappingKey).toString().toLowerCase().substring(0,4)))
                {
                    defined += summary;
                }
                System.out.println(template.name + " doğum yer = " + template.Propetys.get("p_dogum_yer"));
                }
            }    catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e5) {
                e5.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("HATA!!!" + template.revisionId);
            }

        }

        return defined;
    }

     /*
    private static String SearchInInfoBox(Object p_dogum_yer) {
        Map.Entry mapping = (Map.Entry) entriesIterator.next();
        String key = mapping.getKey().toString();
        String valueList = mapping.getValue().toString();
        String   revTextLower= revText.toLowerCase();
        if (!valueList.isEmpty()) {
            HashSet<String> values = new HashSet<String>((Arrays.asList(valueList.split(","))));
            for (String value : values) {
                if (defined.toLowerCase().contains(value)) {
                    defined = defined.substring(0, defined.toLowerCase().indexOf(value)+value.length()) + " [B_" + key + "] " + defined.substring(defined.toLowerCase().indexOf(value) + value.length());
                }  else if (revTextLower.contains(value)) {
                    //TODO Şimdilik ilk bulduğunu alıyor!!!
                    int startIndex = revTextLower.indexOf(value) > 75 ? revTextLower.indexOf(value) - 75 : 0;
                    int stopIndex = revTextLower.length() - revTextLower.indexOf(value) > 75 ? revTextLower.indexOf(value) + value.length() + 75 : revTextLower.length();
                    String temp = revText.substring(startIndex, revTextLower.indexOf(value)+value.length());
                    defined += System.getProperty("line.separator") + temp.substring(temp.indexOf(" ") > 0 ? temp.indexOf(" ") : 0) + " [B_" + key + "] ";
                    temp = revText.substring(revTextLower.indexOf(value) + value.length(), stopIndex);
                    defined += temp.substring(0, temp.lastIndexOf(" ") > 0 ? temp.lastIndexOf(" ") : temp.length());
                }
            }
        }
        return defined;
    }
       */

    public static ResultSet getTextInfoTable() throws SQLException {
        if (textTable == null) {
            Statement statement = conn.createStatement();

            textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id ");
            // textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id LIMIT 0,10");
            //   textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id and rev_id='10463244'");
        }
        return textTable;
    }

    private static void ConnectDB() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://localhost/wikidb", "root", "root");
    }

    protected static void writeFile(String trainTxt) throws IOException {
        out.write(trainTxt);
    }
  /*
    private static String ParseString(String trainTxt) {
        String[] stringList = trainTxt.split(" ");
        String newTxt = "";
        for (String s : stringList) {
            if (!isProperty(s))
                newTxt += System.getProperty("line.separator") + s;
            else {
                newTxt += " " + s;
            }
        }
        return newTxt;
    }
    */
    private static boolean isProperty(String s) {
        for (KeyValue key : InfoBoxConst.PropetyBulunacak) {
            if (s.equals("[B_" + key.dbKey + "]")) {
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
