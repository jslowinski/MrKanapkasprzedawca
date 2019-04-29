package com.example.mrkanapka_sprzedawca.view.list

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.example.mrkanapka_sprzedawca.R
import com.example.mrkanapka_sprzedawca.api.model.ResponseShopping
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import java.util.*

class ShoppingListItem(model: ResponseShopping) : ModelAbstractItem<ResponseShopping, ShoppingListItem, ShoppingListItem.ShoppingListItemViewHolder>(model){
    override fun getType(): Int {
        return R.id.shopping_type_id
    }

    override fun getViewHolder(v: View): ShoppingListItemViewHolder {
        return ShoppingListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_in_shopping_list
    }

    override fun getIdentifier(): Long {
        return Objects.hash(model).toLong()
    }

    class ShoppingListItemViewHolder(itemView: View) : FastAdapter.ViewHolder<ShoppingListItem>(itemView){

        private val productName: TextView = itemView.findViewById(R.id.shoppingName)
        private val shoppingAmount: TextView = itemView.findViewById(R.id.shoppingAmount)

        override fun bindView(item: ShoppingListItem, payloads: MutableList<Any>) {
            val model = item.model

            productName.text = model.name
            shoppingAmount.text = model.sum.toString() + " " + model.unit
        }

        override fun unbindView(item: ShoppingListItem) {
            productName.text = null
        }
    }
}

