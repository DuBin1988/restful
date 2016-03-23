select p.*, isnull(c.c, 0) size from(
select * from t_project where parentid={id}
) p left join (
select parentid, COUNT(*) c 
from t_project
group by parentid
) c on p.id=c.parentid