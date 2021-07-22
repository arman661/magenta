CREATE DATABASE distance_calculator; 
update mysql.user set host = ‘%’ where user=’root’;
commit;
