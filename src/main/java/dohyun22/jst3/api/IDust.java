package dohyun22.jst3.api;

public interface IDust {
	boolean canGen();
	/**@return Amount of dust generated every 1s*/int getDust();
}