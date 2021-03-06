package com.nexters.fullstack.mapper

import com.nexters.fullstack.source.LocalFile
import com.nexters.fullstack.source.PresentLocalFile

object LocalFileMapper : Mapper<PresentLocalFile, LocalFile> {
    override fun toData(item: LocalFile): PresentLocalFile {
        return PresentLocalFile(item.url)
    }

    override fun fromData(item: PresentLocalFile): LocalFile {
        return LocalFile(item.url)
    }
}