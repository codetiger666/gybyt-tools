package cn.gybyt.schedule;

import cn.gybyt.util.BaseUtil;

import java.util.*;

/**
 * cron 表达式解析
 *
 * @program: gybyt-tools
 * @classname: CronExpression
 * @author: codetiger
 * @create: 2024/2/5 20:14
 **/
public class CronExpression {
    private final String expression;
    private final TimeZone timeZone;
    private final BitSet months;
    private final BitSet daysOfMonth;
    private final BitSet daysOfWeek;
    private final BitSet hours;
    private final BitSet minutes;
    private final BitSet seconds;

    public CronExpression(String expression) {
        this(expression, TimeZone.getDefault());
    }

    public CronExpression(String expression, TimeZone timeZone) {
        this.months = new BitSet(12);
        this.daysOfMonth = new BitSet(31);
        this.daysOfWeek = new BitSet(7);
        this.hours = new BitSet(24);
        this.minutes = new BitSet(60);
        this.seconds = new BitSet(60);
        this.expression = expression;
        this.timeZone = timeZone;
        this.parse(expression);
    }

    private CronExpression(String expression, List<String> fields) {
        this.months = new BitSet(12);
        this.daysOfMonth = new BitSet(31);
        this.daysOfWeek = new BitSet(7);
        this.hours = new BitSet(24);
        this.minutes = new BitSet(60);
        this.seconds = new BitSet(60);
        this.expression = expression;
        this.timeZone = null;
        this.doParse(fields);
    }

    String getExpression() {
        return this.expression;
    }

    public Date next(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(this.timeZone);
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        long originalTimestamp = calendar.getTimeInMillis();
        this.doNext(calendar, calendar.get(Calendar.YEAR));
        if (calendar.getTimeInMillis() == originalTimestamp) {
            calendar.add(Calendar.SECOND, 1);
            this.doNext(calendar, calendar.get(Calendar.YEAR));
        }

        return calendar.getTime();
    }

    private void doNext(Calendar calendar, int dot) {
        List<Integer> resets = new ArrayList<>();
        int second = calendar.get(Calendar.SECOND);
        List<Integer> emptyList = Collections.emptyList();
        int updateSecond = this.findNext(this.seconds, second, calendar, 13, 12, emptyList);
        if (second == updateSecond) {
            resets.add(13);
        }

        int minute = calendar.get(Calendar.MINUTE);
        int updateMinute = this.findNext(this.minutes, minute, calendar, 12, 11, resets);
        if (minute == updateMinute) {
            resets.add(12);
        } else {
            this.doNext(calendar, dot);
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int updateHour = this.findNext(this.hours, hour, calendar, 11, 7, resets);
        if (hour == updateHour) {
            resets.add(11);
        } else {
            this.doNext(calendar, dot);
        }

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DATE);
        int updateDayOfMonth = this.findNextDay(calendar, this.daysOfMonth, dayOfMonth, this.daysOfWeek, dayOfWeek,
                                                resets);
        if (dayOfMonth == updateDayOfMonth) {
            resets.add(5);
        } else {
            this.doNext(calendar, dot);
        }

        int month = calendar.get(Calendar.MONTH);
        int updateMonth = this.findNext(this.months, month, calendar, 2, 1, resets);
        if (month != updateMonth) {
            if (calendar.get(Calendar.YEAR) - dot > 4) {
                throw new IllegalArgumentException(
                        "Invalid cron expression \"" + this.expression + "\" led to runaway search for next trigger");
            }

            this.doNext(calendar, dot);
        }

    }

    private int findNextDay(Calendar calendar, BitSet daysOfMonth, int dayOfMonth, BitSet daysOfWeek, int dayOfWeek, List<Integer> resets) {
        int count = 0;
        int max = 366;

        while ((!daysOfMonth.get(dayOfMonth) || !daysOfWeek.get(dayOfWeek - 1)) && count++ < max) {
            calendar.add(Calendar.DATE, 1);
            dayOfMonth = calendar.get(Calendar.DATE);
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            this.reset(calendar, resets);
        }

        if (count >= max) {
            throw new IllegalArgumentException("Overflow in day for expression \"" + this.expression + "\"");
        } else {
            return dayOfMonth;
        }
    }

    private int findNext(BitSet bits, int value, Calendar calendar, int field, int nextField, List<Integer> lowerOrders) {
        int nextValue = bits.nextSetBit(value);
        if (nextValue == -1) {
            calendar.add(nextField, 1);
            this.reset(calendar, Collections.singletonList(field));
            nextValue = bits.nextSetBit(0);
        }

        if (nextValue != value) {
            calendar.set(field, nextValue);
            this.reset(calendar, lowerOrders);
        }

        return nextValue;
    }

    private void reset(Calendar calendar, List<Integer> fields) {
        for (int field : fields) {
            calendar.set(field, field == 5 ? 1 : 0);
        }

    }

    private void parse(String expression) throws IllegalArgumentException {
        List<String> fields = BaseUtil.toList(expression, " ");
        if (!areValidCronFields(fields)) {
            throw new IllegalArgumentException(
                    String.format("Cron expression must consist of 6 fields (found %d in \"%s\")", fields.size(),
                                  expression));
        } else {
            this.doParse(fields);
        }
    }

