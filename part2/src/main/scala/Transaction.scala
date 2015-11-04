
import exceptions._
import scala.collection.mutable

object TransactionStatus extends Enumeration {
  val SUCCESS, PENDING, FAILED = Value
}

class TransactionQueue {
  
  private var queue = List[Transaction]()
  
  // Remove and return the first element from the queue
  def pop: Transaction = {
    val tail = queue.last;
    queue = queue.init;
    tail
  }

  // Return whether the queue is empty
  def isEmpty: Boolean = queue.isEmpty

  // Add new element to the back of the queue
  def push(t: Transaction): Unit = queue = t :: queue

  // Return the first element from the queue without removing it
  def peek: Transaction = queue head

  // Return an iterator to allow you to iterate over the queue
  def iterator: Iterator[Transaction] = queue.iterator
}

class Transaction(val transactionsQueue: TransactionQueue,
                  val processedTransactions: TransactionQueue,
                  val from: Account,
                  val to: Account,
                  val amount: Double,
                  val allowedAttemps: Int) extends Runnable {

  var status: TransactionStatus.Value = TransactionStatus.PENDING
  var failCount: Int = 0

  override def run: Unit = {
    
    def doTransaction() = {
      from withdraw amount
      to deposit amount
    }
    
    try {
      if (from.uid < to.uid) from synchronized {
        to synchronized {
          doTransaction
        }
      } else to synchronized {
        from synchronized {
          doTransaction
        }
      }
      
      this.status = TransactionStatus.SUCCESS
      processedTransactions.push(this)
      
    } catch {
      case iae: IllegalAmountException => {
        this.status = TransactionStatus.FAILED
        processedTransactions.push(this)
      }
      case nsfe: NoSufficientFundsException => {
        failCount += 1
        if (failCount < allowedAttemps) transactionsQueue.push(this)
        else {
          this.status = TransactionStatus.FAILED
          processedTransactions.push(this)
        }
      }
    }
    
    //Extend this method to satisfy new requirements.

  }
}
