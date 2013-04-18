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
        //PropetyBulunacak.add(new KeyValue("p_meslek", Arrays.asList("mesle")));
        PropetyBulunacak.add(new KeyValue("p_dogum_yer", Arrays.asList("doğum_yer","doğduğuşehir","doğum yer","yer","doğum/2/1")));
       // PropetyBulunacak.add(new KeyValue("p_dogum_ulke", Arrays.asList("doğduğuülke","doğum/2/2")));

         //   PropetyBulunacak.add(new KeyValue("p_dogum_tar", Arrays.asList("doğumtarih","doğum_tarih","doğum/1/1")));

    }

}

