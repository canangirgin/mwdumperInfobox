package org.mediawiki.extractor;

import java.sql.SQLException;

public abstract class  IExtractor {

   String text=null ;
   abstract String Extract(Template template) throws SQLException;

    protected String GetSummary(String value,String label, int index)
    {
        String summary="";
      try{
      String bas[]= text.substring(0,index).split(" ");
      String son[]= text.substring(index+ value.length()+2).split(" ");


        int basIndex = bas.length < 10 ? bas.length :10;
        int sonIndex = son.length < 10 ? son.length :10;
        summary +=  System.getProperty("line.separator");
    for ( int i=bas.length-basIndex; i<bas.length;i++)
    {
        summary += bas[i];
        summary +=  System.getProperty("line.separator");
    }
        summary += value + "["+ label+"]";
        summary +=  System.getProperty("line.separator");

        for ( int i=0; i<sonIndex;i++)
        {
            summary += son[i];
            summary +=  System.getProperty("line.separator");
        }
      }catch (Exception ex)
      {
          String hebee="";
      }
     return summary;
    }
}
