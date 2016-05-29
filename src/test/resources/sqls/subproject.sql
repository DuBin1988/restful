select p.*, isnull(c.c, 0) size from(
	select * from t_project where f_parentid={id}
) p left join (
	select f_parentid, COUNT(*) c 
	from t_project
	group by f_parentid
) c on p.id=c.f_parentid