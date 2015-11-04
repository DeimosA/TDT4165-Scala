import scala.concurrent.forkjoin.ForkJoinPool
import scala.concurrent.ExecutionContext

class Bank(val allowedAttempts: Integer = 3) {

  private val uid = ???
  private val transactionsQueue: TransactionQueue = new TransactionQueue()
  private val processedTransactions: TransactionQueue = new TransactionQueue()
  private val executorContext = ExecutionContext.global
  
  private var idCounter: Int = 0

  def addTransactionToQueue(from: Account, to: Account, amount: Double): Unit = {
    transactionsQueue push new Transaction(
      transactionsQueue, processedTransactions, from, to, amount, allowedAttempts)
  }
  
  def generateAccountId: Int = this.synchronized {
    idCounter += 1
    idCounter
  }

  private def processTransactions: Unit = {
    executorContext.execute(transactionsQueue.pop)
  }

  def addAccount(initialBalance: Double): Account = {
    new Account(this, initialBalance)
  }

  def getProcessedTransactionsAsList: List[Transaction] = {
    processedTransactions.iterator.toList
  }

}
