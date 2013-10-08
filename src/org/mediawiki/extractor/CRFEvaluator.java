package org.mediawiki.extractor;

import edu.yildiz.nlp.sequence.tagger.Run.WikiRelationTagger;
import org.apache.commons.io.FileUtils;
import org.mediawiki.importer.KeyValue;
import org.mediawiki.importer.SqlServerStream;
import org.mediawiki.importer.SqlWriter;
import org.mediawiki.importer.SqlWriter15;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 28.09.2013
 * Time: 21:47
 * To change this template use File | Settings | File Templates.
 */
public class CRFEvaluator {
    private static ResultSet textTable = null;
    public static Blob textBlob = null;
    public static Connection conn = null;
    public static SqlWriter15 sqlWriter;
    public static Infobox infoBox;
    private static FileWriter fstream;
    private static BufferedWriter out;
    private static Set entries;
    private static Iterator entriesIterator ;
    private static String defined = "";
    private static String revText;


    public static void main(String[] args) throws Exception {
        System.out.println("Bağlantı sağlanıyor...");
        String revText;

        ConnectDB();
        sqlWriter = openWriter();
        fstream = new FileWriter("testEval.txt");
        out = new BufferedWriter(fstream);
        WikiRelationTagger.LoadModel("C:\\Users\\hs\\repo\\wiki-extractor\\nouncrfHadiGari");
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

    private static void setInfoProperties() throws SQLException, IOException {
        infoBox.Propetys.clear();
        infoBox.Propetys.put("p_dogum_tar", getTextInfoTable().getString("p_dogum_tar"));
        infoBox.Propetys.put("p_dogum_yer", getTextInfoTable().getString("p_dogum_yer"));
        infoBox.Propetys.put("p_ad", getTextInfoTable().getString("p_ad"));
        infoBox.Propetys.put("p_olum_tar", getTextInfoTable().getString("p_olum_tar"));
       // infoBox.Propetys.put("p_olum_yer", getTextInfoTable().getString("p_olum_yer"));
    }

    private static String defineTrainString(String revText) throws Exception {
        Template template=null;
        List<edu.yildiz.nlp.sequence.tagger.ResultSet> resultSet=null;

        if(entriesIterator.hasNext())
        {
        resultSet= WikiRelationTagger.test( revText);
        }
        while (entriesIterator.hasNext()) {
            try {

                   Map.Entry mapping = (Map.Entry) entriesIterator.next();
                //TODO burası factory olacak!!!
                    String mappingKey=   mapping.getKey().toString();
                    if (mappingKey!= "p_ad")
                    {
                    template = new Template();
                    template.text = revText;
                    template.name = getTextInfoTable().getString("p_ad");
                    template.revisionId = getTextInfoTable().getInt("rev_id");
                     int status=5; //Bulamadım ama var
                     String word = "";
                    for(edu.yildiz.nlp.sequence.tagger.ResultSet result: resultSet)
                    {
                     int newStatus=5;
                     String   newword=result.word;
                          //Buldum
                    if(result.label.contains(mappingKey) && !result.word.isEmpty())
                        {
                            String resultfirst =(newword.length()>4 ?newword.toLowerCase().substring(0,4): newword);
                            String resultLast = (newword.length()>4 ?newword.toLowerCase().substring(newword.length()-4): result.word);

                        //Buldum ancak infoda yok
                         if (mapping.getValue()==null || ((String)mapping.getValue()).isEmpty())
                         {
                             newStatus=2;
                         }else
                         //CRF buldu ve infolarda var.Herşey tamam:)
                         if (((String)mapping.getValue()).toLowerCase().contains(resultfirst) ||((String)mapping.getValue()).toLowerCase().contains(resultLast) )
                         {
                             newStatus=1;
                         } else
                         {
                             newStatus= 4;
                         }
                        }else //Bulamadım
                        if(mapping.getValue()==null || ((String)mapping.getValue()).isEmpty()) //Bulamadım ancak zaten yoktu
                        {
                            newStatus= 3;
                        }

                     if (word.isEmpty() || status >newStatus)
                     {
                      status= newStatus;
                      word = newword;
                     }
                    }
                defined +=  template.revisionId +","+ status +","+ mapping.getKey() +","+word+","+mapping.getValue();
                defined +=  System.getProperty("line.separator");
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



    public static ResultSet getTextInfoTable() throws SQLException, IOException {
        String revId= FileUtils.readFileToString(new File("test.txt"));

        if (textTable == null) {
            Statement statement = conn.createStatement();
            textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id and rev_id in ( "+revId+")");
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
