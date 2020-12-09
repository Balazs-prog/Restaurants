package com.example.restaurants.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurants.Injection
import com.example.restaurants.R
import com.example.restaurants.databinding.FragmentListBinding
import com.example.restaurants.model.Restaurant
import com.example.restaurants.ui.RestaurantAdapter
import com.example.restaurants.ui.RestaurantViewModel
import com.google.android.material.textview.MaterialTextView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment(),RestaurantAdapter.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: RestaurantViewModel
    private lateinit var adapter:RestaurantAdapter
    private lateinit var recycler:RecyclerView
    private var fav:Int = 0
    //private lateinit var binding:FragmentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this,
                context?.let { Injection.provideViewModelFactory(it) })
                .get(RestaurantViewModel::class.java)
        adapter = RestaurantAdapter(viewModel,this)

        val binding = FragmentListBinding.inflate(inflater)
        val listFavourites = binding.root.findViewById<ImageView>(R.id.listFavourites)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        // Giving the binding access to the ViewModel
        binding.viewModel = viewModel
        // Sets the adapter of the RecyclerView
        binding.recycler.adapter = adapter

        val linearLayoutManager:LinearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recycler.layoutManager = linearLayoutManager
        setHasOptionsMenu(true)


        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        if(listFavourites.getTag() != R.drawable.heart_filled) {
            fav = 0
        }else{
            fav = 1
        }

        viewModel.searchRepo(query,fav)
        initSearch(query,binding)

        listFavourites.setOnClickListener{ listFavouritesOnClickListener(binding,query) }

        return binding.root
    }

    private fun listFavouritesOnClickListener(binding: FragmentListBinding, query: String) {
        val listFavouritess = binding.root.findViewById<ImageView>(R.id.listFavourites)
        Log.d("ListFragment", listFavouritess.tag.toString())
        if(listFavouritess.getTag() != R.drawable.heart_filled) {
            Glide.with(this)
                    .load(R.drawable.heart_filled)
                    .into(listFavouritess)
            listFavouritess.tag = R.drawable.heart_filled
            fav = 1
        }else{
            Glide.with(this)
                    .load(R.drawable.heart)
                    .into(listFavouritess)
            fav = 0
            listFavouritess.tag = R.drawable.heart
        }
        binding.recycler.scrollToPosition(0)
        viewModel.searchRepo(query,fav)
        adapter.submitList(null)
    }

    //->RestaurantAdapter
    override fun onItemClick(restaurant: Restaurant?, id: Int, itemView: View, imageFav: ImageView, position: Int) {
        if(id == R.id.imageRestaurant){
            restaurant?.image_url?.let { url ->
                RestaurantViewModel.select(restaurant!!)
                view?.let { Navigation.findNavController(it).navigate(R.id.detailsFragment) }
            }
        }else{
            if(restaurant?.favourite == 1) {
                viewModel.updateFavourites(0,restaurant?.id)
                Glide.with(itemView)
                        .load(R.drawable.heart_filled)
                        .into(imageFav)
            }else{
                viewModel.updateFavourites(1,restaurant?.id)
                Glide.with(itemView)
                        .load(R.drawable.heart)
                        .into(imageFav)
            }
            adapter.notifyItemChanged(position)
        }
    }

    override fun onResume() {
        super.onResume()
        if(arguments?.getInt("clickFavourite") == 1)
            view?.findViewById<ImageView>(R.id.listFavourites)?.performClick()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    private fun initAdapter() {
        recycler.adapter = adapter
        viewModel.restaurants.observe(viewLifecycleOwner, Observer<PagedList<Restaurant>> {
            Log.d("Activity", "list: ${it?.size}")
            showEmptyList(it?.size == 0)
            adapter.submitList(it)
        })
        viewModel.networkErrors.observe(viewLifecycleOwner, Observer<String> {
            Toast.makeText(context, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show()
        })
    }

    private fun initSearch(query: String,binding:FragmentListBinding) {
        val search_restaurant = binding.root.findViewById<EditText>(R.id.search_restaurant)
        search_restaurant?.setText(query)

        search_restaurant?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(binding)
                true
            } else {
                false
            }
        }
        search_restaurant?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(binding)
                true
            } else {
                false
            }
        }
    }

    private fun updateRepoListFromInput(binding:FragmentListBinding) {
        val search_restaurant = view?.findViewById<EditText>(R.id.search_restaurant)
        Log.d("ListFragment", "gggfdfsfsafasfasfas")
        search_restaurant?.text?.trim()?.let {
            if (it.isNotEmpty()) {
                binding.recycler.scrollToPosition(0)
                viewModel.searchRepo(it.toString(),fav)
                adapter.submitList(null)
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        val emptyList = view?.findViewById<MaterialTextView>(R.id.emptyList)
        if (show) {
            emptyList?.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            emptyList?.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Updates the filter in the [OverviewViewModel] when the menu items are selected from the
     * overflow menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_fav_menu -> RestaurantApiFilter.SHOW_FAVOURITE
                else -> RestaurantApiFilter.SHOW_ALL
            }
        )*/
        return true
    }
    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}