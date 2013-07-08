package org.mediawiki.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        PropetyBulunacak.add(new KeyValue("p_dogum_yer", Arrays.asList("doğum_yer","doğduğuşehir","doğumyeri","doğum yeri","doğum yer","doğum_yeri","birth place","birthplace","birth_place","yer","doğum/2/1","doğduğuülke")));
        PropetyBulunacak.add(new KeyValue("p_olum_yer", Arrays.asList("ölümyeri","ölüm_yeri","ölüm yeri","death_place","deathplace","death place","ölüm/2/1")));
        PropetyBulunacak.add(new KeyValue("p_olum_tar", Arrays.asList("ölümtarihi","ölüm_tarihi","ölüm tarihi","death_date","death date","deathdate","ölüm tarihi ve yaşı","ölüm yılı ve yaşı","ölüm/1")));
        PropetyBulunacak.add(new KeyValue("p_dogum_tar", Arrays.asList("doğumtarih","doğumtarihi","doğum_tarih","doğum tarihi","doğum tarih","birth_date","birthdate","birth date","born","doğum tarihi ve yaşı","doğum yılı ve yaşı","doğum/1")));
    }
    public static List<String> infoboxFilter =  Arrays.asList("Asker bilgi kutusu", "Astronot bilgi kutusu", "Basketbolcu bilgi kutusu",
            "Beyzbolcu bilgi kutusu",
            "Bilardo oyuncusu bilgi kutusu",
            "Bilim adamı bilgi kutusu",
            "Bisikletçi bilgi kutusu",
            "Boksör bilgi kutusu",
            "Buz patencisi bilgi kutusu",
            "Casus bilgi kutusu",
            "F1 Sürücü Kaydı",
            "Filozof bilgi kutusu",
            "Futbolcu bilgi kutusu",
            "Gazeteci bilgi kutusu",
            "Go oyuncusu",
            "Golf oyuncusu bilgi kutusu",
            "Güreşçi bilgi kutusu",
            "Hakem bilgi kutusu",
            "Halife bilgi kutusu",
            "Hanedan bilgi kutusu",
            "Hükümdar bilgi kutusu",
            "Jimnastikçi bilgi kutusu",
            "Kişi bilgi kutusu",
            "Kişi künyesi",
            "Komedyen bilgi kutusu",
            "Korsan bilgi kutusu",
            "Kraliyet bilgi kutusu",
            "Makam sahibi bilgi kutusu",
            "Manken bilgi kutusu",
            "Mimar bilgi kutusu",
            "Motosiklet sürücüsü bilgi kutusu",
            "Müslüman bilgin bilgi kutusu",
            "Müzik sanatçısı bilgi kutusu",
            "NBA oyuncusu bilgi kutusu",
            "NHL oyuncusu bilgi kutusu",
            "Oyuncu bilgi kutusu",
            "Porno yıldızı bilgi kutusu",
            "Red Bull Air Race pilotu bilgi kutusu",
            "Sanatçı bilgi kutusu",
            "Satranç oyuncusu bilgi kutusu",
            "Seri katil bilgi kutusu",
            "Snooker Oyuncusu Bilgi Kutusu",
            "Sporcu bilgi kutusu",
            "Sunucu bilgi kutusu",
            "Süpersport sürücüsü",
            "Tarihçi bilgi kutusu",
            "Tenisçi bilgi kutusu",
            "Türkücü bilgi kutusu",
            "Voleybolcu bilgi kutusu",
            "WRC yarışçısı bilgi kutusu",
            "Yazar bilgi kutusu",
            "Yüzücü bilgi kutusu",
            "Çizgi roman yaratıcısı bilgi kutusu",
            "İnternet ünlüsü bilgi kutusu");

}

