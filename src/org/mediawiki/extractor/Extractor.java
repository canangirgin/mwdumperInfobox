package org.mediawiki.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 02.05.2013
 * Time: 03:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class Extractor {
    private ResultSet textTable=null;

    abstract String Extract() throws SQLException;
    protected ResultSet getTextTable() throws SQLException {
        if (textTable==null)
        {
        Statement statement=InfoBoxExtractor.baglanti.createStatement();
       // TODO burayı incele
       // Deneme amaçlı şimdilik 10 tane ile çalış
       // Şimdilik sadece infobox olanlara bak
            textTable= statement.executeQuery("select old_id,old_text from text,infobox where rev_id = old_id LIMIT 0,10");
        }
        return textTable;
    }

}
