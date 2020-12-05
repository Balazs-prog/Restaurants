package com.example.restaurants.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Annotation
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.restaurants.Injection
import com.example.restaurants.R
import com.example.restaurants.databinding.FragmentInformationBinding
import com.example.restaurants.model.Restaurant
import com.example.restaurants.ui.RestaurantViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InformationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: RestaurantViewModel
    private lateinit var binding: FragmentInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private fun changeEditText(editText: EditText) {
        editText.isFocusable = !editText.isFocusable
        editText.isEnabled = !editText.isEnabled
        editText.isCursorVisible = !editText.isCursorVisible
        if(!editText.isEnabled) {
            editText.setBackgroundColor(Color.TRANSPARENT)
        }
        else{
            editText.setBackgroundColor(android.R.attr.textColorPrimary)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        @Suppress("UNUSED_VARIABLE")
        val application = requireNotNull(activity).application
        viewModel = ViewModelProviders.of(this,
            context?.let { Injection.provideViewModelFactory(it) })
            .get(RestaurantViewModel::class.java)
        binding = FragmentInformationBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.searchUserAndFavourites(1)
        binding.root.findViewById<Button>(R.id.favouritesButton).setOnClickListener {
            val bundle = bundleOf("clickFavourite" to 1)
                view?.let { Navigation.findNavController(it).navigate(R.id.listFragment,bundle) }
            }
        binding.root.findViewById<Button>(R.id.editInformation).setOnClickListener {
            val phone = view?.findViewById<EditText>(R.id.profilePhone)!!
            phone?.isEnabled = !phone?.isEnabled!!

            val email = view?.findViewById<EditText>(R.id.profileEmail)!!
            email?.isEnabled  = !email?.isEnabled!!

            val address = view?.findViewById<EditText>(R.id.profileAddress)!!
            address?.isEnabled  = !address?.isEnabled!!

            val name = view?.findViewById<EditText>(R.id.profileName)!!
            name?.isEnabled  = !name?.isEnabled!!


            if(!phone?.isEnabled!!){
                view?.findViewById<Button>(R.id.profilePicChange)?.visibility = View.INVISIBLE
                viewModel.updateUser(name.text.toString(),email.text.toString(),phone.text.toString(),address.text.toString(),viewModel.user.value?.id)
                Log.d("InformationFragment", viewModel.user.value?.id.toString())
            }else{
                view?.findViewById<Button>(R.id.profilePicChange)?.visibility = View.VISIBLE
            }
        }

        binding.root.findViewById<Button>(R.id.profilePicChange).setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(context?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions,
                            PERMISSION_CODE
                    )
                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }



        return binding.root
    }
    private fun pickImageFromGallery():Boolean {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, InformationFragment.IMAGE_PICK_CODE)
        return true
    }
    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            InformationFragment.PERMISSION_CODE -> {
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
        if (resultCode == Activity.RESULT_OK && requestCode == InformationFragment.IMAGE_PICK_CODE){
            Log.d("InformationFragment", data?.data.toString())
            Log.d("InformationFragment", viewModel.user.value?.id.toString())
            Glide.with(this)
                    .load(data?.data)
                    .into(view?.findViewById<ImageView>(R.id.profilePicture)!!)
            binding.viewModel?.updateImageUser(data?.data.toString(),viewModel.user.value?.id)
            //view?.findViewById<ImageView>(R.id.profilePicture)!!.setImageURI(data?.data)
        }
    }
    override fun onResume() {
        super.onResume()
        //listFavourites()
    }
    fun listFavourites(){
        val textView: TextView = view?.findViewById(R.id.profileFavourites)!!
        var restaurantString:String = "Favourites: "
        val restaurants:ArrayList<Restaurant> = ArrayList<Restaurant>()
        for(restaurant in viewModel?.searchFavouriteRestaurants()?.value!!){
            if(restaurant.favourite == 1){
                restaurantString += restaurant.name + ","
                restaurants.add(restaurant)
            }
        }
        restaurantString = restaurantString.substring(0, restaurantString.length - 1)

        val fullText = restaurantString as SpannedString
        val spannableString = SpannableString(fullText)

        val annotations = fullText.getSpans(0, fullText.length, Annotation::class.java)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                TODO("Not yet implemented")
            }
        }
        for(restaurant in restaurants) {
            annotations?.find { it.value == restaurant.name }?.let {
                spannableString.apply {
                    setSpan(
                            clickableSpan,
                            fullText.getSpanStart(it),
                            fullText.getSpanEnd(it),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    setSpan(ForegroundColorSpan(Color.BLUE),
                            fullText.getSpanStart(it),
                            fullText.getSpanEnd(it),
                            0
                    )
                }
            }
            textView.apply {
                text = spannableString
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
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
         * @return A new instance of fragment InformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InformationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}