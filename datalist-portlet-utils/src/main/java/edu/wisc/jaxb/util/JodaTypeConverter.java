package edu.wisc.jaxb.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

public class JodaTypeConverter {
    public static String printDate(DateMidnight val) {
        final GregorianCalendar cal = val.toGregorianCalendar();
        return DatatypeConverter.printDate(cal);
    }

    public static String printDateTime(DateTime val) {
        final GregorianCalendar cal = val.toGregorianCalendar();
        return DatatypeConverter.printDateTime(cal);
    }

    public static String printTime(DateTime val) {
        final GregorianCalendar cal = val.toGregorianCalendar();
        return DatatypeConverter.printTime(cal);
    }

    public static DateMidnight parseDate(String lexicalXSDDate) {
        final Calendar cal = DatatypeConverter.parseDate(lexicalXSDDate);
        return new DateMidnight(cal);
    }

    public static DateTime parseDateTime(String lexicalXSDDateTime) {
        final Calendar cal = DatatypeConverter.parseDateTime(lexicalXSDDateTime);
        return new DateTime(cal);
    }

    public static DateTime parseTime(String lexicalXSDTime) {
        final Calendar cal = DatatypeConverter.parseTime(lexicalXSDTime);
        return new DateTime(cal);
    }
}
