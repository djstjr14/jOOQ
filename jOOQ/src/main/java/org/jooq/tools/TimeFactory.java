package org.jooq.tools;

public class TimeFactory{
	public static StopWatchTime getTime(TimeAbstractFactory stopWatchTime) {
		return stopWatchTime.createTime();
	}
}
