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

        int indexStartValue = 0;
        boolean exit = true;

        for (String property : Text.replaceFirst("\\|","").split("\n\\|"))
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
       //TODO bayrak simgeleri silmek lazım
        for (KeyValue bulunacakDb: InfoBoxConst.PropetyBulunacak)
        {
        for (String bulunacakValue:bulunacakDb.values)
        {
            //value parçalanacak mı?
            if (bulunacakValue.contains("/"))
            {
                String[] bulunacakValueProp=bulunacakValue.toLowerCase().split("/");
                if (key.trim().equals(bulunacakValueProp[0].trim()))
                {
                    String[] splitValue;
                   if (value.contains("<br/>"))
                    {
                         splitValue=value.split("<br/>");
                    }else{
                     splitValue=value.split("<br />");}
                if (splitValue.length >=Integer.parseInt(bulunacakValueProp[1]))
                {
                    String[] splitValueRow= splitValue[Integer.parseInt(bulunacakValueProp[1])-1].split(",");
                    if (splitValue.length >=Integer.parseInt(bulunacakValueProp[2]))
                    {
                        value= getClearText(splitValueRow[Integer.parseInt(bulunacakValueProp[2])-1]);
                        Propetys.put(bulunacakDb.dbKey, value);
                        return;
                    }
                }
              }
            }
            else
            if (key.trim().startsWith(bulunacakValue.toLowerCase()))
            {
                value=  getClearText(value);
               Propetys.put(bulunacakDb.dbKey,value);
                return;
            }
        }
        }
    }

    //Veri temizleme işlemleri
    private String getClearText(String text) {
        if(text.contains("{{bayraksimge"))
        {
            text=text.substring(0,text.indexOf("{{bayraksimge"))+text.substring(text.indexOf("}}",text.indexOf("{{bayraksimge"))+2);
        }
        text=text.replace("[[","");
        text=text.replace("]]","");

        return text;
    }
}


