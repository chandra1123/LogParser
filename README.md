# LogParser
Java Tool that parses web server access log file, loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration.

## Usage
usage: java -cp "parser.jar" com.ef.Parser --accesslog <arg> --duration <arg> --startDate <arg> --threshold <arg>

--accesslog <arg>   path to access log

--duration <arg>    hourly or daily

--startDate <arg>   date in yyyy-MM-dd.HH:mm:ss format

--threshold <arg>   integer

## Contents

src - java code

db - database code
