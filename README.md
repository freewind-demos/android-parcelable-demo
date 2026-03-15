# Android Parcelable 序列化演示

## 简介

本 Demo 演示 Android Parcelable 接口的使用。

## 基本原理

Parcelable 比 Serializable 性能更好，适合跨进程传递。

## 教程

```kotlin
data class User(val name: String, val age: Int) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString() ?: "", parcel.readInt())
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(age)
    }
    
    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User = User(parcel)
        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}
```
