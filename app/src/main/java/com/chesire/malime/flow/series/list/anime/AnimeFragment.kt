package com.chesire.malime.flow.series.list.anime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.chesire.lifecyklelog.LogLifecykle
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.databinding.FragmentAnimeBinding
import com.chesire.malime.flow.ViewModelFactory
import com.chesire.malime.flow.series.list.SeriesListener
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

@LogLifecykle
class AnimeFragment : DaggerFragment(), AnimeInteractionListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var animeAdapter: AnimeAdapter
    private lateinit var seriesListener: SeriesListener

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, viewModelFactory)
            .get(AnimeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        animeAdapter = AnimeAdapter(this)

        return FragmentAnimeBinding.inflate(inflater, container, false)
            .apply {
                fragmentAnimeRecyclerView.apply {
                    adapter = animeAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    setHasFixedSize(true)
                }
                fragmentAnimeFab.setOnClickListener {
                    seriesListener.loadSearchFragment()
                }
            }
            .root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.animeSeries.observe(
            viewLifecycleOwner,
            Observer {
                Timber.d("Anime has been updated, new count [${it.count()}]")
                animeAdapter.loadItems(it)
            }
        )
    }

    override fun animeSelected(model: SeriesModel) {
        Timber.i("Model ${model.slug} animeSelected called")
        seriesListener.loadDetailFragment(model)
    }

    override fun onPlusOne(model: SeriesModel) {
        Timber.i("Model ${model.slug} onPlusOne called")
        viewModel.updateSeries(model.userId, model.progress.inc(), model.userSeriesStatus)
    }
}
