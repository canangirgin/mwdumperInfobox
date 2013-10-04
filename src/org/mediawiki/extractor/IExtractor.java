package org.mediawiki.extractor;

import java.sql.SQLException;
import java.util.Map;

public abstract class IExtractor {


    abstract void Extract(Template template, Map.Entry infobox) throws SQLException;

    protected void LabelData(String result, Map.Entry infobox, int index) {
        result=CheckResult(result, "" + infobox.getValue());
        if (result != null)
        {
            index = CRFTrainer.labeledText.indexOf(result, index);
            //TODO daha önce işaretlenmiş bir veri var ise bunu dikkate almak gerekir.
            //O yuzden sadece işaretleyecek en son summary çıkartacak.

           // CRFTrainer.labeledText = CRFTrainer.labeledText.substring(0, index).trim() + " <ENAMEX_TYPE=\"" + infobox.getKey() + "\">" + result.trim() + "</ENAMEX>" + CRFTrainer.labeledText.substring(CRFTrainer.labeledText.indexOf(" ", index + result.length()));
            CRFTrainer.labeledText = CRFTrainer.labeledText.substring(0, index).trim() + " <ENAMEX_TYPE=\"" + infobox.getKey() + "\">" + CRFTrainer.labeledText.substring(index,CRFTrainer.labeledText.indexOf(" ",index))+ "</ENAMEX> " +CRFTrainer.labeledText.substring(CRFTrainer.labeledText.indexOf(" ", index)).trim();


        }
    }

    private static String CheckResult(String result, String values) {
        if (result != null && !result.isEmpty())

        {
            String[] splits = result.split("\\s");
            for (String resultSplit : splits) {
                if (resultSplit.length() < 4) {
                    if (values.toLowerCase().contains(resultSplit.toLowerCase())) {
                        return resultSplit;
                    }
                } else {
                    if (resultSplit != null && !resultSplit.isEmpty() && values.toLowerCase().contains(resultSplit.toLowerCase().substring(0, 4))) {
                        return resultSplit;
                    }
                }
            }
        }
        return null;
    }


}
