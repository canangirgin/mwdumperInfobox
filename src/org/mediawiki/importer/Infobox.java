package org.mediawiki.importer;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 18.03.2013
 * Time: 04:22
 * To change this template use File | Settings | File Templates.
 */
public class Infobox {

    public String Text;
    public String Template;
    public HashMap<Object, Object> Propetys;

    public Infobox() {
        Text = "";
        Template = "";
        Propetys = new HashMap<Object, Object>();
        for(KeyValue bulunacak:InfoBoxConst.PropetyBulunacak)
        {
            Propetys.put(bulunacak.dbKey,null);
        }

        //   {"infobox",revision.InfoBox.Text},
        // {"infobox_template",revision.InfoBox.Template}};
    }
    public void TextToProperty() {
        System.out.println(Text);
         int indexStartValue = 0;
        boolean exit = true;

        for (String property : Text.replace("\n","").split("\\|"))
             if (!property.isEmpty()) {
                try {
                    String key = property.substring(0, property.indexOf("=")).trim();
                    String value = "";
                    indexStartValue = property.indexOf("=");
                    if (indexStartValue > 0) {
                        value = property.substring(indexStartValue + 1).trim().replace("[", "").replace("]", "");
                    }
                   extractProperty(key, value);
                   System.out.println(key + "=" + value);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
    }

    private void extractProperty(String key, String value) {
        for (KeyValue bulunacakDb: InfoBoxConst.PropetyBulunacak)
        {
        for (String bulunacakValue:bulunacakDb.values)
        {
            if (key.trim().startsWith(bulunacakValue.toLowerCase()))
            {
                Propetys.put(bulunacakDb.dbKey, value);
                return;
            }
        }
        }
    }
}


