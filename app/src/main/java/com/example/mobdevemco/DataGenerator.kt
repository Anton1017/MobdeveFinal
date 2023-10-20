package com.example.mobdevemco
import java.util.ArrayList
import CustomDate
class DataGenerator {
    companion object {
        fun loadEntryData(): ArrayList<Entry> {
            val data = ArrayList<Entry>()
            val images1 = ArrayList<Int>()
            images1.add(R.mipmap.mario_kart)
            images1.add(R.mipmap.mario_kart)
            data.add(
                Entry(
                    "Maria Kart IRL",
                    "Tokyo, Japan",
                    images1,
                    "Being able to drive the karts like in Mario :)" +
                            "Kart reminds me of my Childhood",
                    CustomDate(2023, 9, 12)
                )
            )
            val images2 = ArrayList<Int>()
            images2.add(R.mipmap.nba_game)
            images2.add(R.mipmap.nba_game)
            data.add(
                Entry(
                    "CAVS vs GSW Game 7",
                    "Oracle Arena, California",
                    images2,
                    "Travelled all the way from the Phillipines to watch Lebron comeback " +
                            "from a 3-1 deficeit",
                    CustomDate(2023, 5, 19)
                )
            )
            val images3 = ArrayList<Int>()
            images3.add(R.mipmap.eiffel_tower)
            images3.add(R.mipmap.eiffel_tower)
            data.add(
                Entry(
                    "Paris Eiffel Tower Tour",
                    "Paris, France",
                    images3,
                    "Exploring the iconic Eiffel Tower in the City of Love.",
                    CustomDate(2022, 11, 12)
                )
            )
            val images4 = ArrayList<Int>()
            images4.add(R.mipmap.beach_indonesia)
            images4.add(R.mipmap.beach_indonesia)
            data.add(
                Entry(
                    "Beach Paradise in Bali",
                    "Bali, Indonesia",
                    images4,
                    "Relaxing on Bali's stunning beaches and enjoy tropical paradise.",
                    CustomDate(2022, 3, 23)
                )
            )
            val images5 = ArrayList<Int>()
            images5.add(R.mipmap.beach_indonesia)
            images5.add(R.mipmap.beach_indonesia)
            data.add(
                Entry(
                    "Historic Rome Walk",
                    "Rome, Italy",
                    images5,
                    "Stroll through ancient Roman ruins and landmarks.",
                    CustomDate(2012, 3, 23)
                )
            )
            return data
        }
    }
}