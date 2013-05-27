SELECT p_dogum_yer FROM wikidb.template;
SELECT * FROM wikidb.template;
select * from text where old_id='10928718'
select * from infobox where rev_id='10466034'

SELECT p_dogum_yer FROM wikidb.infobox where p_dogum_yer is not null and p_dogum_yer!='null';



#Doğru bulunanlar
select ib.p_dogum_yer info ,t.p_dogum_yer template from  template t,infobox ib 
where  ib.rev_id= t.rev_id
and t.p_dogum_yer!="null" and t.p_dogum_yer !="null" and t.p_dogum_yer !=""
and ib.p_dogum_yer LIKE  CONCAT('%',SUBSTRING(t.p_dogum_yer,1,5),'%') COLLATE utf8_unicode_ci

#Doğru bulunanlar sayısı
select ib.p_dogum_yer info ,t.p_dogum_yer template from  template t,infobox ib 
where  ib.rev_id= t.rev_id
and t.p_dogum_yer!="null" and t.p_dogum_yer !="null" and t.p_dogum_yer !=""
and ib.p_dogum_yer LIKE  CONCAT('%',SUBSTRING(t.p_dogum_yer,1,5),'%') COLLATE utf8_unicode_ci

#infoda değeri olmayanlar
select count(*) from  infobox ib 
where  ib.p_dogum_yer = "null" or ib.p_dogum_yer is null or ib.p_dogum_yer=""


#templatede değeri çıkartılmayanlar
select count(*) from  template t 
where  t.p_dogum_yer = "null" or t.p_dogum_yer is null or t.p_dogum_yer=""


#Hem infoda hem templatede değeri olmayanlar
select count(*) from  infobox ib ,template t 
where  (ib.p_dogum_yer = "null" or ib.p_dogum_yer is null or ib.p_dogum_yer="")
and ( t.p_dogum_yer = "null" or t.p_dogum_yer is null or t.p_dogum_yer="") 
and ib.rev_id= t.rev_id

#infoda değeri olup templatede bulunamayanlar
select ib.rev_id,ib.p_dogum_yer info ,t.p_dogum_yer template from  template t ,infobox ib 
where  (t.p_dogum_yer = "null" or t.p_dogum_yer is null or t.p_dogum_yer="")
and  ib.p_dogum_yer != "null" and ib.p_dogum_yer is not null and ib.p_dogum_yer!=""
and   ib.rev_id= t.rev_id


#infoda bulunup templatede de bulunan ancak eşleşmeyenler
select ib.rev_id,ib.p_dogum_yer info ,t.p_dogum_yer template from  template t ,infobox ib 
where  (t.p_dogum_yer = "null" or t.p_dogum_yer is null)
and  ib.p_dogum_yer != "null" and ib.p_dogum_yer is not null and ib.p_dogum_yer!=""
and   t.p_dogum_yer != "null" and t.p_dogum_yer is not null and t.p_dogum_yer!=""
and ib.p_dogum_yer NOT LIKE  CONCAT('%',SUBSTRING(t.p_dogum_yer,1,5),'%') COLLATE utf8_unicode_ci
and   ib.rev_id= t.rev_id




#infoda değeri olup templatede bulunamayanlar detay
select ib.rev_id,t.p_ad,ib.p_dogum_yer info ,SUBSTRING(te.old_text,1,4000),t.p_dogum_yer template from  template t ,infobox ib, text te 
where  (t.p_dogum_yer = "null" or t.p_dogum_yer is null or t.p_dogum_yer="")
and  ib.p_dogum_yer != "null" and ib.p_dogum_yer is not null and ib.p_dogum_yer!=""
and   ib.rev_id= t.rev_id and te.old_id= t.rev_id

#templatede değeri olup infoda bulunamayanlar detay
select ib.rev_id,t.p_ad,ib.p_dogum_yer info ,t.p_dogum_yer template,ib.infobox, SUBSTRING(te.old_text,1,4000) from  template t ,infobox ib, text te 
where  (ib.p_dogum_yer = "null" or ib.p_dogum_yer is null or ib.p_dogum_yer="")
and  t.p_dogum_yer != "null" and t.p_dogum_yer is not null and t.p_dogum_yer!=""
and   ib.rev_id= t.rev_id and te.old_id= t.rev_id
