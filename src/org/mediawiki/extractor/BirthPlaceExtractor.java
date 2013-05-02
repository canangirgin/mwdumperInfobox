package org.mediawiki.extractor;

import java.sql.Blob;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 02.05.2013
 * Time: 03:26
 * To change this template use File | Settings | File Templates.
 */
public class BirthPlaceExtractor extends Extractor {
   public String numbers="0123456789";
    String[] splitter= new String[2];
    int[] splitterIndex= new  int[2];
    Blob textBlob = null;
    String text = null;
    @Override
    public String Extract() throws SQLException {
        String birthPlace=null;
        while(getTextTable().next())
        {
            textBlob = getTextTable().getBlob("old_text");
            byte[] bdata = textBlob.getBytes(1, (int) textBlob.length());
            text = new String(bdata);
            birthPlace=  Template1(text);
            if (birthPlace== null || birthPlace.isEmpty())
            {

            }

         }
        return birthPlace;
    }
    //	“(.d” + Ayıraç Kuralı
    //Template1 algoritması tamam
    private String Template1(String text) {
      String birthPlace=null;
      String controlNumber=null;
      String tempText="";

      int tempStart= text.indexOf("(d.");
        if (tempStart>0)
      {
           getSplitters(text, tempStart);
          //Splitten Öncesine bak. Karakter mi?
          controlNumber = getControlString(text,splitterIndex[0]);
          if (numbers.contains(controlNumber))
          {
             //Splitten Öncesi sayı sonrasını al
             //Tek ayıraç var(")") ise burada doğum yeri yoktur.
              if (splitter[0]!=")")
              {
                birthPlace= text.substring(splitterIndex[0]+1,splitterIndex[1]);
              }
          }else
          {
              //Splitten Öncesi karakter öncesini al
              tempText =text.substring(tempStart,splitterIndex[0]);
              splitterIndex[1]=tempText.lastIndexOf("-");
              if (splitterIndex[1]<=0)
              {
                  splitterIndex[1]=tempText.lastIndexOf(" ");
              }
              birthPlace= tempText.substring(splitterIndex[1]+1);
          }
      }
        if (birthPlace== null ||birthPlace.isEmpty())
        {
            return Template2();
        }
        return birthPlace;
    }

    private String Template2() {
    }

    private void getSplitters(String text, int tempStart)
    {
        splitter= new String[2];
        splitterIndex= new  int[2];
        getSplitter(text,tempStart,0);
        if (splitter[0]==")")
        {
            splitterIndex[1]=splitterIndex[0];
            splitter[1]= splitter[0];
        }
        else
        {
        getSplitter(text,splitterIndex[0]+1,1);
        }

    }
    private void getSplitter( String text, int tempStart, int tempIndex) {
        // Noktalı virgul var ise ayıraç=; ,Virgul var ise ayıraç=, yok ise ayıraç=”)”

        String textTemp=text.substring(tempStart,text.indexOf(")",tempStart)+1);
        if (textTemp.contains(";"))
        {   splitter[tempIndex]=";";
            splitterIndex[tempIndex]=textTemp.indexOf(";", 0)+tempStart;
        }  else if (textTemp.contains(","))
        {
            splitter[tempIndex]=",";
            splitterIndex[tempIndex]=textTemp.indexOf(",", 0)+tempStart;
        }
        else
        {
            splitter[tempIndex]=")";
            splitterIndex[tempIndex]=textTemp.indexOf(")", 0)+tempStart;
        }

    }

    private String getControlString(String text,int startIndex) {
        String controlNumber;
        controlNumber=  text.substring(startIndex-1,startIndex);
        if  (controlNumber.isEmpty() || controlNumber=="" )
        {
            return  getControlString(text,startIndex-1);
        }
        return controlNumber;
    }
    /*
    Patternler:

    Doğum Yeri Templateler:
1.	“(.d” + Ayıraç Kuralı
2.	 “K(“    + Ayıraç Kuralı
3.	DY doğumlu
4.	DY doğdu
5.	DY  dünyaya geldi+
6.	“; d.”    + Ayıraç Kuralı
7.	DY  dünyaya gelmiş+
8.	“(doğ.”  + Ayıraç Kuralı

  Ayıraç Kuralı:
a.	Noktalı virgul var ise ayıraç=; ,Virgul var ise ayıraç=, yok ise ayıraç=”)”
b.	 Ayıraçtan  önceki yazı ise doğum yeri değil ise Ayıraçtan  sonraki  sonraki DY

     */
}
