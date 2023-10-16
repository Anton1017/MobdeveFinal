package com.example.mobdevemco
import java.util.ArrayList
import CustomDate
class DataGenerator {
    fun loadEntryData(): ArrayList<Entry>{
        val data = ArrayList<Entry>()
        data.add(
            Entry(
                "Maria Kart IRL", "Tokyo, Japan", R.drawable.mario_kart, "Being able to drive the karts like in Mario " +
                        "Kart reminds me of my Childhood", CustomDate(2023, 0, 10)
            )
        )
        data.add(
            Entry(
                "CAVS vs GSW Game 7", "Tokyo, Japan", R.drawable.mario_kart, "Travelled all the way from the Phillipines to watch Lebron comeback " +
                        "from a 3-1 deficeit", CustomDate(2016, 5   , 19)
            )
        )
        
        return data
    }

}