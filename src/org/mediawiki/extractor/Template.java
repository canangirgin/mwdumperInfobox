package org.mediawiki.extractor;

import org.mediawiki.importer.KeyValue;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 13.05.2013
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */



public class Template {

    public int revisionId;
    public HashMap<Object, Object> Propetys;
    public String text;
    public String name;
    public Template() {
        Propetys = new HashMap<Object, Object>();
        for(KeyValue bulunacak: InfoBoxConst.PropetyBulunacak)
        {
            Propetys.put(bulunacak.dbKey,null);
        }

    }

}
