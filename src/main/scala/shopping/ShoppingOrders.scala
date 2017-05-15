package shopping

/**
  * Created by shikha on 15/05/17.
  */
class ShoppingOrders {

  val cart = Cart

  def advanceScanCart(items: List[Item]) = {
    items.foreach(cart.addItem)
    val (totalAmount, totalDiscount) = cart.totalBill
    println(f"Total:£${totalAmount}%2.2f, Discount:£$totalDiscount%2.2f ")
  }
}
