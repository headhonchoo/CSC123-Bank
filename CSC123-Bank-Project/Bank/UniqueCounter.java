import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class UniqueCounter {
	private static int counterState = 1000;

	public static int getCounterState() {
		return counterState;
	}

	public static void setCounterState(int counterState) {
		UniqueCounter.counterState = counterState;
	}

	public static int nextValue() {
		return counterState++;
	}


}
