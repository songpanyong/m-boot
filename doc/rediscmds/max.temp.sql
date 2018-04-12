SELECT unix_timestamp(ifnull(max(updateTime), now(6)))
from #table;


