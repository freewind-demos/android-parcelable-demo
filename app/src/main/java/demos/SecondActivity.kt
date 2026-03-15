package demos

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        
        val user = intent.getParcelableExtra<User>("user")
        findViewById<TextView>(R.id.textView).text = "姓名: ${user?.name}, 年龄: ${user?.age}"
    }
}
