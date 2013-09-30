package org.mediawiki.extractor;

import java.sql.SQLException;
import java.util.Map;

public abstract class  IExtractor {


   abstract void Extract(Template template,Map.Entry infobox) throws SQLException;

    protected void LabelData(String result,Map.Entry infobox, int index)
    {
        if (CheckResult(result,""+infobox.getValue()))
        {
            index= CRFTrainer.labeledText.indexOf(result,index);
    //TODO daha önce işaretlenmiş bir veri var ise bunu dikkate almak gerekir.
    //O yuzden sadece işaretleyecek en son summary çıkartacak.
        CRFTrainer.labeledText=CRFTrainer.labeledText.substring(0,index) +"<ENAMEX_TYPE=\""+infobox.getKey()+"\">"+ result.trim() + "</ENAMEX>"+ CRFTrainer.labeledText.substring(CRFTrainer.labeledText.indexOf(" ",index+result.length()));
        }
    }

    private static boolean CheckResult(String result, String values) {
        if (result!= null && !result.isEmpty() && result.length()<4)
        {
             if (values.toLowerCase().contains(result.toLowerCase()))
             {
                 return true;
             }
        }else
        {
            if (result != null && !result.isEmpty() && values.toLowerCase().contains(result.toLowerCase().substring(0,4)))
            { return true;}
        }
        return false;
    }


}
