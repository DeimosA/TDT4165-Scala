import akka.actor._
import exceptions._
import scala.collection.immutable.HashMap

case class TransactionRequest(toAccountNumber: String, amount: Double)

case class TransactionRequestReceipt(toAccountNumber: String,
                                     transactionId: String,
                                     transaction: Transaction)

case class BalanceRequest()

class Account(val accountId: String, val bankId: String, val initialBalance: Double = 0) extends Actor {

  private var transactions = HashMap[String, Transaction]()

  class Balance(var amount: Double) {}

  val balance = new Balance(initialBalance)

  lazy val bank = BankManager.findBank(bankId)

  def getFullAddress: String = {
    bankId + accountId
  }

  def getTransactions: List[Transaction] = {
    // Should return a list of all Transaction-objects stored in transactions 
    transactions.values.toList
  }

  def allTransactionsCompleted: Boolean = {
    // Should return whether all Transaction-objects in transactions are completed
    for (transaction <- transactions) {
      if (!transaction._2.isCompleted) return false
    }
    true
  }

  def withdraw(amount: Double): Unit = {
    balance.synchronized {
      if (balance.amount - amount < 0) throw new NoSufficientFundsException
      if (amount <= 0) throw new IllegalAmountException
      balance.amount -= amount
    }
  }

  def deposit(amount: Double): Unit = {
    balance.synchronized {
      if (amount <= 0) throw new IllegalAmountException()
      balance.amount += amount
    }
  }

  def sendTransactionToBank(t: Transaction): Unit = {
    // Should send a message containing t to the bank of this account
    bank ! t
  }

  def transferTo(accountNumber: String, amount: Double): Transaction = {
    
    val t = new Transaction(from = getFullAddress, to = accountNumber, amount = amount)

    if (reserveTransaction(t)) {
      try {
        withdraw(amount)
        sendTransactionToBank(t)

      } catch {
        case _: NoSufficientFundsException | _: IllegalAmountException =>
          t.status = TransactionStatus.FAILED
      }
    }

    t

  }

  def reserveTransaction(t: Transaction): Boolean = {
    if (!transactions.contains(t.id)) {
      transactions += (t.id -> t)
      return true
    }
    false
  }

  override def receive = {
    case IdentifyActor => sender ! this

    case TransactionRequestReceipt(to, transactionId, transaction) => {
      // Process receipt
      ???
    }

    case BalanceRequest => sender ! getBalanceAmount // Should return current balance

    case t: Transaction => {
    	// Handle incoming transaction
    	try {
    		deposit(t.amount)
    		t.status = TransactionStatus.SUCCESS
    	} catch {
    	  case _: IllegalAmountException =>
    	    t.status = TransactionStatus.FAILED
    	}

    }
    
    case msg => ???
  }

  def getBalanceAmount: Double = balance.amount

}
