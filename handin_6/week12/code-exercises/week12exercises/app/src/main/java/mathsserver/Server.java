package mathsserver;

import akka.actor.SupervisorStrategy;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.IntStream;
import mathsserver.Client.ClientCommand;
import mathsserver.Task;
import mathsserver.Task.BinaryOperation;
import mathsserver.Worker.ComputeTask;
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
	private final int minWorkers;
	private final int maxWorkers;
	private int existantWorkers; // to track the current number of workers
	// Some sort of list to keep track of all the tasks that we have
	Stack<Task> pendingTasks;
	Stack<ActorRef<Client.ClientCommand>> pendingClients;

	// HashMap to keep track of the workers and their status
	// HashMap<ActorRef<Worker.WorkerCommand>, Boolean> available_workers;
	Stack<ActorRef<Worker.WorkerCommand>> avWorks;
	HashMap<ActorRef<WorkerCommand>, Task> jobsInProgress;
	HashMap<ActorRef<WorkerCommand>, ActorRef<Client.ClientCommand>> jobsInProgress2;

	/* --- Constructor ---------------------------------- */
	// the constructor is defined here but we do not use it directly, we use the
	// spawn thing but we are still allowed to toggle with it
	private Server(ActorContext<ServerCommand> context,
			int minWorkers, int maxWorkers) {
		super(context);
		// To be implemented
		// Set the minimum and maximum to the local variables
		this.minWorkers = minWorkers;
		this.maxWorkers = maxWorkers;
		pendingTasks = new Stack<>();
		pendingClients = new Stack<>();
		avWorks = new Stack<>();
		existantWorkers = 0;

		jobsInProgress = new HashMap<ActorRef<Worker.WorkerCommand>, Task>();
		jobsInProgress2 = new HashMap<ActorRef<Worker.WorkerCommand>, ActorRef<Client.ClientCommand>>();

		// spawn the minimum workers and set their value as available in the hashmap
		int i = 0;
		while (i < this.minWorkers) {

			// spawn the minimum amount of workers
			final ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()),
					"worker_n" + i);
			// watch it
			getContext().watch(worker);
			existantWorkers++;
			// put them in the hashmap with availability true
			// available_workers.put(worker, true);
			avWorks.push(worker);
			i++;
		}

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
				.onSignal(ChildFailed.class, this::onChildFailed)
				// To be extended
				.build();
	}

	/* --- Handlers ------------------------------------- */
	public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
		// How many workers are available rn

		// Create a list of tasks
		// ArrayList<Task> tasksToBeAssigned = new ArrayList<>();
		// for each task received, put it thru the pipeline

		msg.tasks.stream().forEach(task -> {
			if (avWorks.size() > 0) { // there are idle workers
				ActorRef<Worker.WorkerCommand> worker = avWorks.pop(); // get one from the availables
				worker.tell(new ComputeTask(task, msg.client)); // tell it to do the task

				jobsInProgress.put(worker, task);
				jobsInProgress2.put(worker, msg.client); // add it to jobs
															// in progress

			} else if (avWorks.isEmpty() & existantWorkers < this.maxWorkers) {
				existantWorkers++;
				ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()),
						"worker_nonOG" + existantWorkers);
				getContext().watch(worker); // watch it
				worker.tell(new ComputeTask(task, msg.client));
				jobsInProgress.put(worker, task);
				jobsInProgress2.put(worker, msg.client);
			} else {
				pendingTasks.push(task);
				pendingClients.push(msg.client);
			}

		});

		return this;
	}

	public Behavior<ServerCommand> onChildFailed(ChildFailed signal) {
		ActorRef<Void> actorFailed = signal.getRef();
		System.out.println(actorFailed + " failed! :((");
		// create new worker
		ActorRef<Worker.WorkerCommand> worker = getContext().spawn(Worker.create(getContext().getSelf()),
				"replaceWorker" + "-" + existantWorkers);
		getContext().watch(worker);
		// push it to available workers
		avWorks.push(worker);
		// worker.tell(new ComputeTask(incompleteTask, incompleteClient));

		return this;
	}

	public Behavior<ServerCommand> onWorkDone(WorkDone msg) {

		if (!pendingTasks.isEmpty()) {
			msg.worker.tell(new ComputeTask(pendingTasks.pop(), pendingClients.pop()));
		} else {
			avWorks.push(msg.worker);
		}

		return this;
	}
}
