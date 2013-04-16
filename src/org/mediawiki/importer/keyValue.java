package org.mediawiki.importer;

import java.util.ArrayList;
import java.util.List;

public class KeyValue
{
   public String dbKey;
   public List<String> values= new ArrayList<String>();


   public KeyValue(String p_ad, List<String> strings) {
   dbKey= p_ad;
   values= strings;
    }
}
