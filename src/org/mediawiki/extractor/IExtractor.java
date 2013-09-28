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
      String son[]= text.substring(index).trim().split(" ");

        int basIndex = bas.length < 5 ? bas.length :5;
        int sonIndex = son.length < 5 ? son.length :5;
        summary +=  System.getProperty("line.separator");
    for ( int i=bas.length-basIndex; i<bas.length;i++)
    {
        summary += bas[i]+ " ";
    }
        summary += "<ENAMEX_TYPE=\""+label+"\">"+ son[0] + "</ENAMEX> ";
  //      summary +=  System.getProperty("line.separator");

        for ( int i=1; i<sonIndex;i++)
        {
            summary += son[i] + " ";
    //        summary +=  System.getProperty("line.separator");
        }
      }catch (Exception ex)
      {
          String hebee="";
      }
        summary +=  System.getProperty("line.separator");
     return summary;
    }
}
