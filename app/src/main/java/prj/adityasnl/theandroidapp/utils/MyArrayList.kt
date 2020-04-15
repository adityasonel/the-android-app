package prj.adityasnl.theandroidapp.utils

import com.google.gson.JsonObject

class MyArrayList: ArrayList<JsonObject>() {
    init {
        for (i in 0..7) {
            this.add(JsonObject())
        }
    }
}