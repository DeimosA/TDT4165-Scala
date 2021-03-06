
object Main extends App {

  def thread(body: =>Unit): Thread = {
      val t = new Thread {
        override def run() = body
      }
      t.start
      t
    }
  
  // Write a few transaction examples using Threads
  println("Test")
  val a1 = new Account(100)
  val a2 = new Account(200)
  
  val t1 = thread(Bank transaction(a2, a1, 150))
  val t2 = thread(Bank transaction(a1, a2, 50))
  val t3 = thread(Bank transaction(a1, a2, 75))
  
  t1.join()
  t2.join()
  t3.join()
  println(a1.getBalanceAmount + " " + a2.getBalanceAmount)
}