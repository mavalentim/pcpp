package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Account extends AbstractBehavior<Account.AccountCommand> {

  /* --- Messages ------------------------------------- */
  public interface AccountCommand {
  }
  // Feel free to add message types at your convenience

  /* --- State ---------------------------------------- */
  // Each account must have a balance, and Im having a hard time understanding
  // how it should be final
  private int balance;

  // SPECIFC MESSAGE TO BE RECEIVED 1: Deposits
  // specific message that will be received stems from AccountCommand which is
  // the more general interface implemented a type Deposit message that
  public static final class Deposit implements AccountCommand {
    public final int value;

    public Deposit(int value) {
      this.value = value;
    }
  }

  public static final class PrintBalance implements AccountCommand {
    public final ActorRef<Account.AccountCommand> referedTo; // WHY IS THIS NOT ACCOUNT ONLY

    public PrintBalance(ActorRef<Account.AccountCommand> refersTo) {
      this.referedTo = refersTo;
    }
  }

  /* --- Constructor ---------------------------------- */
  // Feel free to extend the contructor at your convenience
  private Account(ActorContext<AccountCommand> context) {
    super(context);
    this.balance = 0;
  }

  /* --- Actor initial state -------------------------- */
  public static Behavior<Account.AccountCommand> create() {
    return Behaviors.setup(Account::new);
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<AccountCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(Deposit.class, this::onDeposit) // handles receiving message of that type
        .build();
  }

  /* --- Handlers ------------------------------------- */
  // The handlers are the methods that explain what happens when a
  // particular message is received. The message handling part only refers to them

  public Behavior<AccountCommand> onDeposit(Deposit deposit) {
    // makes a public annoucement of how much was the deposit worth
    getContext().getLog().info("Deposit on the value off {} made",
        // getContext().getSelf().path().name(),
        deposit.value);

    // updates the balance field when receiving this message
    balance = balance + deposit.value;
    // msg.ref.tell(new User.SubscriptionOK());
    return this;
  }

  public Behavior<AccountCommand> onPA(PrintBalance printBalanceMessage) {
    // makes a public annoucement of how much was the deposit worth
    System.out.println("The balance is worth" + printBalanceMessage.referedTo.path()); // its not path but i dont know
                                                                                       // how to reach the account
                                                                                       // balance
    return this;
  }

}
