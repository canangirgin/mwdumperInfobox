package org.mediawiki.importer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: hs
 * Date: 16.04.2013
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class InfoBoxConst {
    public static ArrayList<KeyValue> PropetyBulunacak =new ArrayList<KeyValue>();
    static {
        PropetyBulunacak.add(new KeyValue("p_ad", Arrays.asList("ad","isim","name")));
    }

}

