package me.frep.thotpatrol.utils;

public class UtilTimer {
	
	private long lastMS = 0L;

	public static boolean elapsed(final long from, final long required) {
		return System.currentTimeMillis() - from > required;
	}

	public static long nowlong() {
		return System.currentTimeMillis();
	}

	public boolean isDelayComplete(long delay) {
		if (System.currentTimeMillis() - this.lastMS >= delay) {
			return true;
		}
		return false;
	}

	public boolean hasReached(long milliseconds) {
		return getCurrentMS() - this.lastMS >= milliseconds;
	}

	public void setLastMS() {
		this.lastMS = System.currentTimeMillis();
	}

	public int convertToMS(int perSecond) {
		return 1000 / perSecond;
	}

	public long getCurrentMS() {
		return System.nanoTime() / 1000000L;
	}

	public long getLastMS() {
		return this.lastMS;
	}

	public void setLastMS(long lastMS) {
		this.lastMS = lastMS;
	}

	public void reset() {
		this.lastMS = getCurrentMS();
	}
}
