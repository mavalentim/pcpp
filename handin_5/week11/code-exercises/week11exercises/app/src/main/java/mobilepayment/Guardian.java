package mobilepayment;

import java.util.stream.IntStream;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {

  /* --- Messages ------------------------------------- */
  public static final class GuardianCommand {
  }
  // Feel free to add message types at your convenience

  /* --- State ---------------------------------------- */
  // empty

  /* --- Constructor ---------------------------------- */
  private Guardian(ActorContext<GuardianCommand> context) {
    super(context);
  }

  /* --- Actor initial state -------------------------- */
  public static Behavior<GuardianCommand> create() {
    return Behaviors.setup(Guardian::new);
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<GuardianCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(GuardianCommand.class, this::handleGuardianCommand)
        .build();
  }

  /* --- Handlers ------------------------------------- */
  public Behavior<GuardianCommand> handleGuardianCommand(GuardianCommand msg) {
    // create a mobile user
    final ActorRef<MobileApp.MobileAppCommand> mobileApp = getContext().spawn(MobileApp.create(), "mobileAppActor");

    // mobileApp.tell(new MobileApp.MobileAppCommand()); // tell is passing a
    // message of type MobileApp.Something to the
    // mobileApp actor I created

    return this;
  }
}