    private void doParse(List<String> fields) {
        this.setNumberHits(this.seconds, fields.get(0), 0, 60);
        this.setNumberHits(this.minutes, fields.get(1), 0, 60);
        this.setNumberHits(this.hours, fields.get(2), 0, 24);
        this.setDaysOfMonth(this.daysOfMonth, fields.get(3));
        this.setMonths(this.months, fields.get(4));
        this.setDays(this.daysOfWeek, this.replaceOrdinals(fields.get(5), "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
        if (this.daysOfWeek.get(7)) {
            this.daysOfWeek.set(0);
            this.daysOfWeek.clear(7);
        }

    }

    private String replaceOrdinals(String value, String commaSeparatedList) {
        List<String> list = BaseUtil.toList(commaSeparatedList, ",");
        for (int i = 0; i < list.size(); ++i) {
            String item = list.get(i)
                              .toUpperCase();
            value = this.replace(value.toUpperCase(), item, "" + i);
        }
        return value;
    }

    private String replace(String inString, String oldPattern, String newPattern) {
        if (newPattern != null) {
            int index = inString.indexOf(oldPattern);
            if (index == -1) {
                return inString;
            } else {
                int capacity = inString.length();
                if (newPattern.length() > oldPattern.length()) {
                    capacity += 16;
                }

                StringBuilder sb = new StringBuilder(capacity);
                int pos = 0;

                for (int patLen = oldPattern.length(); index >= 0; index = inString.indexOf(oldPattern, pos)) {
                    sb.append(inString, pos, index);
                    sb.append(newPattern);
                    pos = index + patLen;
                }

                sb.append(inString, pos, inString.length());
                return sb.toString();
            }
        } else {
            return inString;
        }
    }

    private void setDaysOfMonth(BitSet bits, String field) {
        int max = 31;
        this.setDays(bits, field, max + 1);
        bits.clear(0);
    }

    private void setDays(BitSet bits, String field, int max) {
        if (field.contains("?")) {
            field = "*";
        }

        this.setNumberHits(bits, field, 0, max);
    }

    private void setMonths(BitSet bits, String value) {
        int max = 12;
        value = this.replaceOrdinals(value, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
        BitSet months = new BitSet(13);
        this.setNumberHits(months, value, 1, max + 1);

        for (int i = 1; i <= max; ++i) {
            if (months.get(i)) {
                bits.set(i - 1);
            }
        }

    }

    private void setNumberHits(BitSet bits, String value, int min, int max) {
        List<String> fields = BaseUtil.toList(value, ",");
        int var7 = fields.size();

        for (String field : fields) {
            if (!field.contains("/")) {
                int[] range = this.getRange(field, min, max);
                bits.set(range[0], range[1] + 1);
            } else {
                List<String> split = BaseUtil.toList(field, "/");
                if (split.size() > 2) {
                    throw new IllegalArgumentException(
                            "Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }

                int[] range = this.getRange(split.get(0), min, max);
                if (!split.get(0)
                          .contains("-")) {
                    range[1] = max - 1;
                }

                int delta = Integer.parseInt(split.get(1));
                if (delta <= 0) {
                    throw new IllegalArgumentException(
                            "Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
                }

                for (int i = range[0]; i <= range[1]; i += delta) {
                    bits.set(i);
                }
            }
        }

    }

    private int[] getRange(String field, int min, int max) {
        int[] result = new int[2];
        if (field.contains("*")) {
            result[0] = min;
            result[1] = max - 1;
            return result;
        } else {
            if (!field.contains("-")) {
                result[0] = result[1] = Integer.parseInt(field);
            } else {
                List<String> split = BaseUtil.toList(field, "-");
                if (split.size() > 2) {
                    throw new IllegalArgumentException(
                            "Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
                }

                result[0] = Integer.parseInt(split.get(0));
                result[1] = Integer.parseInt(split.get(1));
            }

            if (result[0] < max && result[1] < max) {
                if (result[0] >= min && result[1] >= min) {
                    if (result[0] > result[1]) {
                        throw new IllegalArgumentException(
                                "Invalid inverted range: '" + field + "' in expression \"" + this.expression + "\"");
                    } else {
                        return result;
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Range less than minimum (" + min + "): '" + field + "' in expression \"" + this.expression + "\"");
                }
            } else {
                throw new IllegalArgumentException(
                        "Range exceeds maximum (" + max + "): '" + field + "' in expression \"" + this.expression + "\"");
            }
        }
    }

    public static boolean isValidExpression(String expression) {
        if (expression == null) {
            return false;
        } else {
            List<String> fields = BaseUtil.toList(expression, " ");
            if (!areValidCronFields(fields)) {
                return false;
            } else {
                try {
                    new CronExpression(expression, fields);
                    return true;
                } catch (IllegalArgumentException var3) {
                    return false;
                }
            }
        }
    }

    private static boolean areValidCronFields(List<String> fields) {
        return fields != null && fields.size() == 6;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof CronExpression)) {
            return false;
        } else {
            CronExpression otherCron = (CronExpression) other;
            return this.months.equals(otherCron.months) && this.daysOfMonth.equals(
                    otherCron.daysOfMonth) && this.daysOfWeek.equals(otherCron.daysOfWeek) && this.hours.equals(
                    otherCron.hours) && this.minutes.equals(otherCron.minutes) && this.seconds.equals(
                    otherCron.seconds);
        }
    }

    public int hashCode() {
        return 17 * this.months.hashCode() + 29 * this.daysOfMonth.hashCode() + 37 * this.daysOfWeek.hashCode() + 41 * this.hours.hashCode() + 53 * this.minutes.hashCode() + 61 * this.seconds.hashCode();
    }

    public String toString() {
        return this.getClass()
                   .getSimpleName() + ": " + this.expression;
    }
}

