## Database configuration
1. First you need to have a **MySQL** database. 

1. You need to create the following _tables_ into this database.

    ```
    CREATE TABLE log_data (
        id INT not null auto_increment,
        execution_date datetime default null,
        ip varchar(15) default null,
        request varchar(200),
        status int,
        user_agent varchar(200),
        
        primary key (id)
    );

    CREATE TABLE ip_blocked (
        id INT not null auto_increment,
        ip varchar(15) default null,
        reason varchar(200),
        primary key (id)
    );

    ```
    > You can run the below script using the file _CREATE-TABLES.sql_

1. The default database connection properties are found in the _application.properties_ file.

    ```
    db-username=root
    db-password=mysql
    db-database=log_reader
    db-host=jdbc:mysql://localhost:3306
    ```
    If desired, it's possible to change these properties values according to your local database configuration, e.g. different username, password and database.

## Running the application

1. You can find the **.jar** file and all needed files and instructions inside the folder [App Final](app-final).

1. The log file named _access.log_ contains the example file to execute this test.

1. You should have java jdk 8, if not, please install it.

1. To run the application, you should run the below instruction, like asked by the file _Java_MySQL_Test_Instructions.txt_

    ```
    java -cp "log-parser-1.0-SNAPSHOT.jar" br.com.gabrielguimaraes.log.parser.LogParserApp --accesslog=access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500
    ```
    > This command will load all the _access.log_ file into the **MySQL** database and also will do the query to the database according to the _startDate_, _duration_ and _threshold_ parameters.

    > You can find the result of the parsed _access.log_ file in the table _log_data_.
    > You can also find the result of the query result in the table _ip_blocked_. This result will be the same as shown on the console after running the application for the first time.

## Answers for _Java_MySQL_Test_Instructions.txt_

### Deliverables
---
1. Java program that can be run from command line
    ```
    java -cp "log-parser-1.0-SNAPSHOT.jar" br.com.gabrielguimaraes.log.parser.LogParserApp --accesslog=access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500
    ```

    > If you just want to run the _blocked ip's query_, you should omit the parameter  _--accesslog_ and its value.

1. Source Code for the Java program
    
    The source code could be found at this _github_ repository 

1. MySQL schema used for the log data
    
    You can find at [Database Configuration](#database-configuration)

1. SQL queries for SQL test

    ```
    /* QUERY TO GET IP'S HOURLY */
    SELECT ip as ip, count(ip) as count FROM log_data
			WHERE execution_date BETWEEN '2017-01-01 00:00:00.000'
			AND DATE_ADD('2017-01-01 00:00:00.000', INTERVAL 1 HOUR)
	GROUP BY ip
	HAVING count(ip) >= 200;

    /* QUERY TO GET IP'S DAILY */
    SELECT ip as ip, count(ip) as count FROM log_data
			WHERE execution_date BETWEEN '2017-01-01.00:00:00'
			AND DATE_ADD('2017-01-01.00:00:00', INTERVAL 1 DAY)
	 GROUP BY ip
	HAVING count(ip) >= 500;
    ```
    > The application used the _java-time_ function to add '1 HOUR' and '1 DAY' to _startDate_ parameter. This methods were used instead of using _DATE_ADD_ **MySQL** function, however the result will be the same as the _SQL_ above.


