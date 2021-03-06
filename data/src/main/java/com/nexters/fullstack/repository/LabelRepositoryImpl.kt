package com.nexters.fullstack.repository

import com.nexters.fullstack.local.LabelaryLocalDataSource
import com.nexters.fullstack.remote.LabelaryRemoteDataSource
import com.nexters.fullstack.source.DomainLabel
import com.nexters.fullstack.source.data.LocalImageDomain
import com.nexters.fullstack.source.local.DomainUserImage
import com.nexters.fullstack.source.local.DomainUserLabel
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class LabelRepositoryImpl(
    private val labelaryLocalDataSourceLabel: LabelaryLocalDataSource.Label
) : LabelRepository {

//    override fun remoteLoad(): Single<List<DomainLabel>> {
//        return labelaryRemoteDataSource.load()
//    }
//
//    override fun remoteSave(labels: Pair<List<DomainLabel>, LocalImageDomain>): Completable {
//        return labelaryRemoteDataSource.save(labels)
//    }

    override fun update(label: DomainUserLabel): Completable {
        return labelaryLocalDataSourceLabel.update(label)
    }

    override fun save(label: DomainUserLabel): Completable {
        return labelaryLocalDataSourceLabel.save(label)
    }

    override fun delete(label: DomainUserLabel): Completable {
        return labelaryLocalDataSourceLabel.delete(label)
    }

    override fun load(): Maybe<List<DomainUserLabel>> {
        return labelaryLocalDataSourceLabel.labelLoad()
    }
}