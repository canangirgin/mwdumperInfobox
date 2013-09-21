package org.mediawiki.extractor;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 02.05.2013
 * Time: 03:26
 * To change this template use File | Settings | File Templates.
 */
public class BirthPlaceExtractor extends IExtractor {
    public String numbers = "0123456789";
    String[] splitter = new String[2];
    int[] splitterIndex = new int[2];
    String name=null;
    String[] locations= new String[]{"köy","ilçe","şehri","ilinin","kent"};
    int index=-1;
     //TODO bu locations bölumn atlama. Burası yapılacak
    //TODO Bulduğun şey sayımı kontrolunu yap

    @Override
    public String Extract(Template template) throws SQLException {
        String birthPlace = null;
        index=-1;
        text=template.text;
        name=template.name;
        birthPlace = null;
        birthPlace = Template1(text);
            if (validateBirthPlace(birthPlace)) {
                birthPlace = getRoot(birthPlace).trim();
            }
        if (birthPlace != null && birthPlace != "")
        {
            template.Propetys.put("p_dogum_yer",birthPlace);
            return GetSummary (birthPlace,"p_dogum_yer",index);
        }
        return null;
    }

    private String getRoot(String birthPlace) {
       /* int tempstop =birthPlace.indexOf("'");
        if (tempstop >0)
        {
            birthPlace=birthPlace.substring(0, tempstop);
        }else
        {
            tempstop= birthPlace.indexOf("’");
            if (tempstop >0)
            {
                birthPlace=birthPlace.substring(0, tempstop);
            }
        }*/

        return birthPlace;
    }

    //	“(.d” + Ayıraç Kuralı
    //Template1 algoritması tamam
    private String Template1(String text) {
        String birthPlace = null;
        String controlNumber = null;
        String tempText = "";

        int tempStart = text.indexOf("(d.");
        if (tempStart > 0) {
            getSplitters(text, tempStart);
            //Splitten Öncesine bak. Karakter mi?
            controlNumber = getControlString(text, splitterIndex[0]);
            if (numbers.contains(controlNumber)) {
                //Splitten Öncesi sayı sonrasını al
                //Tek ayıraç var(")") ise burada doğum yeri yoktur.
                if (splitter[0] != ")") {
                    birthPlace = text.substring(splitterIndex[0] + 1, splitterIndex[1]);
                   index=    splitterIndex[0] + 1;
                }
            } else {
                //Splitten Öncesi karakter öncesini al
                tempText = text.substring(tempStart, splitterIndex[0]);
                splitterIndex[1] = tempText.lastIndexOf("-");
                if (splitterIndex[1] <= 0) {
                    splitterIndex[1] = tempText.lastIndexOf(" ");
                }
                birthPlace = tempText.substring(splitterIndex[1] + 1);
                index=   tempStart+ splitterIndex[1] + 1;
            }
        }
        if (!validateBirthPlace(birthPlace)) {
            return Template2(text);
        }
        return birthPlace;
    }
    //    2.	“(doğ.”  + Ayıraç Kuralı

