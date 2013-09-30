package org.mediawiki.extractor;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 30.09.2013
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public class DateExtractor extends IExtractor {
    @Override
    void Extract(Template template,Map.Entry infobox)  throws SQLException {

        Pattern p = Pattern.compile("\\d{4}");

        Matcher matcher = p.matcher(""+infobox.getValue().toString());
        if (matcher.find())
        {

            int year= Integer.parseInt(matcher.group(0));
        int index = CRFTrainer.labeledText.indexOf(""+year);
        if (index<1)
            return ;
        else
        {
            template.Propetys.put("p_dogum_tar",year);
            LabelData(""+year, infobox,index);
        }
        }

    }
}
