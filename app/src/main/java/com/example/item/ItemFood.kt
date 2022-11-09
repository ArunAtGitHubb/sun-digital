package com.example.item

class ItemFood {

    var id: String? = null
    var cityId: String? = null
    var pid: String? = null
    var quantity: Long = 0
    var price: String? = null
    var name: String? = null
    var count: Int = 0
    var imageUrl: String? = null
    var type: String? = null
    var priceUnit: String? = null
    var priceUnitName: String? = null
    var priceUnitId: String? = null
    var qty: Int? = null

    var catId: String? = null
    var views: String? = null
    var category: String? = null
    var categoryImage: String? = null

    override fun toString(): String {
        return "food -> $name"
    }
}