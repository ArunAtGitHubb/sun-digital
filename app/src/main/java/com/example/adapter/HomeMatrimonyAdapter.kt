package com.example.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.item.ItemMatrimony
import com.example.util.PopUpAds
import com.example.util.RvOnClickListener
import com.jellysoft.sundigitalindia.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeMatrimonyAdapter(private val mContext: Context, private val dataList: ArrayList<*>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var clickListener: RvOnClickListener? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.row_matrimony_item, parent, false)
        return ItemRowHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ItemRowHolder
        val singleItem = dataList!![position] as ItemMatrimony
        holder.matrimonyName.text = singleItem.matrimonyName
        holder.matrimonyAge.text = singleItem.matrimonyAge
        holder.matrimonyCity.text = singleItem.city
        holder.matrimonyGender.text = singleItem.matrimonyGender
        holder.matrimonyReligion.text = singleItem.matrimonyReligion
        holder.text_caste.text = singleItem.categoryName
        if (!singleItem.matrimonyArea?.let { isNullOrEmpty(it) }!!){
            holder.text_area.text = singleItem.matrimonyArea
            holder.areaLinear.visibility = View.VISIBLE
        } else {
            holder.areaLinear.visibility = View.GONE
        }
        holder.text_matrimony_id.text = "MM" + singleItem.id
        Picasso.get().load(singleItem.matrimonyLogo).placeholder(R.drawable.placeholder)
            .into(holder.matrimonyImage)
        holder.lyt_parent.setOnClickListener {
            PopUpAds.showInterstitialAds(
                mContext,
                holder.adapterPosition,
                clickListener
            )
        }
        holder.btnApplyJob.setOnClickListener {
            PopUpAds.showInterstitialAds(
                mContext,
                holder.adapterPosition,
                clickListener
            )
        }
        holder.share.setOnClickListener { view: View? ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                """
                ${singleItem.matrimonyName}
                ${mContext.getString(R.string.job_phone_lbl)}${singleItem.matrimonyPhoneNumber}
                ${mContext.getString(R.string.job_address_lbl)}${singleItem.city}
                
                "View details here: https://www.sundigitalindiajobs.com/matrimony?id=${singleItem.id}
                """.trimIndent()
            )
            sendIntent.type = "text/plain"
            mContext.startActivity(sendIntent)
        }
        holder.call.setOnClickListener { view: View? ->
            val phone = singleItem.matrimonyPhoneNumber
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            mContext.startActivity(intent)
        }
        holder.whatsapp.setOnClickListener { view: View? ->
            val phone = "91" + singleItem.matrimonyPhoneNumber!!.replace("+91", "")
            val url = "https://api.whatsapp.com/send?phone=$phone"
            val pm = mContext.packageManager
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mContext.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun setOnItemClickListener(clickListener: RvOnClickListener?) {
        this.clickListener = clickListener
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }

    internal inner class ItemRowHolder @RequiresApi(api = Build.VERSION_CODES.O) constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var call: TextView
        var whatsapp: TextView
        var matrimonyName: TextView
        var text_area: TextView
        var text_caste: TextView
        var text_matrimony_id: TextView
        var matrimonyAge: TextView
        var matrimonyCity: TextView
        var matrimonyGender: TextView
        var matrimonyReligion: TextView
        var lyt_parent: LinearLayout
        var areaLinear: LinearLayout
        var btnApplyJob: Button
        var cardViewType: CardView
        var matrimonyImage: CircleImageView
        var share: ImageView

        init {
            matrimonyName = itemView.findViewById(R.id.text_name)
            matrimonyAge = itemView.findViewById(R.id.text_age)
            matrimonyCity = itemView.findViewById(R.id.city)
            text_area = itemView.findViewById(R.id.area)
            areaLinear = itemView.findViewById(R.id.areaLinear)
            text_caste = itemView.findViewById(R.id.text_caste)
            matrimonyGender = itemView.findViewById(R.id.text_matrimony_religion)
            matrimonyReligion = itemView.findViewById(R.id.text_religion)
            text_matrimony_id = itemView.findViewById(R.id.text_matrimony_id)
            lyt_parent = itemView.findViewById(R.id.rootLayout)
            cardViewType = itemView.findViewById(R.id.cardJobType)
            matrimonyImage = itemView.findViewById(R.id.image_job)
            share = itemView.findViewById(R.id.share)
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job)
            call = itemView.findViewById(R.id.call)
            whatsapp = itemView.findViewById(R.id.whatsapp)
        }
    }
}