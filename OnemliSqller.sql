SELECT p_dogum_yer FROM wikidb.template;
select * from text where old_id='10928718'

SELECT p_dogum_yer FROM wikidb.infobox where p_dogum_yer is not null and p_dogum_yer!='null';

#Doğru bulunanlar
select SUBSTRING(t.p_dogum_yer,1,5),INSTR(ib.p_dogum_yer, CONCAT('%',SUBSTRING(t.p_dogum_yer,1,5),'%') >0),
t.rev_id,ib.p_dogum_yer info, t.p_dogum_yer template from template t,infobox ib 
where INSTR(ib.p_dogum_yer, CONCAT('%',SUBSTRING(t.p_dogum_yer,1,5),'%') >0) 
and t.p_dogum_yer!="null" and ib.p_dogum_yer !="null"

#Doğru bulunanlar
select ib.p_dogum_yer info ,t.p_dogum_yer template from  template t,infobox ib 
where  ib.rev_id= t.rev_id
and t.p_dogum_yer!="null" and ib.p_dogum_yer !="null"
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
where  (t.p_dogum_yer = "null" or t.p_dogum_yer is null)
and  ib.p_dogum_yer != "null" and ib.p_dogum_yer is not null and ib.p_dogum_yer!=""
and   ib.rev_id= t.rev_id
