#/root/training/data/emp.data

1201,Gopal,45000,Technical manager,Engineering
1202,Manisha,45000,Senior Developer,Engineering
1203,Amit,4000,Technical Writer,Engineering
1204,Kiran,40000,HR Admin,HR
1205,Keshav,30000,Op Admin,Admin
1206,Raghav,50000,Sales Manager,Sales
1207,Udit,46000,Finance Admin,Finance
1208,Shyam,35000,Software Enginner,Engineering
1209,Radhika,70000,HR Head,HR
1210,Abhinav,36000,Op Admin,Admin
----------------------------------------------------------------------------------------------------------
CREATE DATABASE training;
----------------------------------------------------------------------------------------------------------
USE training;
----------------------------------------------------------------------------------------------------------
CREATE TABLE employee(empid int,name string,salary int,designation string,department string) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';
----------------------------------------------------------------------------------------------------------
DESCRIBE  employee;
DESCRIBE FORMATTED employee;
----------------------------------------------------------------------------------------------------------
LOAD DATA LOCAL INPATH '/root/training/data/emp.data' INTO TABLE employee;
----------------------------------------------------------------------------------------------------------
--check if file exist in default hive warehouse location

hdfs dfs -ls /user/hive/warehouse/training.db/employee
 ----------------------------------------------------------------------------------------------------------
select * from employee;
----------------------------------------------------------------------------------------------------------
LOAD DATA LOCAL INPATH '/root/training/data/emp.data' OVERWRITE INTO TABLE employee;
----------------------------------------------------------------------------------------------------------
select * from employee;
----------------------------------------------------------------------------------------------------------
hdfs dfs -put /root/training/data/emp.data /training/input
----------------------------------------------------------------------------------------------------------
LOAD DATA INPATH '/training/input/emp.data' OVERWRITE INTO TABLE employee;
----------------------------------------------------------------------------------------------------------
--create folder on hdfs
hdfs dfs -mkdir -p /training/input/employee
----------------------------------------------------------------------------------------------------------
hdfs dfs -put /root/training/data/emp.data /training/input/employee
----------------------------------------------------------------------------------------------------------
CREATE EXTERNAL TABLE emp_ext (empid int,name string,salary int,designation string,department string) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','
LOCATION '/training/input/employee';
----------------------------------------------------------------------------------------------------------
select * from emp_ext;
----------------------------------------------------------------------------------------------------------
--drop external table and check if file exist 

DROP TABLE emp_ext;
----------------------------------------------------------------------------------------------------------
hdfs dfs -ls /training/input/employee
----------------------------------------------------------------------------------------------------------------------
--now drop internal table and check if file exist
DROP TABLE employee;
hdfs dfs -ls /user/hive/warehouse/training.db/
---------------------------------------------------------------------------------------------------------------------
-- create partition table
CREATE TABLE emp_part(empid int,name string,salary int,designation string) 
PARTITIONED BY(department STRING)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';

# /root/training/data/emp_engineering.data
1201,Gopal,45000,Technical manager
1202,Manisha,45000,Senior Developer
1203,Amit,40000,Technical Writer
1208,Shyam,35000,Software Enginner

# /root/training/data/emp_admin.data
1205,Keshav,30000,Op Admin
1210,Abhinav,36000,Op Admin

# /root/training/data/emp_hr.data
1204,Kiran,40000,HR Admin
1209,Radhika,70000,HR Head
-- Static partition
LOAD DATA LOCAL INPATH '/root/training/data/emp_engineering.data' INTO TABLE emp_part PARTITION(department="Engineering");
LOAD DATA LOCAL INPATH '/root/training/data/emp_admin.data' INTO TABLE emp_part PARTITION(department="Admin");
LOAD DATA LOCAL INPATH '/root/training/data/emp_hr.data' INTO TABLE emp_part PARTITION(department="HR");
------------------------------------------------------------------------------------------------------------------------
-- load data from other table
CREATE TABLE employee(empid int,name string,salary int,designation string,department string) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/root/training/data/emp.data' INTO TABLE employee;

--static partition
INSERT OVERWRITE TABLE emp_part PARTITION (department="Engineering") SELECT empid,name,salary,designation FROM employee where department='Engineering';

--dynamic partition
set hive.exec.dynamic.partition = true;
set hive.exec.dynamic.partition.mode = nonstrict;

INSERT OVERWRITE TABLE emp_part PARTITION(department) SELECT empid,name,salary,designation,department FROM employee;

-- check folder location in hdfs
hdfs dfs -ls /user/hive/warehouse/training.db/emp_part
-----------------------------------------------------------------------------------------------------------------------------
-- external partition table 
hdfs dfs -mkdir -p /training/input/emp/department=HR

hdfs dfs -put /root/training/data/emp_hr.data  /training/input/emp/department=HR

