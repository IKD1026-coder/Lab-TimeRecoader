package library;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time {

	private int y, m, d, ho, mi, se;

	public Time(int year, int month, int date, int hour, int minute, int second) {
		Calendar timestamp = Calendar.getInstance();
		timestamp.set(year, month - 1, date, hour, minute, second);
		y = timestamp.get(Calendar.YEAR);
		m = timestamp.get(Calendar.MONTH);
		d = timestamp.get(Calendar.DATE);
		ho = timestamp.get(Calendar.HOUR_OF_DAY);
		mi = timestamp.get(Calendar.MINUTE);
		se = timestamp.get(Calendar.SECOND);
	}

	public Time(int year, int month, int date) {
		Calendar timestamp = Calendar.getInstance();
		timestamp.set(year, month - 1, date, 0, 0, 0);
		y = timestamp.get(Calendar.YEAR);
		m = timestamp.get(Calendar.MONTH);
		d = timestamp.get(Calendar.DATE);
		ho = timestamp.get(Calendar.HOUR_OF_DAY);
		mi = timestamp.get(Calendar.MINUTE);
		se = timestamp.get(Calendar.SECOND);
	}

	public Time(int year) {
		Calendar timestamp = Calendar.getInstance();
		timestamp.set(year, 0, 1, 0, 0, 0);
		y = timestamp.get(Calendar.YEAR);
		m = timestamp.get(Calendar.MONTH);
		d = timestamp.get(Calendar.DATE);
		ho = timestamp.get(Calendar.HOUR_OF_DAY);
		mi = timestamp.get(Calendar.MINUTE);
		se = timestamp.get(Calendar.SECOND);
	}

	public Time() {//today
		Calendar timestamp = Calendar.getInstance();
		y = timestamp.get(Calendar.YEAR);
		m = timestamp.get(Calendar.MONTH);
		d = timestamp.get(Calendar.DATE);
		ho = timestamp.get(Calendar.HOUR_OF_DAY);
		mi = timestamp.get(Calendar.MINUTE);
		se = timestamp.get(Calendar.SECOND);
	}

	public void add(int c_param, int scal) {
		Calendar timestamp = Calendar.getInstance();
		timestamp.set(y, m, d, ho, mi, se);
		timestamp.add(c_param, scal);
		y = timestamp.get(Calendar.YEAR);
		m = timestamp.get(Calendar.MONTH);
		d = timestamp.get(Calendar.DATE);
		ho = timestamp.get(Calendar.HOUR_OF_DAY);
		mi = timestamp.get(Calendar.MINUTE);
		se = timestamp.get(Calendar.SECOND);
	}

	public void set(int c_param, int scal) {
		Calendar timestamp = Calendar.getInstance();
		timestamp.set(y, m, d, ho, mi, se);
		timestamp.set(c_param, scal);
		y = timestamp.get(Calendar.YEAR);
		m = timestamp.get(Calendar.MONTH);
		d = timestamp.get(Calendar.DATE);
		ho = timestamp.get(Calendar.HOUR_OF_DAY);
		mi = timestamp.get(Calendar.MINUTE);
		se = timestamp.get(Calendar.SECOND);
	}

	public int getYear() {
		return y;
	}

	public int getMonth() {
		return m + 1;
	}

	public int getDATE() {
		return d;
	}

	public int getHOUR() {
		return ho;
	}

	public int getMINUTE() {
		return mi;
	}

	public int getSECOND() {
		return se;
	}

	public Calendar getCalendar() {
		Calendar timestamp = Calendar.getInstance();
		timestamp.set(y, m, d, ho, mi, se);
		y = timestamp.get(Calendar.YEAR);
		m = timestamp.get(Calendar.MONTH);
		d = timestamp.get(Calendar.DATE);
		ho = timestamp.get(Calendar.HOUR_OF_DAY);
		mi = timestamp.get(Calendar.MINUTE);
		se = timestamp.get(Calendar.SECOND);
		return timestamp;
	}

	/*
	 	ˆø”‚Ì‚Ù‚¤‚ªV‚µ‚¢Žž‚Í1
	 	“™‚µ‚¢Žž‚Í0
	 	ˆø”‚Ì‚Ù‚¤‚ªŒÃ‚¢Žž‚Í-1
	 * */

	public int compare_d(Time t) {//“ú’PˆÊ‚Å”äŠr
		if (getYear() > t.getYear())
			return -1;
		if (getYear() < t.getYear())
			return 1;
		if (getMonth() > t.getMonth())
			return -1;
		if (getMonth() < t.getMonth())
			return 1;
		if (getDATE() > t.getDATE())
			return -1;
		if (getDATE() < t.getDATE())
			return 1;
		return 0;
	}

	public int compare_m(Time t) {//ŒŽ’PˆÊ‚Å”äŠr
		if (getYear() > t.getYear())
			return -1;
		if (getYear() < t.getYear())
			return 1;
		if (getMonth() > t.getMonth())
			return -1;
		if (getMonth() < t.getMonth())
			return 1;
		return 0;
	}

	public int compare_y(Time t) {//”N’PˆÊ‚Å”äŠr
		if (getYear() > t.getYear())
			return -1;
		if (getYear() < t.getYear())
			return 1;
		return 0;
	}

	public String getString(int para) {

		String ret = y + "”N" + (m + 1) + "ŒŽ" + d + "“ú" + ho + "Žž" + mi + "•ª" + se + "•b";
		switch (para) {
		case 1:
			ret = y + "”N" + (m + 1) + "ŒŽ" + d + "“ú" + ho + "Žž" + mi + "•ª" + se + "•b";
			break;
		case 2:
			ret = y + "”N" + (m + 1) + "ŒŽ" + d + "“ú" + ho + "Žž" + mi + "•ª";
			break;
		case 3:
			ret = y + "”N" + (m + 1) + "ŒŽ" + d + "“ú" + ho + "Žž";
			break;
		case 4:
			ret = y + "”N" + (m + 1) + "ŒŽ" + d + "“ú";
			break;
		case 5:
			ret = y + "”N" + (m + 1) + "ŒŽ";
			break;
		case 6:
			ret = y + "”N";
			break;
		}
		return ret;
	}

	public String getWeek(int para) {
		Calendar a = Calendar.getInstance();
		a.set(y, m, d, ho, mi, se);
		String ret = new SimpleDateFormat("'('EEE')'").format(a.getTime());
		switch (para) {
		case 1:
			ret = new SimpleDateFormat("'('EEE')'").format(a.getTime());
			break;
		case 2:
			ret = new SimpleDateFormat("EEE'—j“ú'").format(a.getTime());
			break;
		}

		return ret;
	}

}
