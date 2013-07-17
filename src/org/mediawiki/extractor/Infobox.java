package org.mediawiki.extractor;

import org.mediawiki.importer.KeyValue;

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
    public int rev_id;
    public Infobox() {
        Text = "";
        Template = "";
        Propetys = new HashMap<Object, Object>();
        for(KeyValue bulunacak:InfoBoxConst.PropetyBulunacak)
        {
            Propetys.put(bulunacak.dbKey,null);
        }
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
       //TODO bayrak simgelerini silmek lazım
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
                        Propetys.put(bulunacakDb.dbKey, Propetys.get(bulunacakDb.dbKey) + "," + value);
                        continue;
                    }
                }
              }
            }
            else
            if (key.trim().startsWith(bulunacakValue.toLowerCase()))
            {
                value=  getClearText(value);
                if (!value.isEmpty() && !value.equals("") && !value.equals(null))
                {
                if ( Propetys.get(bulunacakDb.dbKey)!= null)
                {
                    value=Propetys.get(bulunacakDb.dbKey)+ "," +value;
                }
                Propetys.put(bulunacakDb.dbKey,value);
                }
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
        if (text.contains("tarihi"))
        {
        text=text.substring(text.indexOf("|",text.indexOf("tarihi"))+1);
            String[] tar= text.split("\\|");
            text= tar[0] + ","+ tar[1] + ","+ tar[2];

        }
        if (text.contains("(il)"))
        {
        text=text.substring(0,text.indexOf("(il)"));
        }
        if (text.contains("dosya:flag of"))
        {
            text= text.substring(0,text.indexOf("dosya:flag of"))+ text.substring(text.indexOf("px",text.indexOf(".svg"))+2);
        }

        text = removeRef(text);

        text=text.replace("(eyalet)","");
        text=text.replace("(şehir)","");
        text=text.replace("(birleşik krallık)","");

        text= text.replace("(ada)","");
        text= text.replace("(bugünkü","(");
        text= text.replace("(bugün","(");
        text= text.replace("(günümüzde","(");
        text= text.replace("şimdiki","");
        text=  text.replace("(bugün","(");
        text=  text.replace("(günümüz","(");
        text=  text.replace("yakınında","");
        text=  text.replace("yakınlarında","");
        text=  text.replace("veya",",");
        text=  text.replace("yakınları","");
        text= text.replace("<small>","");
        text= text.replace("</small>","");
        text= text.replace("<br/>",",");
        text= text.replace("<br />",",");
        text= text.replace("</br>",",");
        text= text.replace("null","");
        text=text.replace("[[","");
        text=text.replace("]]","");
        text=text.replace("}","");
        text=text.replace("\"","");
        text=text.replace("{","");
        text=text.replace("''","");
        text=text.replace("|",",");
        text= text.replace("(",",");
        text= text.replace(")",",");
        text= text.replace("/",",");
        text=text.trim();

        return text;
    }

    private String removeRef(String text) {
        if (text.contains("<ref"))
        {
            int refEnd=  text.indexOf("</",text.indexOf("<ref"));
            if (refEnd == -1)
                refEnd= text.indexOf("/>",text.indexOf("<ref"));
            text= text.substring(0,text.indexOf("<ref"))+ text.substring(text.indexOf('>',refEnd)+1);
            text=removeRef(text);
        }
        return text;

    }
}



