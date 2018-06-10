package org.jooq.tools;

public abstract class StopWatchTime{
	public abstract String getTime();
	
	@Override
	public String toString() {
		return getTime();
	}
}
