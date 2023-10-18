package com.example.mobdevemco
import java.util.ArrayList
import CustomDate
class DataGenerator {
    companion object {
        fun loadEntryData(): ArrayList<Entry> {
            val data = ArrayList<Entry>()
            data.add(
                Entry(
                    "Maria Kart IRL",
                    "Tokyo, Japan",
                    R.drawable.mario_kart,
                    "Being able to drive the karts like in Mario :)" +
                            "Kart reminds me of my Childhood",
                    CustomDate(2023, 9, 12)
                )
            )
            data.add(
                Entry(
                    "CAVS vs GSW Game 7",
                    "Oracle Arena, California",
                    R.drawable.nba_game,
                    "Travelled all the way from the Phillipines to watch Lebron comeback " +
                            "from a 3-1 deficeit",
                    CustomDate(2023, 5, 19)
                )
            )
            data.add(
                Entry(
                    "Paris Eiffel Tower Tour",
                    "Paris, France",
                    R.drawable.eiffel_tower,
                    "Exploring the iconic Eiffel Tower in the City of Love.",
                    CustomDate(2022, 11, 12)
                )
            )
            data.add(
                Entry(
                    "Beach Paradise in Bali",
                    "Bali, Indonesia",
                    R.drawable.beach_indonesia,
                    "Relaxing on Bali's stunning beaches and enjoy tropical paradise.",
                    CustomDate(2022, 3, 23)
                )
            )
            data.add(
                Entry(
                    "Historic Rome Walk",
                    "Rome, Italy",
                    R.drawable.ruins_italy,
                    "Stroll through ancient Roman ruins and landmarks.",
                    CustomDate(2012, 3, 23)
                )
            )
            return data
        }
    }
}