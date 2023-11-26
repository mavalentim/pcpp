package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import mobilepayment.Bank.BankCommand;
import mobilepayment.Bank.Transfer;

// Hint: You may generate random numbers using Random::ints
import java.util.Random;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {

  /* --- Messages ------------------------------------- */
  public interface MobileAppCommand {
  }

  // Adding the only specific message type this might receive, which is make
  // payments from the guardian
  public static class MakePayments implements MobileAppCommand {

    public final ActorRef<Account.AccountCommand> from;
    public final ActorRef<Account.AccountCommand> to;
    public final int value;
    private ActorRef<BankCommand> bank;

    public MakePayments(ActorRef<Account.AccountCommand> from, ActorRef<Account.AccountCommand> to,
        ActorRef<Bank.BankCommand> bank, int value) {

      this.bank = bank;
      this.from = from;
      this.to = to;
      this.value = value;
    }
  }

  /* --- State ---------------------------------------- */
  // I dont reckon this needs any state?

  /* --- Constructor ---------------------------------- */
  // Feel free to extend the contructor at your convenience
  private MobileApp(ActorContext context) {
    super(context);
    context.getLog().info("Mobile app {} started!",
        context.getSelf().path().name());
  }

  /* --- Actor initial state -------------------------- */
  public static Behavior<MobileApp.MobileAppCommand> create() {
    return Behaviors.setup(MobileApp::new);
    // You may extend the constructor if necessary
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<MobileAppCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(MakePayments.class, this::onMakePayments)
        .build();
  }

  /* --- Handlers ------------------------------------- */
  // Handles the receiving of make payments
  public Behavior<MobileAppCommand> onMakePayments(MakePayments make_payments_message) {
    /*
     * getContext().getLog().info("{}: Actor {} subscribed",
     * getContext().getSelf().path().name(),
     * msg.ref.path().name());
     */
    // System.out.println("pei pei");
    for (int i = 0; i < 100; i++) {
      Random random = new Random();
      int randomDouble = random.nextInt(1000);
      make_payments_message.bank.tell(new Transfer(make_payments_message.from, make_payments_message.to, randomDouble));
      System.out.println("pei pei");
    }

    /*
     * new Random().ints(100).forEach(
     * x -> make_payments_message.bank.tell(new Transfer(make_payments_message.from,
     * make_payments_message.to, x)));
     */

    return this;
  }
}
