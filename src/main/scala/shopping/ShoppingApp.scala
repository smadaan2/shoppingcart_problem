package shopping


/**
  * Created by shikha on 15/5/2017.
  */

object ShoppingApp {

  val items = List(
    Item("orange"), Item("apple"),
    Item("orange"), Item("apple"), Item("orange"), Item("orange"))

  def main(args: Array[String]): Unit = {
    val shoppingOrders = new ShoppingOrders
    shoppingOrders.advanceScanCart(items)
  }

}
