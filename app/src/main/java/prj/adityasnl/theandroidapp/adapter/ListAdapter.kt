package prj.adityasnl.theandroidapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.list_item.view.*
import prj.adityasnl.theandroidapp.R
import prj.adityasnl.theandroidapp.utils.MyArrayList

class ListAdapter(private var items: MyArrayList): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var context: Context ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.configure(items[position], context!!)
    }

    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun configure(obj: JsonObject, context: Context) {
            if (obj.size() != 0) {
                Glide.with(context).load(obj.get("artworkUrl100").asString).into(view.iv_thumbnail)
                view.tv_name.text = obj.get("name").asString
                view.view_gradient.visibility = View.VISIBLE

                view.card_container.setOnClickListener {}
            }
        }
    }
}