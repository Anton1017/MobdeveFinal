package com.example.mobdevemco.model
import android.net.Uri
import com.example.mobdevemco.R
import java.util.ArrayList

class DataGenerator {
    companion object {
        fun loadEntryData(): ArrayList<Entry> {
            val data = ArrayList<Entry>()
            val images1 = ArrayList<EntryImages>()
            images1.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            images1.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            images1.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            images1.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            images1.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            data.add(
                Entry(
                    "Mario Kart IRL",
                    "Tokyo, Japan",
                    images1,
                    "Being able to drive the karts like in Mario " +
                            "Kart reminds me of my Childhood" + " Being able to drive the karts like in Mario " +
                            "Kart reminds me of my Childhood" + " Being able to drive the karts like in Mario " +
                            "Kart reminds me of my Childhood" + " Being able to drive the karts like in Mario " +
                            "Kart reminds me of my Childhood",
                    CustomDateTime(2023, 9, 12, 12, 20)
                )
            )
            data.add(
                Entry(
                    "Mario Kart IRL",
                    "Tokyo, Japan",
                    images1,
                    "Being able to drive the karts like in Mario :)" +
                            "Kart reminds me of my Childhood, excited to be like Mario while my friend is Luigi",
                    CustomDateTime(2023, 9, 12,12, 20)
                )
            )
            data.add(
                Entry(
                    "Mario Kart IRL",
                    "Tokyo, Japan",
                    images1,
                    "Being able to drive the karts like in Mario :)" +
                            "Kart reminds me of my Childhood",
                    CustomDateTime(2023, 9, 12,12, 20)
                )
            )
            data.add(
                Entry(
                    "Mario Kart IRL",
                    "Tokyo, Japan",
                    images1,
                    "Being able to drive the karts like in Mario :)" +
                            "Kart reminds me of my Childhood",
                    CustomDateTime(2023, 9, 12,12, 20)
                )
            )
            data.add(
                Entry(
                    "Mario Kart IRL",
                    "Tokyo, Japan",
                    images1,
                    "Being able to drive the karts like in Mario :)" +
                            "Kart reminds me of my Childhood",
                    CustomDateTime(2023, 9, 12,12, 20)
                )
            )
//            val images2 = ArrayList<EntryImages>()
//            images2.add(EntryImages(R.mipmap.nba_game))
//            images2.add(EntryImages(R.mipmap.nba_game))
//            images2.add(EntryImages(R.mipmap.nba_game))
//            images2.add(EntryImages(R.mipmap.nba_game))
//            images2.add(EntryImages(R.mipmap.nba_game))
//            images2.add(EntryImages(R.mipmap.nba_game))
//
//            data.add(
//                Entry(
//                    "CAVS vs GSW Game 7",
//                    "Oracle Arena, California",
//                    images2,
//                    "Travelled all the way from the Phillipines to watch Lebron comeback " +
//                            "from a 3-1 deficeit",
//                    com.example.mobdevemco.model.CustomDate(2023, 5, 19)
//                )
//            )
//            val images3 = ArrayList<EntryImages>()
//            images3.add(EntryImages(R.mipmap.eiffel_tower))
//            images3.add(EntryImages(R.mipmap.eiffel_tower))
//            data.add(
//                Entry(
//                    "Paris Eiffel Tower Tour",
//                    "Paris, France",
//                    images3,
//                    "Exploring the iconic Eiffel Tower in the City of Love.",
//                    com.example.mobdevemco.model.CustomDate(2022, 11, 12)
//                )
//            )
//            val images4 = ArrayList<EntryImages>()
//            images4.add(EntryImages(R.mipmap.beach_indonesia))
//            images4.add(EntryImages(R.mipmap.beach_indonesia))
//            data.add(
//                Entry(
//                    "Beach Paradise in Bali",
//                    "Bali, Indonesia",
//                    images4,
//                    "Relaxing on Bali's stunning beaches and enjoy tropical paradise.",
//                    com.example.mobdevemco.model.CustomDate(2022, 3, 23)
//                )
//            )
//            val images5 = ArrayList<EntryImages>()
//            images5.add(EntryImages(R.mipmap.ruins_italy))
//            images5.add(EntryImages(R.mipmap.ruins_italy))
//            data.add(
//                Entry(
//                    "Historic Rome Walk",
//                    "Rome, Italy",
//                    images5,
//                    "Stroll through ancient Roman ruins and landmarks.",
//                    com.example.mobdevemco.model.CustomDate(2012, 3, 23)
//                )
//            )
//            val images6 = ArrayList<EntryImages>()
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            images6.add(EntryImages(R.mipmap.bee))
//            data.add(
//                Entry(
//                    "Bee Movie Script",
//                    "Pyongyang, North Korea",
//                    images6,
//                    "According to all known laws of aviation, there is no way a bee should be able to fly.\n" +
//                            "Its wings are too small to get its fat little body off the ground.\n" +
//                            "The bee, of course, flies anyway because bees don't care what humans think is impossible.",
//                    com.example.mobdevemco.model.CustomDate(2012, 11, 21)
//                )
//            )
            return data
        }
        fun loadEntryImageData(): ArrayList<EntryImages>{
            val imagedata = ArrayList<EntryImages>()
            imagedata.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            imagedata.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            imagedata.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            imagedata.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            imagedata.add(EntryImages(Uri.parse("android.resource://com.example.mobdevemco/" + R.mipmap.mario_kart)))
            return imagedata
        }

    }
}