package com.chesire.malime.flow.overview.anime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.chesire.malime.databinding.FragmentAnimeBinding
import com.chesire.malime.flow.ViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_anime.fragmentAnimeRecyclerView
import timber.log.Timber
import javax.inject.Inject

class AnimeFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var animeAdapter: AnimeAdapter

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
        animeAdapter = AnimeAdapter()

        return FragmentAnimeBinding.inflate(inflater, container, false)
            .apply {
                fragmentAnimeRecyclerView.apply {
                    adapter = animeAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    setHasFixedSize(true)
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

    companion object {
        const val TAG = "AnimeFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AnimeFragment.
         */
        fun newInstance() = AnimeFragment()
    }
}
