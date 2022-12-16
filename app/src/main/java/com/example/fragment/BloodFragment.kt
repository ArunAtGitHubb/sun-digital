package com.example.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.item.ItemJob
import com.jellysoft.sundigitalindia.BloodSearchActivity
import com.jellysoft.sundigitalindia.R
import com.jellysoft.sundigitalindia.contact

class BloodFragment : Fragment() {

    lateinit var needBlood: RelativeLayout
    lateinit var donateBlood: RelativeLayout

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_blood, container, false)

        donateBlood = rootView.findViewById(R.id.donateBlood)
        needBlood = rootView.findViewById(R.id.needBlood)

        needBlood.setOnClickListener {
            val intent = Intent(requireActivity(), BloodSearchActivity::class.java)
            startActivity(intent)
        }

        donateBlood.setOnClickListener {
            val intent2 = Intent(requireActivity(), contact::class.java)
            startActivity(intent2)
        }

        return rootView
    }
    companion object {
        @JvmStatic
        fun newInstance(itemJob: ItemJob?): BloodFragment {
            val f = BloodFragment()
            val args = Bundle()
            args.putSerializable("itemJob", itemJob)
            f.arguments = args
            return f
        }
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }
}