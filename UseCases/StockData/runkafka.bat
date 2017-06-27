cd D:\ShalajS\sw\kafka_2.10-0.10.1.1\bin\windows
d:

start cmd /c "zookeeper-server-start.bat ..\..\config\zookeeper.properties"
ping -n 15 127.0.0.1 > nul
start cmd /c "zookeeper-server-start.bat ..\..\config\zookeeper2.properties"
ping -n 15 127.0.0.1 > nul
start cmd /c "zookeeper-server-start.bat ..\..\config\zookeeper3.properties"

ping -n 15 127.0.0.1 > nul
start cmd /c "kafka-server-start.bat ..\..\config\server.properties"
ping -n 15 127.0.0.1 > nul
start cmd /c "kafka-server-start.bat ..\..\config\server2.properties"
ping -n 15 127.0.0.1 > nul
start cmd /c "kafka-server-start.bat ..\..\config\server3.properties"