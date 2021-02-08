package com.nexters.fullstack.usecase

import com.nexters.fullstack.repository.LabelRepository
import com.nexters.fullstack.source.DomainLabel
import com.nexters.fullstack.source.local.DomainUserLabel
import com.nexters.fullstack.usecase.base.BaseUseCase
import io.reactivex.Maybe
import io.reactivex.Single

class LoadLabelUseCase(private val labelRepository: LabelRepository) :
    BaseUseCase<Unit, Maybe<List<DomainUserLabel>>> {
    override fun buildUseCase(params: Unit): Maybe<List<DomainUserLabel>> {
        return labelRepository.load()
    }
}