
import exceptions._

class Account(initialBalance: Double, val uid: Int = Bank getUniqueId) {
  
  var balance: Double = initialBalance
  
  
  def withdraw(amount: Double): Unit = this.synchronized {
    if (amount < 0) throw new IllegalAmountException()
    if (initialBalance - amount < 0) throw new NoSufficientFundsException()
    
    balance -= amount
  }
  
  def deposit(amount: Double): Unit = this.synchronized {
    if (amount < 0) throw new IllegalAmountException
    
    balance += amount
  }
  
  def getBalanceAmount: Double = {
    balance
  }

}