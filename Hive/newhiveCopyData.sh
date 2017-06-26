#!/bin/bash

dt=`date '+%Y-%m-%d'`
isExternal=0
isPartition=0

logFile=hiveCopyLog_$dt.log

source config.properties

touch $logFile
hive -S -e "use $SOURCE_SCHEMA;show tables;"> all_tables.txt

echo -e "\n******************************************************************************************" >> "$logFile"
ssh $DEST_USER_NAME@$DEST_IP "hive -S -e 'CREATE DATABASE IF NOT EXISTS $DEST_SCHEMA;'" >> "$logFile"

echo "`date '+%Y-%m-%d %H:%M:%S'` schema $DEST_SCHEMA created at destinatation hive cluster: $DEST_IP" |tee -a  "$logFile"

for tablename in $(cat all_tables.txt) ;
do
        echo -e "\n---------------------------------------$tablename <<Start>>----------------------------------------------" >> "$logFile"
		hive -S -e "show partitions $SOURCE_SCHEMA.$tablename;" > partition.txt 2>/dev/null
        lineCount=`cat partition.txt | wc -l`
        if [ $lineCount -eq 0 ]; then
                isPartition=0
        else
                isPartition=1
        fi
        isExternal=0
        hive -S -e "show create table $SOURCE_SCHEMA.$tablename" > my_table_script.txt
        if grep  -iFq "CREATE EXTERNAL TABLE " my_table_script.txt
        then
                echo "`date '+%Y-%m-%d %H:%M:%S'` $SOURCE_SCHEMA.$tablename is an External Table"|tee -a  "$logFile"
                isExternal=1
        else
                isExternal=0
                echo "`date '+%Y-%m-%d %H:%M:%S'` $SOURCE_SCHEMA.$tablename is a Manage Table"|tee -a  "$logFile"
        fi
    if [ $isPartition -eq 1 ]; then
		# convert the part_col1=part_val1/part_col2=part_val2 to partition (part_col1="part_val1",part_col2="part_val2")
        sed  -e 's/\//,/g' -e 's/=\([^,]*\)/="\1"/g' -e 's/^/partition (/g' -e 's/$/)/g' partition.txt > partition1.txt

        while read partition; do
            echo "EXPORT TABLE $SOURCE_SCHEMA.$tablename $partition  TO '$SOURCE_HDFS_PATH/$tablename';" | hive >>"$logFile" 2>&1
			if [ $? -eq 0 ]; then
					 echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Export table : $tablename for partition $partition" |tee -a "$logFile"
			else
					echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Some issue occurs in Exporting table : $tablename for partition $partition, Please check the log file "|tee -a  "$logFile"

			fi
            echo -e "\nhadoop distcp -overwrite $SOURCE_HDFS_PATH/$tablename $DEST_HDFS_PATH/$tablename"|tee -a  "$logFile"
			hadoop distcp -overwrite $SOURCE_HDFS_PATH/$tablename $DEST_HDFS_PATH/$tablename >> "$logFile" 2>&1
			if [ $? -eq 0 ]; then
					echo "`date '+%Y-%m-%d %H:%M:%S'` Copy data to $DEST_HDFS_PATH for table : $tablename and partition $partition completed"|tee -a "$logFile"
			else
					echo "`date '+%Y-%m-%d %H:%M:%S'` Some issue occurs in distcp for table : $tablename and partition $partition,Please check the log file"|tee -a "$logFile"

			fi
			if [ $isExternal -eq 0 ]; then
			   ssh -n $DEST_USER_NAME@$DEST_IP "echo \"IMPORT TABLE $DEST_SCHEMA.$tablename FROM '$DEST_HDFS_PATH/$tablename';\" | hive" >> "$logFile" 2>&1
			else
					  ssh -n $DEST_USER_NAME@$DEST_IP "echo \"IMPORT EXTERNAL TABLE $DEST_SCHEMA.$tablename FROM '$DEST_HDFS_PATH/$tablename';\" | hive" >> "$logFile" 2>&1
			fi
			if [ $? -eq 0 ]; then
					 echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Table $tablename and partition $partition successfully imported into destination schema $DEST_SCHEMA" |tee -a "$logFile"
			else
					echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Some issue occurs in Importing table : $tablename and partition $partition,Please check the log file "|tee -a "$logFile"
			fi
             hdfs dfs -rm -r -f $SOURCE_HDFS_PATH/$tablename
        done < partition1.txt
	else
			echo "EXPORT TABLE $SOURCE_SCHEMA.$tablename TO '$SOURCE_HDFS_PATH/$tablename';" | hive >>"$logFile" 2>&1
			if [ $? -eq 0 ]; then
					 echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Export table : $tablename" |tee -a "$logFile"
			else
					echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Some issue occurs in Exporting table : $tablename Please check the log file "|tee -a  "$logFile"

			fi
			echo -e "\nhadoop distcp -overwrite $SOURCE_HDFS_PATH/$tablename $DEST_HDFS_PATH/$tablename"|tee -a  "$logFile"
			hadoop distcp -overwrite $SOURCE_HDFS_PATH/$tablename $DEST_HDFS_PATH/$tablename >> "$logFile" 2>&1
			if [ $? -eq 0 ]; then
					echo "`date '+%Y-%m-%d %H:%M:%S'` Copy data to $DEST_HDFS_PATH for table : $tablename completed"|tee -a "$logFile"
			else
					echo "`date '+%Y-%m-%d %H:%M:%S'` Some issue occurs in distcp for table : $tablename Please check the log file"|tee -a "$logFile"

			fi


			if [ $isExternal -eq 0 ]; then
					   ssh $DEST_USER_NAME@$DEST_IP "echo \"IMPORT TABLE $DEST_SCHEMA.$tablename FROM '$DEST_HDFS_PATH/$tablename';\" | hive" >> "$logFile" 2>&1
			else
					  ssh $DEST_USER_NAME@$DEST_IP "echo \"IMPORT EXTERNAL TABLE $DEST_SCHEMA.$tablename FROM '$DEST_HDFS_PATH/$tablename';\" | hive" >> "$logFile" 2>&1
			fi
			if [ $? -eq 0 ]; then
					 echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Table $tablename successfully imported into destination schema $DEST_SCHEMA" |tee -a "$logFile"
			else
					echo -e "\n`date '+%Y-%m-%d %H:%M:%S'` Some issue occurs in Importing table : $tablename Please check the log file "|tee -a "$logFile"
			fi
	fi
			echo "---------------------------------------$tablename <<End>>----------------------------------------------" >> "$logFile"

done
rm -f all_tables.txt my_table_script.txt partition.txt partition1.txt
echo "******************************************************************************************">> "$logFile"