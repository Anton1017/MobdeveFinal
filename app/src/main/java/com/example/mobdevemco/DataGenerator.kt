package com.example.mobdevemco
import java.util.ArrayList
import CustomDate
class DataGenerator {
    fun loadEntryData(): ArrayList<Entry>{
        val data = ArrayList<Entry>()
        data.add(
            Entry(
                "Maria Kart IRL", R.drawable.mariokart, "Being able to drive the karts like in Mario " +
                        "Kart reminds me of my Childhood", CustomDate(2023, 0, 10)
            )

        )
        return data
    }

}