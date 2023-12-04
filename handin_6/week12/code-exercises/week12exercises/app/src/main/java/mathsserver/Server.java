package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.IntStream;

import mathsserver.Task;
import mathsserver.Task.BinaryOperation;
import mathsserver.Worker.WorkerCommand;

public class Server extends AbstractBehavior<Server.ServerCommand> {
	/* --- Messages ------------------------------------- */
	public interface ServerCommand {
	}

	public static final class ComputeTasks implements ServerCommand {
		public final List<Task> tasks;
		public final ActorRef<Client.ClientCommand> client;

		public ComputeTasks(List<Task> tasks,
				ActorRef<Client.ClientCommand> client) {
			this.tasks = tasks;
			this.client = client;
		}
	}

	public static final class WorkDone implements ServerCommand {
		ActorRef<Worker.WorkerCommand> worker;

		public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
			this.worker = worker;
		}
	}

	/* --- State ---------------------------------------- */
	// To be implemented
	int minWorkers;
	int maxWorkers;

	// Some sort of list to keep track of all the tasks that we have
	ArrayList<Task> tasksToBeAssigned;

	// HashMap to keep track of the workers and their status
	HashMap<ActorRef<Worker.WorkerCommand>, Boolean> available_workers;

	/* --- Constructor ---------------------------------- */
	// the constructor is defined here but we do not use it directly, we use the
	// spawn thing but we are still allowed to toggle with it
	private Server(ActorContext<ServerCommand> context,
			int minWorkers, int maxWorkers) {
		super(context);
		// To be implemented
		// Set the minimum and maximum to the local variables
		minWorkers = minWorkers;
		maxWorkers = maxWorkers;
		// initialize the hashmap for tracking which workers are inactive
		available_workers = new HashMap<>();
		// initialize the list or whatever structure to keep track of the other tasks
		tasksToBeAssigned = new ArrayList<>();

		// spawn the minimum workers and set their value as available in the hashmap
		ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(this), "server");

	}

	/* --- Actor initial state -------------------------- */
	public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
		return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
	}

	/* --- Message handling ----------------------------- */
	@Override
	public Receive<ServerCommand> createReceive() {
		return newReceiveBuilder()
				.onMessage(ComputeTasks.class, this::onComputeTasks)
				.onMessage(WorkDone.class, this::onWorkDone)
				// To be extended
				.build();
	}

	/* --- Handlers ------------------------------------- */
	public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
		// To be implemented
		// The list of tasks should be appended to my local state somehow
		msg.tasks.stream().forEach(x -> tasksToBeAssigned.add(x));
		// spawn workers to get these tasks done

		return this;
	}

	public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
		// To be implemented
		return this;
	}
}
