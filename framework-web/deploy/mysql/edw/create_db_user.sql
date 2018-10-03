CREATE USER 'root'@'%' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
CREATE DATABASE IF NOT EXISTS `edw_small`;
CREATE USER 'inferyx'@'localhost' identified by 'inferyx';
GRANT ALL PRIVILEGES ON edw_small.* TO 'inferyx'@'localhost';
FLUSH PRIVILEGES;