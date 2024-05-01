package com.example.campwild

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PackingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packing)
        val packingTextView = findViewById<TextView>(R.id.packing_text)
        packingTextView.text = """
            <b>Equipment:</b>
            Equipment such as a tent, sleeping bag, cooking stove, route map, compass, food, and supplies, and water are very important to take with you. Some mountain stream water is safe to drink, but it is important to check your surroundings first.

            <b>Winter Camping:</b>

            <b>What to pack</b>
            - Duct tape is the winter camper’s friend. With a good duct tape, you can insulate fuel bottles, even against sub-zero conditions. You can repair broken tent poles, leaks, and holes in shoes, or use as insulation on anything you don’t want to freeze through overnight.
            - Bring a torch. Night falls fast in winter, and it is easy to get caught out. If you have room, an alternative such as a flare or glow stick is also a handy backup choice.
            - Lithium batteries are an absolute essential. These work better in cold conditions than alkaline or NiMh batteries, so pack a few spares.
            - Your rucksack is all important. Think big, but light. You need more equipment in the winter, and the pack has to be able to accommodate things like a snow shovel, stove, or an extra windbreaker without weighing you down too much.

            <b>Clothing</b>
            - The base layer is your second skin and helps to trap warm air. Wool or synthetic materials should be your go-to choice. Multiple layers can be advisable in very cold weather.
            - The mid layer provides insulation, so opt for heavy fleece if possible, with a gilet for added warmth. Don’t forget your legs will get cold too. Try lined trousers, or a pair of thermal tights. A lightweight layer containing down can help in extreme cold - think a gilet or a vest.
            - The outer layers need to be well-sealed and waterproof. Dry your jacket out before the trip, and hang it inside the tent at night if possible, to avoid the moisture inside freezing up.
            - Double up on socks, and always bring a hat, gloves, and a scarf.
            - Check the terrain of your planned route and the local weather to predict what gear you might need. Will you need walking boots? Crampons, or other specialist equipment?

            <b>Sleeping bags</b>
            - One good tip for keeping your sleeping bag extra toasty is to stuff it with your clothes for the next day before climbing in to sleep, so they do not freeze or get cold. Don’t stuff too much, though, or it could flatten the insulation in the bag.
            - Trap air by shaking the sleeping bag before you get in. More air equals more warmth!
            - Keep your face outside the sleeping bag. Moisture from your breath can decrease the insulating effect. Bring a hat or a balaclava just in case that end of you gets chilly!
            - Use hand warmers, not hot water bottles. You don’t want to risk bursting a hot water bottle in your sleeping bag, and they are difficult to pack.

            <b>Tents</b>
            - Smaller is better. This may seem counterintuitive, but the bigger the tent, the more heat will dissipate, and the colder it will be.
            - Vent the tent to avoid condensation, which could lead to icicles on the inside of your home-from-home. Best avoided!
            - Pitch your tent before dark, as there’s nothing worse than fumbling with guy ropes in the gathering dusk. Check the times for sunset and sunrise at your intended destination.
            - Get some wind cover if you can. Pitch wherever you can find some cover, from the side of a hill to the edge of a forest. Mountaineering Scotland has some great tent-pitching tips.
            - Pack down snow before pitching your tent, as the depth of fallen snow can trick you into thinking a peg is secure when it’s not.
            - Check for avalanches with the Scottish Avalanche Information Service. Extreme weather events like these can be fatal, so be prepared.
            - Make sure you have a decent water supply. Don’t rely on streams or lakes, which could be frozen over, or could be potentially unsafe to drink.
            - Falling trees and branches can be very dangerous, so in windy conditions, camping near the forest can be risky. Be aware of weather conditions and pitch with this in mind.
            - Make careful note of nearby landmarks if snow is heavy. More might fall overnight, and it’s easy to lose your bearings in whiteout conditions.

            <b>Beat the cold and damp</b>
            - Keep matches and/or a lighter dry inside metal containers. Pack extra! You never know when a lighter may run out, or a matchbox is near-empty, so play it safe. The ability to start a fire in the cold and damp is an essential asset.
            - Bring a sleeping mat to insulate you from the temperature of the ground beneath your tent.
            - Don’t sleep in all of your clothes! Your sleeping bag will warm up with your natural body heat, and if you step out of it fully clothed in the morning, all of it will dissipate. Layer up first thing, and keep everything zipped up.
        """.trimIndent()
    }
}
