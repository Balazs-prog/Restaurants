package com.example.restaurants.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.restaurants.Injection
import com.example.restaurants.R
import com.example.restaurants.databinding.FragmentDetailsBinding
import com.example.restaurants.ui.RestaurantViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: RestaurantViewModel
    //private lateinit var viewModel: DetailsViewModel

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
        // Inflate the layout for this fragment
        @Suppress("UNUSED_VARIABLE")
        val application = requireNotNull(activity).application
        val binding = FragmentDetailsBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this,
                context?.let { Injection.provideViewModelFactory(it) })
                .get(RestaurantViewModel::class.java)
        binding.lifecycleOwner = this
        binding.property = viewModel.selected_.value


        val map = binding.root.findViewById<Button>(R.id.map)
        map.setOnClickListener{ mapClickListener(binding) }
        val changeImage = binding.root.findViewById<Button>(R.id.changeImage)

        viewModel.selected_.observe(viewLifecycleOwner) {
                Glide.with(this)
                        .load(it.image_url)
                        .into(view?.findViewById<ImageView>(R.id.restaurantImage)!!)
        }

        changeImage.setOnClickListener{changeImageClickListener(binding)}

        return binding.root
    }

    private fun pickImageFromGallery():Boolean {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
        return true
    }

    fun changeImageClickListener(binding:FragmentDetailsBinding){
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                    binding.root.context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                pickImageFromGallery()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_CODE)
            }
        }
    }
    fun mapClickListener(binding:FragmentDetailsBinding){
        val position = binding.property?.lat.toString()+","+binding.property?.lng.toString()
        val bundle = bundleOf("position" to position)
        binding.root?.let { Navigation.findNavController(it).navigate(R.id.mapsFragment, bundle) }
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                }
            }
        }
    }
    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
           //view?.findViewById<ImageView>(R.id.restaurantImage)?.setImageURI(data?.data)
            viewModel.updateImageRestaurant(data?.data.toString(),viewModel.selected_.value?.id)

        }
    }
    override fun onResume() {
        super.onResume()
        /*
        val restaurant = arguments?.getInt("position")
        val name = view?.findViewById<TextView>(R.id.restaurantName)
        val type = view?.findViewById<TextView>(R.id.restaurantAdress)
        val calories = view?.findViewById<TextView>(R.id.restaurantCity)
        val description = view?.findViewById<TextView>(R.id.restaurantState)
        val image = view?.findViewById<ImageView>(R.id.restaurantImage)
        image?.setImageResource(R.drawable.ic_launcher_background)
        if (name != null) {
           // name.text = obj?.text1
        }
        if (type != null) {
            //type.text = obj?.text2
        }
        if (calories != null) {
            //calories.text = obj?.text3.toString()
        }
        if (description != null) {
            //description.text = obj?.description
        }

         */
        //val restaurantId = arguments?.getLong("id")
        val name = view?.findViewById<TextView>(R.id.restaurantName)
        Log.d("DetailsFragment", "Restaurant name: ${name?.text}")

    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}