    private String Template2(String text) {
        String birthPlace = null;
        String controlNumber = null;
        String tempText = "";

        int tempStart = text.indexOf("(doğ.");
        if (tempStart > 0) {
            getSplitters(text, tempStart);
            //Splitten Öncesine bak. Karakter mi?
            controlNumber = getControlString(text, splitterIndex[0]);
            if (numbers.contains(controlNumber)) {
                //Splitten Öncesi sayı sonrasını al
                //Tek ayıraç var(")") ise burada doğum yeri yoktur.
                if (splitter[0] != ")") {
                    birthPlace = text.substring(splitterIndex[0] + 1, splitterIndex[1]);
                    index=    splitterIndex[0] + 1;
                }
            } else {
                //Splitten Öncesi karakter öncesini al
                tempText = text.substring(tempStart, splitterIndex[0]);
                splitterIndex[1] = tempText.lastIndexOf("-");
                if (splitterIndex[1] <= 0) {
                    splitterIndex[1] = tempText.lastIndexOf(" ");
                }
                birthPlace = tempText.substring(splitterIndex[1] + 1);
                index=   tempStart+ splitterIndex[1] + 1;
            }
        }
        if (!validateBirthPlace(birthPlace)) {
            return Template3(text);
        }
        return birthPlace;
    }
    // 3.	“; d.”    + Ayıraç Kuralı
    private String Template3(String text) {
        String birthPlace = null;
        String controlNumber = null;
        String tempText = "";

        int tempStart = text.indexOf("; d.");
        if (tempStart > 0) {
            getSplitters(text, tempStart);
            //Splitten Öncesine bak. Karakter mi?
            controlNumber = getControlString(text, splitterIndex[0]);
            if (numbers.contains(controlNumber)) {
                //Splitten Öncesi sayı sonrasını al
                //Tek ayıraç var(")") ise burada doğum yeri yoktur.
                if (splitter[0] != ")") {
                    birthPlace = text.substring(splitterIndex[0] + 1, splitterIndex[1]);
                    index=    splitterIndex[0] + 1;
                }
            } else {
                //Splitten Öncesi karakter öncesini al
                tempText = text.substring(tempStart, splitterIndex[0]);
                splitterIndex[1] = tempText.lastIndexOf("-");
                if (splitterIndex[1] <= 0) {
                    splitterIndex[1] = tempText.lastIndexOf(" ");
                }
                birthPlace = tempText.substring(splitterIndex[1] + 1);
                index=   tempStart+ splitterIndex[1] + 1;
            }
        }
        if (!validateBirthPlace(birthPlace)) {
            return Template4(text);
        }
        return birthPlace;
    }
    //    4.	 “K(“    + Ayıraç Kuralı

    private String Template4(String text) {

        String birthPlace = null;
        String controlNumber = null;
        String tempText = "";

        int tempStart = text.indexOf(name+"(");
        if (tempStart > 0) {
            getSplitters(text, tempStart);
            //Splitten Öncesine bak. Karakter mi?
            controlNumber = getControlString(text, splitterIndex[0]);
            if (numbers.contains(controlNumber)) {
                //Splitten Öncesi sayı sonrasını al
                //Tek ayıraç var(")") ise burada doğum yeri yoktur.
                if (splitter[0] != ")") {
                    birthPlace = text.substring(splitterIndex[0] + 1, splitterIndex[1]);
                    index=    splitterIndex[0] + 1;
                }
            } else {
                //Splitten Öncesi karakter öncesini al
                tempText = text.substring(tempStart, splitterIndex[0]);
                splitterIndex[1] = tempText.lastIndexOf("-");
                if (splitterIndex[1] <= 0) {
                    splitterIndex[1] = tempText.lastIndexOf(" ");
                }
                birthPlace = tempText.substring(splitterIndex[1] + 1);
                index=   tempStart+ splitterIndex[1] + 1;
            }
        }
        if (!validateBirthPlace(birthPlace)) {
            return Template5(text);
        }
        return birthPlace;
    }




    // 5.	DY doğumlu
    private String Template5(String text) {
    String birthPlace = null;
    int tempStop=text.indexOf("doğumlu");
    if (tempStop > 0) {
       tempStop= tempStop-1;
       birthPlace= controlLocation(text,tempStop);
       }

        if (!validateBirthPlace(birthPlace)) {
            return Template6(text);
        }
        return birthPlace;
    }

    // 6.	DY doğdu
    private String Template6(String text) {
        String birthPlace = null;
        int tempStop=text.indexOf("doğdu");
        if (tempStop > 0) {
            tempStop= tempStop-1;
            birthPlace= controlLocation(text,tempStop);
        }

        if (!validateBirthPlace(birthPlace)) {
            return Template7(text);
        }
        return birthPlace;
    }



