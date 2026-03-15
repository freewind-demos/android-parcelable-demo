package demos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        findViewById<Button>(R.id.sendBtn).setOnClickListener {
            val user = User("张三", 25)
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }
}
