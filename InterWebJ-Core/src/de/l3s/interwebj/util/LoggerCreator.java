package de.l3s.interwebj.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerCreator
{

    //	25-06-2011 18:21:39.239 Config.initLog4j() :: Logger initialized successfully

    static class DefaultFormatter extends SimpleFormatter
    {

	private static final DateFormat LOGGER_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

	@Override
	public String format(LogRecord record)
	{
	    StringBuilder sb = new StringBuilder();
	    sb.append(align(record.getLevel().getName(), 7));
	    sb.append(": [");
	    sb.append(LOGGER_DATE_FORMAT.format(new Date(record.getMillis())));
	    sb.append("] ");
	    sb.append(record.getSourceClassName());
	    sb.append(".");
	    sb.append(record.getSourceMethodName());
	    sb.append("() :: ");
	    sb.append(record.getMessage());
	    sb.append("\n");
	    return sb.toString();
	}

	private String align(String s, int length)
	{
	    StringBuilder sb = new StringBuilder(s);
	    int count = length - sb.length();
	    for(int i = 0; i < count; i++)
	    {
		sb.append(' ');
	    }
	    return sb.toString();
	}
    }

    public static Logger create(String name)
    {
	Logger logger = Logger.getLogger(name);
	logger.setLevel(Level.ALL);
	logger.setUseParentHandlers(false);
	Formatter formatter = new DefaultFormatter();
	Handler consoleHandler = new ConsoleHandler();
	consoleHandler.setLevel(Level.ALL);
	consoleHandler.setFormatter(formatter);
	logger.addHandler(consoleHandler);
	return logger;
    }
}
