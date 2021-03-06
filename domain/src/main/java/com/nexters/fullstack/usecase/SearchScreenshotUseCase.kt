package com.nexters.fullstack.usecase

import com.nexters.fullstack.repository.ImageRepository
import com.nexters.fullstack.source.data.LocalImageDomain
import com.nexters.fullstack.usecase.base.BaseUseCase
import kotlinx.coroutines.flow.Flow

class SearchScreenshotUseCase(private val ImageRepository : ImageRepository) : BaseUseCase<Unit, Flow<List<LocalImageDomain>>> {
    override fun buildUseCase(params : Unit): Flow<List<LocalImageDomain>> {
        TODO("Not yet implemented")
    }
}