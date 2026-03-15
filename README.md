# Android Parcelable 序列化演示

## 简介

本 Demo 演示 Android Parcelable 接口的使用，展示如何实现对象序列化并在 Activity 之间传递。

## 基本原理

Parcelable 是 Android 提供的轻量级序列化接口，用于将对象转换为字节流，以便在 Activity 之间传递或跨进程通信。

Parcelable 与 Serializable 的对比：

| 特性 | Parcelable | Serializable |
|------|------------|--------------|
| 性能 | 高（通过Binder直接传输） | 较低（使用反射） |
| 使用场景 | Activity/Fragment间传递 | 文件存储、网络传输 |
| 实现复杂度 | 较高 | 简单 |

Parcelable 的核心方法：
- `writeToParcel()`：将对象数据写入 Parcel
- `describeContents()`：返回内容描述（通常为0）
- `CREATOR`：创建对象的工厂类

## 启动和使用

### 环境要求
- Android Studio
- JDK 17
- Gradle 8.x

### 安装和运行

1. 用 Android Studio 打开项目
2. 连接 Android 设备或模拟器
3. 点击 Run 运行

### 使用方法
- 点击"传递数据"按钮跳转到第二个 Activity
- 第二个 Activity 会显示传递过来的 User 对象数据

## 教程

### 什么是 Parcelable？

Parcelable 是 Android 特有的序列化接口，相比 Java 的 Serializable 性能更好。它通过将对象数据写入 Parcel 对象，实现高效的数据传输。

Parcelable 的工作原理：
1. 实现 Parcelable 接口的类可以将其状态写入 Parcel
2. 通过 Intent 在 Activity 之间传递
3. 接收方通过 CREATOR 重新构建对象

### 为什么使用 Parcelable？

在 Android 中，当需要在 Activity 之间传递自定义对象时，必须实现序列化接口。Parcelable 是 Android 官方推荐的方式，原因：
- 性能更好：通过Binder直接传输，无需序列化/反序列化
- 更安全：不依赖Java反射机制
- 官方推荐：Google 推荐使用 Parcelable

### 实现 Parcelable

```kotlin
data class User(val name: String, val age: Int) : Parcelable {

    // 从 Parcel 中读取数据
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",  // 读取 String
        parcel.readInt()            // 读取 Int
    )

    // 将数据写入 Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    // 返回内容描述（通常返回0）
    override fun describeContents(): Int = 0

    // 伴生对象：创建 CREATOR 工厂
    companion object CREATOR : Parcelable.Creator<User> {
        // 从 Parcel 创建对象
        override fun createFromParcel(parcel: Parcel): User = User(parcel)
        // 创建对象数组
        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}
```

### 传递 Parcelable 对象

使用 Intent 的 putExtra 方法传递：

```kotlin
// 传递对象
val user = User("张三", 25)
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("user", user)
startActivity(intent)
```

接收对象：

```kotlin
// 接收对象
val user = intent.getParcelableExtra<User>("user")
```

### 注意事项

1. **data class**：Kotlin 的 data class 会自动生成 equals、hashCode、toString
2. **可空类型**：读取数据时使用 Elvis 操作符处理空值
3. **多字段**：按写入顺序读取
4. **替代方案**：简单对象可用 Bundle 传递

## 关键代码详解

### User.kt（数据模型）

```kotlin
data class User(val name: String, val age: Int) : Parcelable {

    // 1. 构造函数：从 Parcel 读取数据
    // 当 CREATOR.createFromParcel() 调用时使用
    constructor(parcel: Parcel) : this(
        // 读取 String，为空时使用空字符串
        parcel.readString() ?: "",
        // 读取 Int
        parcel.readInt()
    )

    // 2. 将对象数据写入 Parcel
    // 在 writeToParcel() 时调用
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // 按顺序写入：先 String 后 Int
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    // 3. 返回内容描述
    // 0 表示默认行为
    override fun describeContents(): Int = 0

    // 4. 伴生对象：CREATOR 工厂
    companion object CREATOR : Parcelable.Creator<User> {
        // 从 Parcel 重建对象
        override fun createFromParcel(parcel: Parcel): User = User(parcel)
        // 创建对象数组（用于批量反序列化）
        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}
```

### MainActivity.kt（发送方）

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 设置按钮点击事件
        findViewById<Button>(R.id.sendBtn).setOnClickListener {
            // 1. 创建 User 对象
            val user = User("张三", 25)

            // 2. 创建 Intent
            val intent = Intent(this, SecondActivity::class.java)

            // 3. 将 User 对象放入 Intent
            // Parcelable 对象会自动序列化
            intent.putExtra("user", user)

            // 4. 启动 SecondActivity
            startActivity(intent)
        }
    }
}
```

### SecondActivity.kt（接收方）

```kotlin
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // 1. 从 Intent 中获取 User 对象
        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("user")
        }

        // 2. 显示用户信息
        user?.let {
            findViewById<TextView>(R.id.resultText).text = "姓名: ${it.name}\n年龄: ${it.age}"
        }
    }
}
```
