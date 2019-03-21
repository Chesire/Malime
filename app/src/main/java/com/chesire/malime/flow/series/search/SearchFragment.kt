package com.chesire.malime.flow.series.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.chesire.malime.AsyncState
import com.chesire.malime.core.flags.UserSeriesStatus
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.databinding.FragmentSearchBinding
import com.chesire.malime.flow.ViewModelFactory
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class SearchFragment : DaggerFragment(), SearchInteractionListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var searchAdapter: SearchAdapter

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, viewModelFactory)
            .get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchAdapter = SearchAdapter(this)

        return FragmentSearchBinding.inflate(inflater, container, false)
            .apply {
                vm = viewModel
                lifecycleOwner = viewLifecycleOwner
                fragmentSearchRecyclerView.apply {
                    adapter = searchAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    setHasFixedSize(true)
                }
            }
            .root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.searchResults.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is AsyncState.Success -> {
                        Timber.d("Search results has been updated, new count [${it.data.count()}]")
                        searchAdapter.loadItems(it.data)
                    }
                    is AsyncState.Error -> Timber.w("Search failed: [${it.error}]")
                    is AsyncState.Loading -> Timber.v("Display load indicator")
                }
            }
        )
    }

    override fun addSeries(model: SeriesModel) {
        Timber.i("Model ${model.slug} addSeries called")
        // Default to UserSeriesStatus.Current for now
        viewModel.addSeries(model, UserSeriesStatus.Current)
    }

    companion object {
        const val TAG = "SearchFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SearchFragment.
         */
        fun newInstance() = SearchFragment()
    }
}