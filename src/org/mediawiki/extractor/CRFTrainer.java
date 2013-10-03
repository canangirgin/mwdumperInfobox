package org.mediawiki.extractor;

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
    private static Set entries;
    private static Iterator entriesIterator;
    private static String defined = "";
    private static String revText;
    private static int testSCount;
    static ArrayList<Integer> testList = new ArrayList<Integer>();
    static HashMap testCount = new HashMap();
    public static String labeledText = "";


    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        System.out.println("Bağlantı sağlanıyor...");
        String revText;
        ConnectDB();
        sqlWriter = openWriter();
        fstream = new FileWriter("out.txt");
        ftest = new FileWriter("test.txt");
        out = new BufferedWriter(fstream);
        outTest = new BufferedWriter(ftest);

        while (getTextInfoTable().next()) {
            try {

                if (testSCount < 200) {
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
        infoBox.Propetys.put("p_dogum_tar", getTextInfoTable().getString("p_dogum_tar"));
        infoBox.Propetys.put("p_dogum_yer", getTextInfoTable().getString("p_dogum_yer"));
        infoBox.Propetys.put("p_ad", getTextInfoTable().getString("p_ad"));
        infoBox.Propetys.put("p_olum_tar", getTextInfoTable().getString("p_olum_tar"));
        // infoBox.Propetys.put("p_olum_yer", getTextInfoTable().getString("p_olum_yer"));
        infoBox.Propetys.remove("p_olum_yer");
       // infoBox.Propetys.remove("p_dogum_yer");
        //infoBox.Propetys.remove("p_dogum_tar");
       // infoBox.Propetys.remove("p_olum_tar");
    }

    private static String defineTrainString(String revText) throws SQLException {

        Template template = null;
        textBlob = getTextInfoTable().getBlob("old_text");
        byte[] bdata = textBlob.getBytes(1, (int) textBlob.length());

        labeledText = new String(bdata);
        while (entriesIterator.hasNext()) {
            try {
                IExtractor iExtractor = null;
                Map.Entry mapping = (Map.Entry) entriesIterator.next();
                //TODO burası factory olacak!!!
                String mappingKey = mapping.getKey().toString();
                if (!mappingKey.equals("p_ad")) {
                    //TODO şimdilik ilk 200  iyisiyle kötusuyle test için alıyorum.

                    if (mappingKey.equals("p_dogum_yer")) {
                        iExtractor = new BirthPlaceExtractor();
                    } else {
                        iExtractor = new DateExtractor();
                    }
                    if (iExtractor != null) {
                        template = new Template();
                        //template.text = labeledText;

                        template.name = getTextInfoTable().getString("p_ad");
                        template.revisionId = getTextInfoTable().getInt("rev_id");
                        iExtractor.Extract(template, mapping);
                        System.out.println(template.name + " "+mappingKey +" = " + template.Propetys.get(mappingKey));
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (Exception e5) {
                e5.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("HATA!!!" + template.revisionId);
            }

        }
        return GetLabeledSummary();

        /*
    *   String bas[]= text.substring(0,index).split(" ");
      String son[]= text.substring(index).trim().split(" ");


      int basIndex = bas.length < 5 ? bas.length :5;
      int sonIndex = son.length < 5 ? son.length :5;
          summaryOut +=  System.getProperty("line.separator");
    for ( int i=bas.length-basIndex; i<bas.length;i++)
    {
        summaryOut += bas[i]+ " ";
    }
     summaryOut += "<ENAMEX_TYPE=\""+label+"\">"+ son[0] + "</ENAMEX> ";

        for ( int i=1; i<sonIndex;i++)
        {
            summaryOut += son[i] + " ";
        }
      }catch (Exception ex)
      {
          String hebee="";
      }
        summaryOut +=  System.getProperty("line.separator");
     return summaryOut;

    * */
    }

    //Teste konulacaksa true döner
    private static boolean CheckTestCount(String mappingKey) {

        if (testCount.containsKey(mappingKey)) {
            if (((List<Integer>) testCount.get(mappingKey)).size() < 50) {

                return true;    //Test verisine koy.
            }

        } else {
            testCount.put(mappingKey, new ArrayList<Integer>());
            return true;          //Test verisine koy.
        }
        return false;  //Test verisine koyma. Train Verisine Koy
    }

    private static String GetLabeledSummary() {
        //TODO Texte Bak
        //TODO Labelların indexlerini bul
        //TODO  5 + ve 5 * de  yine label var ise alınacak indexleri ona göre belirle.
        String summary="";
        String[] labeledTextArray=labeledText.split("<ENAMEX_TYPE=");
        if (labeledTextArray.length<2)return null;
//!!!Burayı incele ve debug et
        for (int k=0;k<labeledTextArray.length;k++)
        {
            String s= labeledTextArray[k];
            if (summary.trim().isEmpty())
            {
                String[] split=  s.trim().split(" ");
                if (split.length<=5)
                {
                    summary =s;
                } else
                {
                    for ( int i=split.length-5; i<split.length;i++)
                    {
                        summary += split[i] + " ";
                    }
                }

                continue;
            }

            String[] split=  s.trim().split(" ");
            if (k!=labeledTextArray.length-1)
            {
            if (split.length<=11)
           {
               summary+= "<ENAMEX_TYPE="+s;

           }else
           {  //ilk başı al
               summary +=  "<ENAMEX_TYPE=";
               for ( int i=0; i<6;i++)
               {
                   summary += split[i] + " ";

               }
               summary +=  System.getProperty("line.separator");
               summary +=  System.getProperty("line.separator");
               for ( int i=split.length-5; i<split.length;i++)
               {
                   summary += split[i] + " ";
               }

           }
            }else
            {
                if (split.length<=5)
                {
                    summary+= "<ENAMEX_TYPE="+s;

                    summary +=  System.getProperty("line.separator");
                    summary +=  System.getProperty("line.separator");
                }else
                {
                    //ilk başı al
                    summary +=  "<ENAMEX_TYPE=";
                    for ( int i=0; i<6;i++)
                    {
                        summary += split[i] + " ";
                    }
                    summary +=  System.getProperty("line.separator");
                    summary +=  System.getProperty("line.separator");

                }
            }



    }
        return summary;
    }


    public static ResultSet getTextInfoTable() throws SQLException {
        if (textTable == null) {
            Statement statement = conn.createStatement();

            textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id ");
            // textTable = statement.executeQuery("select rev_id,old_id,old_text,p_ad from text,infobox where rev_id = old_id LIMIT 0,10");
            //   textTable = statement.executeQuery("select * from text,infobox_analyse  where rev_id = old_id and rev_id='12563013'");
        }
        return textTable;
    }

    private static void ConnectDB() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://localhost/wikidb", "root", "root");
    }

    protected static void writeFile(String trainTxt) throws IOException {
       if (trainTxt!=null && !trainTxt.trim().isEmpty())
        out.write(trainTxt);
    }

    protected static void writeTestFile() throws IOException {

        StringBuffer testBf = new StringBuffer();
        for (Integer revId : testList) {
            testBf.append(revId + ",");
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


    private static SqlWriter15 openWriter() {
        SqlServerStream sqlStream = new SqlServerStream(conn);
        return new SqlWriter15(new SqlWriter.MySQLTraits(), sqlStream, "");
    }

}
