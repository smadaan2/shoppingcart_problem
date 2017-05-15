package shopping

import shopping.Cart.{BillToPay, CalculatedPrice}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by shikha on 15/05/17.
  */
trait PrepareBilling {
  def addItem(item: Item): Unit

  def calculateItemPrice(item: Item, alreadyAddedItems: Int): Option[CalculatedPrice]
}

case class Cart(stockService: Stock) extends PrepareBilling {
  private val cart = mutable.Map[Item, List[Item]]()

  private val calculatedPrices = new ListBuffer[(Item, CalculatedPrice)]()

  def totalBill: (Double, Double) = {
    calculatedPrices.foldLeft((0.0, 0.0)) {
      case ((total, discount), (_, calculatedPrice)) =>
        (total + calculatedPrice.price, discount + calculatedPrice.discount)
    }
  }

  def removeItem(item: Item) = {
    cart.get(item).map {
      items =>
        if (items.length == 0) cart.remove(item)
        else {
          cart += item -> items.tail
        }
    }
  }

  override def addItem(item: Item): Unit = {
    cart.get(item) match {
      case Some(items) =>
        val alreadyAddedItems = item +: items
        cart.update(item, alreadyAddedItems)
        calculateItemPrice(item, alreadyAddedItems.length).foreach { f =>
          val itemWithPrice = (item, f)
          println(f"${item.name}, price:${f.price}%2.2f, discount:${f.discount}%2.2f")
          calculatedPrices += itemWithPrice
        }
      case None =>
        cart += item -> List(item)
        calculateItemPrice(item, alreadyAddedItems = 1).foreach { f =>
          val itemWithPrice = (item, f)
          println(f"${item.name}, price:${f.price}%2.2f, discount:${f.discount}%2.2f")
          calculatedPrices += itemWithPrice
        }
    }
  }

  override def calculateItemPrice(item: Item, alreadyAddedItems: Int): Option[CalculatedPrice] = {
    stockService.getStockItem(item).map {
      stockItem =>
        stockService.getOffer(item).fold(CalculatedPrice(stockItem.price, 0)) {
          offer =>
            if (alreadyAddedItems % offer.discount.getNItems == 0) CalculatedPrice(0, stockItem.price)
            else CalculatedPrice(stockItem.price, 0)
        }
    }
  }

  /**
    * This function calculate price of item after applying discount offer
    *
    * @param item : shopping.Item item name and price
    * @return Double final price of item after applying discount offer
    * @example { { {
    *          //Discount-3 for the price of 2 on Oranges, oranges cost 25p
    *          //calculate price of 5 oranges
    *          (2*(5/3)+(5%3))*0.25
    *          }}}
    **/
  def calculateItemPrice(item: Item): Option[Double] = {
    stockService.getStockItem(item).map {
      stockItem =>
        stockService.getOffer(item).fold(item.numberOfItems * stockItem.price) {
          offer =>
            val discount = offer.discount
            (discount.buyNItems * (item.numberOfItems / discount.getNItems) + (item.numberOfItems % discount.getNItems)) * stockItem.price
        }

    }
  }
}

object Cart extends Cart(new Stock) {

  case class BillToPay(total: Double = 0, discount: Double = 0)

  case class CalculatedPrice(price: Double, discount: Double)

}