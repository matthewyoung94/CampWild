package com.example.campwild

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        val termsAndConditionsText = """
            **Terms and Conditions**
            
            These terms and conditions ("Terms") govern your use of the Camp Wild mobile application ("App") provided by Camp Wild ("Company"). By downloading, installing, or using the App, you agree to be bound by these Terms. If you do not agree to these Terms, please do not use the App.
            
            **1. Privacy and Data Protection**
            
            a. Camp Wild respects your privacy and is committed to protecting your personal information. By using the App, you consent to the collection, storage, and processing of your personal data in accordance with our Privacy Policy.
            
            b. We may collect and use information about your usage of the App, including but not limited to location data, device information, and usage patterns, to improve our services and user experience.
            
            c. Your personal data will not be shared with third parties without your explicit consent, except as required by law or as necessary for the operation of the App.
            
            **2. User Conduct**
            
            a. You agree to use the App in compliance with all applicable laws, regulations, and these Terms.
            
            b. You are solely responsible for your conduct while using the App, including any content you post, upload, or share through the App.
            
            c. You agree not to engage in any conduct that may be harmful, offensive, or illegal, including but not limited to harassment, defamation, or infringement of intellectual property rights.
            
            **3. Intellectual Property**
            
            a. The App and all content, including but not limited to text, graphics, logos, and images, are the property of Camp Wild and are protected by copyright and other intellectual property laws.
            
            b. You may not modify, reproduce, distribute, or create derivative works based on the App or its content without Camp Wild's prior written consent.
            
            **4. Limitation of Liability**
            
            a. Camp Wild shall not be liable for any direct, indirect, incidental, special, or consequential damages arising out of or in connection with your use of the App, including but not limited to loss of data, revenue, or profits.
            
            b. The App is provided "as is" and "as available" without any warranties, express or implied, including but not limited to warranties of merchantability, fitness for a particular purpose, or non-infringement.
            
            **5. Changes to Terms**
            
            a. Camp Wild reserves the right to modify or update these Terms at any time without prior notice. Your continued use of the App after any changes to these Terms will constitute your acceptance of such changes.
            
            **6. Governing Law**
            
            a. These Terms shall be governed by and construed in accordance with the laws of England and Wales, without regard to its conflict of law provisions.
            
            **Contact Us**
            
            If you have any questions or concerns about these Terms, please contact us at m.young8@uni.brighton.ac.uk
        """.trimIndent()

        val termsAndConditionsTextView = findViewById<TextView>(R.id.termsAndConditionsTextView)
        termsAndConditionsTextView.text = termsAndConditionsText
    }
}
