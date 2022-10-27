package com.example.item

import com.denzcoskun.imageslider.models.SlideModel
import java.io.Serializable

class ItemProduct : Serializable {
    var id: String? = null
    var views: String? = null
    var productName: String? = null
    var productBanner: ArrayList<SlideModel>? = null

    var url: String? = null
    var productDesignation: String? = null
    var productDesc: String? = null
    var productSalary: String? = null
    var productCompanyName: String? = null
    var productCompanyWebsite: String? = null
    var productImage: String? = null
    var productArea: String? = null
    var productPrice: String? = null
    var productSellingPrice: String? = null
    var WebsiteLink: String? = null
    var productLogo: String? = null
    var productDate: String? = null
    var productNegotiable: String? = null
    var productDoc: String? = null
    var productApplyTotal: String? = null
    var productCategoryName: String? = null
    var productAppliedDate: String? = null
    var productType: String? = null
    var pLate: String? = null
    var pDate: String? = null
    var Age: String? = null
    var Sex: String? = null
    var marital: String? = null
    var City: String? = null
    var productExperience: String? = null
    var isproductSeen = false
    var isProductFavourite = false

    var productPhoneNumber: String? = null
    var productPhoneNumber2: String? = null
    var productMail: String? = null

    var productVacancy: String? = null
    var productAddress: String? = null
    var productQualification: String? = null
    var productSkill: String? = null
}