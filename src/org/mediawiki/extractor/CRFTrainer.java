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
    private static FileWriter ftest;
    private static BufferedWriter out;
    private static BufferedWriter outTest;
    private static  Set entries;
    private static Iterator entriesIterator ;
    private static String defined = "";
    private static String revText;
    private static int testSCount;
    static ArrayList<Integer> testList= new ArrayList<Integer>();
    static HashMap  testCount=new HashMap();



    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        System.out.println("Bağlantı sağlanıyor...");
        String revText;
        ConnectDB();
        sqlWriter = openWriter();
        fstream = new FileWriter("out.txt");
        ftest= new FileWriter("test.txt");
        out = new BufferedWriter(fstream);
        outTest= new BufferedWriter(ftest);

        while (getTextInfoTable().next()) {
            try {
                if (testSCount<200)
                {
                    testList.add(getTextInfoTable().getInt("rev_id"));
                    testSCount++;
                    continue;
                }
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
        writeTestFile();
        outTest.close();
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
                //TODO şimdilik ilk 200  iyisiyle kötusuyle test için alıyorum.

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
              //TODO test için burada kes(50 tane)
                    boolean checkResult=CheckResult(template, mappingKey, summary);
                    //TODO şimdilik ilk 200  iyisiyle kötusuyle test için alıyorum.
                    if (checkResult)
                    {
                        defined += summary;
                    }
                    /*
                    if( checkResult && CheckTestCount(mappingKey) )
                   {
                       ((List<Integer>)testCount.get(mappingKey)).add( template.revisionId);
                   }
                   else  if (checkResult)
                   {
                       defined += summary;
                   }
                    */
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
   //Teste konulacaksa true döner
    private static boolean CheckTestCount(String mappingKey) {

        if (testCount.containsKey(mappingKey))
        {
          if (((List<Integer>)testCount.get(mappingKey)).size()<50)
          {

            return true;    //Test verisine koy.
          }

        } else
        {
            testCount.put(mappingKey,new ArrayList<Integer>());
            return true;          //Test verisine koy.
        }
        return false;  //Test verisine koyma. Train Verisine Koy
    }

    private static boolean CheckResult(Template template, String mappingKey, String summary) {
        if (summary != null && template.Propetys.get(mappingKey).toString().length()<4) {
            if(infoBox.Propetys.get(mappingKey).toString().toLowerCase().contains(template.Propetys.get(mappingKey).toString().toLowerCase() ))
            return true;
        }else
         if (summary != null && infoBox.Propetys.get(mappingKey).toString().toLowerCase().contains(template.Propetys.get(mappingKey).toString().toLowerCase().substring(0,4)))
         {
            return true;
         }
        return false;
    }


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
    protected static void writeTestFile() throws IOException {

        StringBuffer testBf=new StringBuffer();
        for(Integer revId:testList)
        {
            testBf.append(revId +"," );
        }
        testBf.deleteCharAt(testBf.lastIndexOf(","));
       /* Iterator testIterator = testCount.entrySet().iterator();
        while (testIterator.hasNext()) {

            Map.Entry mapping = (Map.Entry) testIterator.next();
            String mappingKey=   mapping.getKey().toString();
            testBf.append(mappingKey);
            testBf.append( System.getProperty("line.separator"));
            for(Integer revId:(List<Integer>)mapping.getValue())
            {
                testBf.append(revId +"," );
            }
            testBf.deleteCharAt(testBf.lastIndexOf(","));
            testBf.append( System.getProperty("line.separator"));
        }
        */
        outTest.write(testBf.toString());
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
