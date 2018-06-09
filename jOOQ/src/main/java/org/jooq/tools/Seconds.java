package org.jooq.tools;

public class Seconds extends StopWatchTime{
	private String time;
	
	public Seconds (String time) {
		this.time = time;
	}
	
	@Override
	public String getTime() {
		return this.time;
	}
}
