SELECT IP, COUNT(*) FROM access_log
WHERE access_date between STR_TO_DATE('2017-01-01.00:00:00', '%Y-%m-%d.%H:%i:%s') AND STR_TO_DATE('2017-01-01.01:00:00', '%Y-%m-%d.%H:%i:%s')
GROUP BY IP
HAVING COUNT(*) > 100;

SELECT REQUEST FROM access_log
WHERE IP = '192.168.234.82';