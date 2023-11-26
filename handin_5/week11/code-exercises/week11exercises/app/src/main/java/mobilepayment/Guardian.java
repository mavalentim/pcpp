package mobilepayment;

import java.util.stream.IntStream;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import mobilepayment.MobileApp.MakePayments;

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
    // ActorSystem<Guardian.GuardianCommand> guardiao =
    // ActorSystem.create(Guardian.create(), "Mysystem");

    // create accounts and a bank
    final ActorRef<Account.AccountCommand> account1 = getContext().spawn(Account.create(), "a1");
    final ActorRef<Account.AccountCommand> account2 = getContext().spawn(Account.create(), "a2");
    // bank
    final ActorRef<Bank.BankCommand> bank1 = getContext().spawn(Bank.create(), "bank1");
    // and you can see that bank1 is an actor (ACTORREF) parameterized for receiving
    // the Bank.BankCommand kind of message

    mobileApp.tell(new MakePayments(account1, account2, bank1, 0)); // tell is passing a
    // message of type MobileApp.Something to the
    // mobileApp actor I created

    return this;
  }
}