    // 7.   DY  dünyaya geldi+
    private String Template7(String text) {
        String birthPlace = null;
        int tempStop=text.indexOf("dünyaya geldi");
        if (tempStop > 0) {
            tempStop= tempStop-1;
            birthPlace= controlLocation(text,tempStop);
        }

        if (!validateBirthPlace(birthPlace)) {
            return Template8(text);
        }
        return birthPlace;
    }

//    8.	DY  dünyaya gelmiş+

    private String Template8(String text) {
        String birthPlace = null;
        int tempStop=text.indexOf("dünyaya gelmiş");
        if (tempStop > 0) {
            tempStop= tempStop-1;
            birthPlace= controlLocation(text,tempStop);
        }

        return birthPlace ;
    }
    private boolean validateBirthPlace(String birthPlace) {

       return birthPlace != null && !birthPlace.isEmpty() && birthPlace.length()>2&& !numbers.contains(""+birthPlace.trim().charAt(birthPlace.trim().length()-1));
    }
    private void getSplitters(String text, int tempStart) {
        splitter = new String[2];
        splitterIndex = new int[2];
        getSplitter(text, tempStart, 0);
        if (splitter[0] == ")") {
            splitterIndex[1] = splitterIndex[0];
            splitter[1] = splitter[0];
        } else {
            getSplitter(text, splitterIndex[0] + 1, 1);
        }

    }


    private void getSplitter(String text, int tempStart, int tempIndex) {
        // Noktalı virgul var ise ayıraç=; ,Virgul var ise ayıraç=, ,- var ise ayıraç=- ,yok ise ayıraç=”)”
        String textTemp = text.substring(tempStart, text.indexOf(")", tempStart) + 1);
        int  tIndex = textTemp.indexOf(")", 0) + tempStart;
        String tempSplitter = ")";
        if (textTemp.indexOf(";", 0)!=-1 && textTemp.indexOf(";", 0) + tempStart < tIndex)
        {
         tIndex =textTemp.indexOf(";", 0) + tempStart;
         tempSplitter = ";";
        }
        if (textTemp.indexOf("-", 0)!=-1 && textTemp.indexOf("-", 0) + tempStart < tIndex) {
            tIndex = textTemp.indexOf("-", 0) + tempStart;
            tempSplitter = "-";
        }
        if (textTemp.indexOf(",", 0)!=-1 && textTemp.indexOf(",", 0) + tempStart < tIndex) {
            tIndex = textTemp.indexOf(",", 0) + tempStart;
            tempSplitter = ",";
        }
        splitter[tempIndex] = tempSplitter;
        splitterIndex[tempIndex] = tIndex;
    }

    private String getControlString(String text, int startIndex) {
        String controlNumber;
        controlNumber = text.substring(startIndex - 1, startIndex);
        if (controlNumber.isEmpty() || controlNumber.equals(" ")) {
            return getControlString(text, startIndex - 1);
        }
        return controlNumber;
    }
    private String controlLocation(String text, int tempStop) {
        String birthPlaceTemp="";
        int tempStart= text.substring(0,tempStop).lastIndexOf(" ");
        birthPlaceTemp =  text.substring(tempStart,tempStop);
        for(String loc:locations)
        {
            if(birthPlaceTemp.contains(loc))
            {
                return  controlLocation(text,tempStart);
            }
        }
        index = tempStart;
        return birthPlaceTemp;
    }


    /*
    Patternler:

    Doğum Yeri Templateler:
1.	“(.d” + Ayıraç Kuralı
2.	DY doğumlu
3.	DY doğdu
4.	DY  dünyaya geldi+
5.	“; d.”    + Ayıraç Kuralı
6.	DY  dünyaya gelmiş+
7.	“(doğ.”  + Ayıraç Kuralı
8.	 “K(“    + Ayıraç Kuralı


  Ayıraç Kuralı:
a.	Noktalı virgul var ise ayıraç=; ,Virgul var ise ayıraç=, yok ise ayıraç=”)”
b.	 Ayıraçtan  önceki yazı ise doğum yeri değil ise Ayıraçtan  sonraki  sonraki DY

     */
}
