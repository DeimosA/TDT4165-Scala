

object Bank {
  
  private var idCounter: Int = 0
  
  def transaction(from: Account, to: Account, amount: Double): Unit = ??? //Implement
  
  def getUniqueId: Int = this.synchronized {
    idCounter += 1
    idCounter
  }
  
}