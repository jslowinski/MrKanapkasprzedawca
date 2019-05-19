package com.example.mrkanapka_sprzedawca.view.list

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.mrkanapka_sprzedawca.R
import com.example.mrkanapka_sprzedawca.database.entity.OrderEntity
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.ModelAbstractItem
import org.w3c.dom.Text
import java.util.Objects.hash

class OrderListItem(model: OrderEntity) : ModelAbstractItem<OrderEntity, OrderListItem, OrderListItem.OrderListItemViewHolder>(model) {

    override fun getType(): Int {
        return R.id.order_type_id
    }

    override fun getViewHolder(v: View): OrderListItemViewHolder {
        return OrderListItemViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_in_order
    }

    override fun getIdentifier(): Long {
        return hash(model).toLong()
    }

    class OrderListItemViewHolder(itemView: View) : FastAdapter.ViewHolder<OrderListItem>(itemView){

        private val nameText: TextView = itemView.findViewById(R.id.numberOrderTextView)
        private val emailText: TextView = itemView.findViewById(R.id.emailOrderTextView)
        private val statusText: TextView = itemView.findViewById(R.id.statusOrderTextView)
        private val commentText: TextView = itemView.findViewById(R.id.commentOrder)
        private val button: Button = itemView.findViewById(R.id.button)
        private val button2: Button = itemView.findViewById(R.id.button2)
        override fun bindView(item: OrderListItem, payloads: MutableList<Any>) {

            // Retrieve model.
            val model = item.model

            //Update view.
            nameText.text = model.order_number
            emailText.text = model.email
            statusText.text = model.status
            if (model.comment == "" || model.comment == "None") {
                commentText.text = "Brak"
            } else {
                commentText.text = model.comment
            }
            if (model.status == "Do realizacji") {
                button.visibility = View.VISIBLE
                button2.visibility = View.GONE
            } else if (model.status == "W transporcie") {
                button2.visibility = View.VISIBLE
                button.visibility = View.GONE
            } else {
                button.visibility = View.GONE
                button2.visibility = View.GONE
            }
        }

        override fun unbindView(item: OrderListItem) {
            nameText.text = null
            emailText.text = null
            statusText.text = null
        }
    }
}