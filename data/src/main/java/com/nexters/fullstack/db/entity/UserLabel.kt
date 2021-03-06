package com.nexters.fullstack.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexters.fullstack.db.TableName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = TableName.LABEL)
data class UserLabel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "color")
    val color: String,

    @ColumnInfo(name = "text")
    val text: String
) : Parcelable
