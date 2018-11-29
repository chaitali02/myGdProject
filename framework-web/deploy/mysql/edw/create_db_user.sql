create user 'root'@'%' identified by 'root';
grant all privileges on *.* to 'root'@'%' with grant option;
create database if not exists `edw_small`;
create user 'inferyx'@'localhost' identified by 'inferyx';
grant all privileges on edw_small.* to 'inferyx'@'localhost';
flush privileges;