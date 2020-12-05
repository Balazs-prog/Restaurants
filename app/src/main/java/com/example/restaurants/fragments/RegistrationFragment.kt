package com.example.restaurants.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.restaurants.Injection
import com.example.restaurants.MainActivity
import com.example.restaurants.R
import com.example.restaurants.databinding.FragmentRegistrationBinding
import com.example.restaurants.ui.RestaurantViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: RestaurantViewModel

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
        val binding = FragmentRegistrationBinding.inflate(inflater)
        (activity as MainActivity?)?.setBottomNavInvisible()
        viewModel = ViewModelProviders.of(this,
            context?.let { Injection.provideViewModelFactory(it) })
            .get(RestaurantViewModel::class.java)
        val register = binding.root.findViewById<Button>(R.id.register)
        register.setOnClickListener{
            val name = binding.root.findViewById<EditText>(R.id.name)
            val address = binding.root.findViewById<EditText>(R.id.address)
            val phone = binding.root.findViewById<EditText>(R.id.phone)
            val email = binding.root.findViewById<EditText>(R.id.email)
            viewModel.addUser(name.text.toString(),email.text.toString(),address.text.toString(),phone.text.toString(),"temp")

            binding.root.let { Navigation.findNavController(it).navigate(R.id.listFragment) }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity?)?.setBottomNavVisible()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}