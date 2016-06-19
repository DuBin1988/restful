select p.*, isnull(c.c, 0) size from(
	select * from t_project where f_parentid is null
) p left join (
	{ this.call($项目汇总$, {groupName: $f_parentid$}) }
) c on p.id=c.f_parentid
order by p.id desc