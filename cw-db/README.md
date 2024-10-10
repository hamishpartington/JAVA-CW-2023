# SQL Server and Client

This was the first summative coursework for my Java module for my MSc. The task was to write a SQL server and client. I received a distinction for this coursework with a mark of 82

To use:
* Make sure you have a Java SDK installed ([instructions](https://www.golinuxcloud.com/install-java-linux-windows-mac/))
* Clone the repo
* Open your terminal and navigate to the `cw-db` directory
* Compile by running `./mvnw clean compile`
* Run the server with `./mvnw exec:java@server`
* Open a new terminal window and run the client with `./mvnw exec:java@client`
* Enter SQL commands in the client. The grammar is slightly reduced from standard SQL. The BNF is in `grammar.txt`