CREATE EXTERNAL TABLE emp_ext (empid int,name string,salary int,designation string) 
PARTITIONED BY (department string)
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ','
LOCATION '/training/input/emp';

-- you will not find any data because we havent added partition
select * from emp_ext;

-- add partition
alter table add partition (department='HR')
-- or you can use MSCK (Meta Store Check) command to add all partition in meta data
MSCK REPAIR TABLE emp_ext

select * from emp_ext;
-----------------------------------------------------------------------------------------------------------------------------
-- drop partition
ALTER TABLE emp_part DROP PARTITION (department="Engineering");

-- check folder location in hdfs
hdfs dfs -ls /user/hive/warehouse/training.db/emp_part

-----------------------------------------------------------------------------------------------------------------------------

-- create bucketed table
CREATE TABLE emp_bucket(empid int,name string,salary int,designation string,department string) 
CLUSTERED BY (empid) INTO 4 BUCKETS;

-- make sure to SET hive.enforce.bucketing = TRUE , other wise it will not create specified buckets
SET hive.enforce.bucketing = TRUE

INSERT INTO TABLE emp_bucket  SELECT * FROM employee;

--check number of files created
hdfs dfs -ls /user/hive/warehouse/training.db/emp_bucket
-----------------------------------------------------------------------------------------------------------------------------
CREATE TABLE emp_part_bucket(empid int,name string,salary int,designation string) 
PARTITIONED BY (department string)
CLUSTERED BY (empid) INTO 4 BUCKETS;

INSERT INTO TABLE emp_part_bucket PARTITION(department) SELECT * FROM employee;

hdfs dfs -ls /user/hive/warehouse/training.db/emp_part_bucket
-----------------------------------------------------------------------------------------------------------------------------
-- UDF
add jar /root/training/jars/MYUpper.jar;

CREATE TEMPORARY FUNCTION myUpper AS 'org.cyb.UDF.MyUpper';
--or to make it permanent function put jar file in hdfs
CREATE FUNCTION myUpper  AS 'com.hive.udf.MyUpper' USING JAR 'hdfs://172.27.155.92:8020/user/hive/udf/MYUpper.jar';

SELECT MYUPPER(name) FROM employee;
-----------------------------------------------------------------------------------------------------------------------------
-- JSON Serde ---

{"DocId":"ABC","User":{"Id":1234,"Username":"sam1234","Name":"Sam","ShippingAddress":{"Address1":"123 Main St.","Address2":"","City":"Durham","State":"NC"},"Orders":[{"ItemId":6789,"OrderDate":"11/11/2012"},{"ItemId":4352,"OrderDate":"12/12/2012"}]}}

add jar /root/training/jars/json-serde-1.3.6-jar-with-dependencies.jar;

CREATE TABLE complex_json (
  DocId string,
  User struct<Id:int,
              Username:string,
              Name: string,
              ShippingAddress:struct<Address1:string,
                                     Address2:string,
                                     City:string,
                                     State:string>,
              Orders:array<struct<ItemId:int,
                                  OrderDate:string>>>
)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe';

LOAD DATA LOCAL INPATH  '/root/training/data/complex.json' INTO TABLE complex_json;

SELECT DocId, User.Id, User.ShippingAddress.City as city,
       User.Orders[0].ItemId as order0id,
       User.Orders[1].ItemId as order1id
FROM complex_json;
-----------------------------------------------------------------------------------------------------------------------------
--hive batch mode commands

hive -e "use training;select * from employee;"
hive -S -e "use training;select * from employee;"
hive -f /root/training/hive_script.hql

-----------------------------------------------------------------------------------------------------------------------------
--connect with beeline
beeline -u jdbc:hive2://mac53:10000
-----------------------------------------------------------------------------------------------------------------------------
CREATE TABLE users ( 
name STRING, 
salary FLOAT, 
subordinates ARRAY<STRING>, 
deductions MAP<STRING, FLOAT>, 
address STRUCT<street:STRING, city:STRING, state:STRING, zip:INT> 
) 
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY ',' 
COLLECTION ITEMS TERMINATED BY '#' 
MAP KEYS TERMINATED BY '|' 
LINES TERMINATED BY '\n' 
STORED AS TEXTFILE;

users.data
a,1000,a1#a2#a3,tds|20#pt|10,st1#pune#mah#11111
b,2000,b1#b2#b3,tds|20#pt|10,st1#pune#mah#22222
c,3000,c1#c2#c3,tds|20#pt|10,st1#pune#mah#33333
d,4000,d1#d2#d3,tds|20#pt|10,st1#pune#mah#44444

LOAD DATA LOCAL INPATH  '/root/training/data/users.data' INTO TABLE users;

select * from users;
select subordinates[0], deductions["tds"], address.zip from users;
-----------------------------------------------------------------------------------------------------------------------------
DROP DATABASE IF EXISTS training CASCADE;
-----------------------------------------------------------------------------------------------------------------------------




