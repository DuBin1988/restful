select p.*, isnull(c.c, 0) size from(
	select * from t_task where f_parentid={id}
) p left join (
	select f_parentid, COUNT(*) c 
	from t_task
	group by f_parentid
) c on p.id=c.f_parentid
order by p.id desc
