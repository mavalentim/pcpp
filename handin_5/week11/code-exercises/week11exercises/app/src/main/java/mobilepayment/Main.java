package mobilepayment;

import akka.actor.typed.ActorSystem;
import mobilepayment.Guardian.GuardianCommand;

import java.io.IOException;

import com.google.common.util.concurrent.Monitor.Guard;

public class Main {

	public static void main(String[] args) {

		// start actor system
		// We start the actorsystem instance as

		final ActorSystem<Guardian.GuardianCommand> guardiao = ActorSystem.create(Guardian.create(), "Mysystem");

		// init message
		guardiao.tell(new Guardian.GuardianCommand());

		// wait until user presses enter
		try {
			System.out.println(">>> Press ENTER to exit <<<");
			System.in.read();
		} catch (IOException e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		} finally {
			// terminate actor system execution
			// To be implemented
		}

	}

}
