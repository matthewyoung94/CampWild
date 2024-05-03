package com.example.campwild

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class PackingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packing)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.title = "Camp Wild"
        val packingTextView = findViewById<TextView>(R.id.packing_text)
        val builder = SpannableStringBuilder()
        val equipmentHeader = "Equipment:\n"
        val equipmentContent = "Essential gear includes your tent, sleeping bag, cooking stove, map, compass, food, and water.\n" +
                "Always have duct tape on hand for quick fixes and insulation.\n" +
                "Don't leave without a torch, lithium batteries, and a roomy backpack.\n\n"
        builder.append(equipmentHeader)
        builder.append(equipmentContent)
        builder.setSpan(StyleSpan(Typeface.BOLD), 0, equipmentHeader.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val winterCampingHeader = "Camping:\n"
        val winterCampingContent = "What to pack:\n" +
                "- Duct tape is a camper’s best friend. Use it for quick fixes like tent repairs, shoe patches, or insulating gear against cold weather.\n" +
                "- Always bring a torch or flashlight, especially for navigating at night. Consider backup options like flares or glow sticks.\n" +
                "- Lithium batteries are reliable in various conditions, so pack extras for your devices.\n" +
                "- Choose a spacious yet lightweight rucksack that can accommodate extra gear without weighing you down.\n\n" +
                "Clothing:\n" +
                "- Start with a base layer of wool or synthetic material to trap warm air close to your body. Layer up with fleece and consider adding a gilet for extra insulation.\n" +
                "- Ensure your outer layers are waterproof and well-sealed. Dry out jackets before the trip and hang them inside the tent at night to prevent moisture from freezing.\n" +
                "- Don't forget extra socks, a hat, gloves, and a scarf for added warmth.\n" +
                "- Research the terrain and weather conditions to determine if you need specialized gear like walking boots or crampons.\n\n"
        builder.append(winterCampingHeader)
        builder.append(winterCampingContent)
        builder.setSpan(StyleSpan(Typeface.BOLD), builder.length - winterCampingContent.length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val sleepingBagsHeader = "Sleeping bags:\n"
        val sleepingBagsContent = "- Stuff your sleeping bag with tomorrow's clothes to keep them warm and handy. But don’t overstuff it!\n" +
                "- Shake your sleeping bag to trap more air inside before you hop in. More air means more warmth!\n" +
                "- Keep your face out of the bag to avoid moisture buildup. Bring a hat just in case!\n" +
                "- Use hand warmers instead of hot water bottles to avoid any messy accidents.\n"
        builder.append(sleepingBagsHeader)
        builder.append(sleepingBagsContent)
        builder.setSpan(StyleSpan(Typeface.BOLD), builder.length - sleepingBagsContent.length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tentsHeader = "Tents:\n"
        val tentsContent = "- Smaller tents are cozier. Big ones tend to get colder since they lose heat quickly.\n" +
                "- Keep your tent ventilated to prevent condensation. Nobody wants icicles inside their tent!\n" +
                "- Set up your tent before it gets dark. Trust me, wrestling with ropes in the dark is no fun.\n" +
                "- Find some wind cover if you can. Look for spots like hillsides or the edge of a forest.\n" +
                "- Flatten the snow before pitching your tent. It's easy to think your pegs are secure when they're not.\n" +
                "- Check for avalanches with the Scottish Avalanche Information Service. Safety first!\n" +
                "- Bring enough water. Don’t count on streams or lakes, they might be frozen or unsafe.\n" +
                "- Avoid camping near trees in windy weather. Falling branches are no joke.\n" +
                "- Take note of landmarks if it's snowing heavily. It's easy to get lost in a whiteout.\n\n"
        builder.append(tentsHeader)
        builder.append(tentsContent)
        builder.setSpan(StyleSpan(Typeface.BOLD), builder.length - tentsContent.length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val beatTheColdHeader = "Beat the cold and damp:\n"
        val beatTheColdContent = "- Keep your matches or lighter in a waterproof container. And pack extra, just in case!\n" +
                "- Use a sleeping mat to stay warm. It insulates you from the cold ground.\n" +
                "- Don’t sleep in all your clothes! Your sleeping bag warms up with your body heat, so layer up in the morning instead.\n"
        builder.append(beatTheColdHeader)
        builder.append(beatTheColdContent)
        builder.setSpan(StyleSpan(Typeface.BOLD), builder.length - beatTheColdContent.length, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        packingTextView.text = builder
    }
}
