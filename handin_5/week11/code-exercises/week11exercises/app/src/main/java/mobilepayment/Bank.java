package mobilepayment;

import java.util.ArrayList;
import java.util.List;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Bank extends AbstractBehavior<Bank.BankCommand> {

    /* --- Messages ------------------------------------- */
    public interface BankCommand {
    }

    public static class Transfer implements BankCommand {
        // fields of a transfer message
        public final ActorRef<Account.AccountCommand> from;
        public final ActorRef<Account.AccountCommand> to;
        public final int value;

        public Transfer(ActorRef<Account.AccountCommand> from, ActorRef<Account.AccountCommand> to, int value) {
            this.from = from;
            this.to = to;
            this.value = value;
        }

    }

    /* --- State ---------------------------------------- */
    private final List<ActorRef<Account.AccountCommand>> accounts;

    /* --- Constructor ---------------------------------- */
    // Feel free to extend the contructor at your convenience
    private Bank(ActorContext<BankCommand> context) {
        super(context);
        this.accounts = new ArrayList<ActorRef<Account.AccountCommand>>();
    }

    /* --- Actor initial state -------------------------- */
    // this i dont understand so well
    public static Behavior<BankCommand> create() {
        return Behaviors.setup(Bank::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<BankCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Transfer.class, this::onTransfer)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    public Behavior<BankCommand> onTransfer(Transfer transfer) {
        /*
         * getContext().getLog().info("{}: Actor {} subscribed",
         * getContext().getSelf().path().name(),
         * msg.ref.path().name());
         */
        transfer.from.tell(new Account.Deposit(-transfer.value));
        transfer.to.tell(new Account.Deposit(transfer.value));
        return this;
    }
}
