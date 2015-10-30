

object Bank 
{
  
  private var idCounter: Int = 0
  
  def transaction(from: Account, to: Account, amount: Double): Unit = {from.deposit(amount); to.withdraw(amount)}
  
  def getUniqueId: Int = 
  {
    this.synchronized(idCounter += 1)
    idCounter
  } 
}