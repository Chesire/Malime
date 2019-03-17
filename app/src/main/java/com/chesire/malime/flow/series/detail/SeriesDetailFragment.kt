package com.chesire.malime.flow.series.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.databinding.FragmentSeriesDetailBinding
import dagger.android.support.DaggerFragment

class SeriesDetailFragment : DaggerFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSeriesDetailBinding.inflate(inflater, container, false).root
    }

    companion object {
        const val TAG = "SeriesDetailFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SeriesDetailFragment.
         */
        fun newInstance(seriesModel: SeriesModel) = SeriesDetailFragment()
    }
}
