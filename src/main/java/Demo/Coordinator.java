import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

/*
 * Coordinator.java
 * This class main functionality call different threads based on the provided logic.
 * Coordinator starts the pipe by sending a stream of random integers.
 * Returns the final out put from different process classes.
 */

public class Coordinator {

	@SuppressWarnings({ "unused" })
	public static void main(String[] args) {

		Integer randomIntegerCount = Integer.parseInt(args[0]);
		PrintStream printStreamObj = new PrintStream(System.out);

		ProcessBuilder processBuilderObjForGen = new ProcessBuilder("java", "PerimeterGen");
		ProcessBuilder processBuilderObjForUse = new ProcessBuilder("java", "PerimeterUse");

		try {
			final Process prcoessObjForGen = processBuilderObjForGen.start();
			Thread thread1 = new Thread(new Thread1(randomIntegerCount, printStreamObj, prcoessObjForGen));
			thread1.start();
			thread1.join();

			Scanner genScanner = new Scanner(new InputStreamReader(prcoessObjForGen.getInputStream()));
			PrintStream genPrintStream = new PrintStream(prcoessObjForGen.getOutputStream());
			prcoessObjForGen.waitFor();

			final Process prcoessObjForUse = processBuilderObjForUse.start();
			Thread thread2 = new Thread(new Thread2(genScanner, genPrintStream, prcoessObjForUse));
			thread2.start();
			thread2.join();

			Scanner useScanner = new Scanner(new InputStreamReader(prcoessObjForUse.getInputStream()));
			PrintStream usePrintStream = new PrintStream(prcoessObjForUse.getOutputStream());
			prcoessObjForUse.waitFor();

			Thread thread3 = new Thread(new Thread3(useScanner));
			thread3.start();
			thread3.join();

		} catch (Exception ex) {
			System.out.println("Exception Info: main method " + ex.getStackTrace());

		}
	}

	/*
	 * Thread Thread1 class. Generating the random integers and feed to first
	 * process class. stream of random integers. Returns the final out put from
	 * different.
	 */

	static class Thread1 implements Runnable {

		private Integer randomIntegerCount;
		private Random r = new Random();
		private PrintStream printStreamObj;
		private Process process;

		public Thread1(Integer randomIntegerCount, PrintStream printStreamObj, Process process) {
			this.printStreamObj = printStreamObj;
			this.randomIntegerCount = randomIntegerCount;
			this.process = process;
		}

		@Override
		public synchronized void run() {

			OutputStream outputStream = process.getOutputStream();
			StringBuffer buffer = new StringBuffer();
			try {
				for (int i = 0; i < randomIntegerCount; i++) {
					int tmp = r.nextInt(899) + 100; // between 100 - 999 inclusive.
					printStreamObj.println(tmp);
					buffer.append(tmp).append(" ");
				}
				printStreamObj.println(-1);
				buffer.append(-1);
				
				outputStream.write(buffer.toString().getBytes());
				outputStream.flush();
				outputStream.close();

			} catch (Exception ex) {
				System.out.println("Exception Info: Thread 1 " + ex.getStackTrace());
			}
		}
	}

	/*
	 * Thread Thread2 class. Generating the random integers and feed to first
	 * process class. stream of random integers. Returns the final out put from
	 * different.
	 */
	static class Thread2 implements Runnable {

		private Scanner scanner;
		@SuppressWarnings("unused")
		private PrintStream printStreamObj;
		private Process prcoess;

		public Thread2(Scanner scanner, PrintStream printStreamObj, Process prcoess) {
			this.scanner = scanner;
			this.printStreamObj = printStreamObj;
			this.prcoess = prcoess;
		}

		@Override
		public synchronized void run() {
			try {
				OutputStream outputStream = prcoess.getOutputStream();
				while (scanner.hasNext()) {
					outputStream.write(String.valueOf(scanner.nextDouble()).toString().getBytes());
					String hd = " ";
					outputStream.write(hd.getBytes());
				}
				outputStream.flush();
				outputStream.close();
			} catch (Exception ex) {
				System.out.println("Exception Info: Thread 2 " + ex.getStackTrace());
			}
		}
	}

	/*
	 * Thread Thread3 class. populating and printing the final output.
	 */
	static class Thread3 implements Runnable {

		private Scanner scanner;

		public Thread3(Scanner scanner) {
			this.scanner = scanner;
		}

		@Override
		public synchronized void run() {
			StringBuffer buffer = new StringBuffer();
			while (scanner.hasNext()) {
				buffer.append(scanner.next()).append(" ");
			}
			System.out.println("PerimeterUse value returned: " + buffer.toString());

		}
	}
}
