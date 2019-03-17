package com.chesire.malime.flow.series.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.databinding.FragmentSeriesDetailBinding
import com.chesire.malime.extensions.extra
import dagger.android.support.DaggerFragment

class SeriesDetailFragment : DaggerFragment() {
    private val model by extra<SeriesModel>(MODEL_KEY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSeriesDetailBinding.inflate(inflater, container, false).root

    companion object {
        const val TAG = "SeriesDetailFragment"
        private const val MODEL_KEY = "SeriesDetailFragment#MODEL_KEY"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SeriesDetailFragment.
         */
        fun newInstance(seriesModel: SeriesModel) = SeriesDetailFragment().apply {
            arguments = bundleOf(MODEL_KEY to seriesModel)
        }
    }
}
