docker-machine create cas --driver virtualbox

mysql -uroot -proot -h `docker-machine ip cas` < bin/create-db.sql
mysql -uroot -proot -h `docker-machine ip cas` < bin/create-user.sql
mysql -ueric -peric5425 -h `docker-machine ip cas` < bin/baseline-db.sql