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
import androidx.recyclerview.widget.RecyclerView
import com.example.item.ItemProduct
import com.example.util.PopUpAds
import com.example.util.RvOnClickListener
import com.jellysoft.sundigitalindia.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeProductAdapter(private val mContext: Context, private val dataList: ArrayList<*>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var clickListener: RvOnClickListener? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.row_product_item, parent, false)
        return ItemRowHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ItemRowHolder
        val singleItem = dataList!![position] as ItemProduct
        holder.jobTitle.text = singleItem.productName
        holder.jobid.text = "PD" + singleItem.id
        holder.city.text = singleItem.City
        if (!singleItem.productArea?.let { isNullOrEmpty(it) }!!){
            holder.areaLinear.visibility = View.VISIBLE
            holder.area.text = singleItem.productArea
        } else {
            holder.areaLinear.visibility = View.GONE
        }

        holder.productPrice.text = "Rs. " + singleItem.productPrice!!.replace(".00", "") + "/-"
        holder.productSellingPrice.text =
            "Rs. " + singleItem.productSellingPrice!!.replace(".00", "") + "/-"
        holder.productDoc.text = if (singleItem.productDoc == "YES") "YES" else "NO"
        Picasso.get().load(singleItem.productLogo).placeholder(R.drawable.placeholder)
            .into(holder.jobImage)
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
                ${singleItem.productName}
                ${mContext.getString(R.string.job_company_lbl)}${singleItem.productCompanyName}
                ${mContext.getString(R.string.job_designation_lbl)}${singleItem.productDesignation}
                ${mContext.getString(R.string.job_phone_lbl)}${singleItem.productPhoneNumber}
                ${mContext.getString(R.string.job_address_lbl)}${singleItem.City}
                
                "View details here: https://www.sundigitalindiajobs.com/product?id=${singleItem.id}
                """.trimIndent()
            )
            sendIntent.type = "text/plain"
            mContext.startActivity(sendIntent)
        }
        holder.call.setOnClickListener { view: View? ->
            val phone = singleItem.productPhoneNumber
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            mContext.startActivity(intent)
        }
        holder.whatsapp.setOnClickListener { view: View? ->
            val phone = singleItem.productPhoneNumber
            val url = "https://api.whatsapp.com/send?phone=" + phone!!.substring(1)
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
        var jobTitle: TextView
        var productPrice: TextView
        var productSellingPrice: TextView
        var productDoc: TextView
        var jobid: TextView
        var city: TextView
        var call: TextView
        var whatsapp: TextView
        var area: TextView
        var lyt_parent: LinearLayout
        var areaLinear: LinearLayout
        var btnApplyJob: Button
        var jobImage: CircleImageView
        var share: ImageView

        init {
            jobTitle = itemView.findViewById(R.id.text_job_title)
            city = itemView.findViewById(R.id.city)
            area = itemView.findViewById(R.id.area)
            productPrice = itemView.findViewById(R.id.product_price)
            productSellingPrice = itemView.findViewById(R.id.product_selling_price)
            productDoc = itemView.findViewById(R.id.product_document)
            areaLinear = itemView.findViewById(R.id.areaLinear)
            jobid = itemView.findViewById(R.id.text_job_id)
            lyt_parent = itemView.findViewById(R.id.rootLayout)
            jobImage = itemView.findViewById(R.id.image_job)
            share = itemView.findViewById(R.id.share)
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job)
            call = itemView.findViewById(R.id.call)
            whatsapp = itemView.findViewById(R.id.whatsapp)
        }
    }
}