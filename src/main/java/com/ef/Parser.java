package com.ef;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.ef.dao.AccessLogDao;
import com.ef.dao.BlockedIPDao;
import com.ef.dao.impl.AccessLogDaoImpl;
import com.ef.dao.impl.BlockedIPDAOImpl;
import com.ef.dao.impl.MySQLDataSourceFactoryImpl;
import com.ef.domain.AccessLog;
import com.ef.domain.BlockedIP;
import com.opencsv.bean.CsvToBeanBuilder;

public class Parser {

    private String accesslog;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String duration;
    private Integer threshold;
    private AccessLogDao accessLogDao;
    private BlockedIPDao blockedIPDao;

    private Parser(String accesslog, LocalDateTime startDate, LocalDateTime endDate, String duration, Integer threshold) throws Exception {
        this.accesslog = accesslog;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.threshold = threshold;
        DataSource dataSource = new MySQLDataSourceFactoryImpl().getDataSource();
        accessLogDao = new AccessLogDaoImpl(dataSource);
        blockedIPDao = new BlockedIPDAOImpl(dataSource);
    }

    public static void main(String[] args) {
        Parser parser = parseArgs(args);
        if (parser == null) {
            return;
        }

        try {
            parser.loadDataBatch();
        } catch (Exception e) {
            System.err.println("Exception occurred while loading data.");
            e.printStackTrace();
        }
    }

    private static Parser parseArgs(String[] args) {
        Parser parser = null;
        Options options = getCommandOptions();
        String accesslog = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        String duration = null;
        Integer threshold = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
        boolean isValidCommand = true;
        try {
            CommandLineParser commandLineParser = new DefaultParser();
            CommandLine line = commandLineParser.parse(options, args);
            accesslog = line.getOptionValue("accesslog");
            String startDateStr = line.getOptionValue("startDate");
            duration = line.getOptionValue("duration");
            threshold = Integer.parseInt(line.getOptionValue("threshold"));

            if (accesslog == null || accesslog.isEmpty()) {
                System.err.println("accesslog path is required");
                isValidCommand = false;
            }

            startDate = LocalDateTime.parse(startDateStr, dateFormatter);

            if ("hourly".equals(duration)) {
                endDate = startDate.plusHours(1);
            } else if ("daily".equals(duration)) {
                endDate = startDate.plusHours(24);
            } else {
                System.err.println("duration should be hourly or daily");
                isValidCommand = false;
            }

        } catch (Exception e) {
            isValidCommand = false;
            System.err.println("Exception ocurred while parsing arguments");
            e.printStackTrace();
            

        }
        
        if (isValidCommand) {
            try {
                parser = new Parser(accesslog, startDate, endDate, duration, threshold);
            } catch (Exception e) {
                System.err.println("Exception occurred while initializing Parser");
                e.printStackTrace();
            }
        } else {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -cp \"parser.jar\" com.ef.Parser", options, true);
        }
        return parser;
    }

    private static Options getCommandOptions() {
        Options options = new Options();
        options.addOption(Option.builder()
            .longOpt("accesslog")
            .hasArgs()
            .required()
            .desc("path to access log")
            .build());
        options.addOption(Option.builder()
            .longOpt("startDate")
            .hasArgs()
            .required()
            .desc("date in yyyy-MM-dd.HH:mm:ss format")
            .build());
        options.addOption(Option.builder()
            .longOpt("duration")
            .hasArgs()
            .required()
            .desc("hourly or daily")
            .build());
        options.addOption(Option.builder()
            .longOpt("threshold")
            .hasArgs()
            .required()
            .desc("integer")
            .build());
        return options;
    }

    public void loadData() throws IOException {
        Reader reader = new FileReader(this.accesslog);
        new CsvToBeanBuilder<AccessLog>(reader).withSeparator('|')
            .withType(AccessLog.class)
            .build()
            .forEach(accessLog -> {
                try {
                    accessLogDao.insertAccessLog(accessLog);
                } catch (Exception e) {
                    System.err.println("Exception occurred while inserting AccessLog");
                    e.printStackTrace();
                }
            });
        ;
    }

    public void loadDataBatch() throws Exception {
        accessLogDao.clearAccessLogs();

        Reader reader = new FileReader(this.accesslog);
        Iterator<AccessLog> accesslogIterator = new CsvToBeanBuilder<AccessLog>(reader).withSeparator('|')
            .withType(AccessLog.class)
            .build()
            .iterator();

        final int BATCH_SIZE = 5000;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Map<String, Integer> requestCount = new HashMap<>();
        List<AccessLog> accesslogList = new ArrayList<>(BATCH_SIZE);
        List<String> blockedIp = new ArrayList<>();
        while (accesslogIterator.hasNext()) {
            AccessLog accesslog = accesslogIterator.next();
            String ip = accesslog.getIp();
            String datestr = accesslog.getDate();
            try {
                LocalDateTime date = LocalDateTime.parse(datestr, dateFormatter);
                if (date.isBefore(startDate) || date.isAfter(endDate)) {
                    // skip
                } else {
                    Integer count = requestCount.get(ip);
                    if (count == null) {
                        count = 0;
                    }
                    count++;
                    if (count == threshold) {
                        blockedIp.add(ip);
                    }
                    requestCount.put(ip, count);

                }
            } catch (DateTimeParseException e) {

            }

            accesslogList.add(accesslog);
            if (accesslogList.size() >= BATCH_SIZE) {
                try {
                    accessLogDao.insertAccessLogList(accesslogList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                accesslogList.clear();
            }
        }

        if (!accesslogList.isEmpty()) {
            try {
                accessLogDao.insertAccessLogList(accesslogList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Blocked IP");
        for (String ip : blockedIp) {
            System.out.println(ip);
            Integer count = requestCount.get(ip);
            String reason = "Between " + this.startDate + " and " + this.endDate + ", requests = " + count + ", threshold = " + threshold + ", duration = " + duration;
            try {
                blockedIPDao.insertBlockedIP(new BlockedIP(ip, reason));
            } catch (Exception e) {
                System.err.println("Error occurred while inserting into BlockedIP");
                e.printStackTrace();
            }
        }

    }
}
