package com.example.simplequiz

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.example.simplequiz.databinding.FragmentHighscoreBinding
import com.example.simplequiz.databinding.FragmentQuizBinding
import com.example.simplequiz.model.FirestoreViewModel


class HighscoreFragment : Fragment() {
    private var _binding: FragmentHighscoreBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dbvm: FirestoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHighscoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbvm.highscoreList.observe(viewLifecycleOwner) { highscoredata ->
            val adapter = ArrayAdapter(requireContext(),
                R.layout.simple_list_item_1, highscoredata)
            binding.listView.adapter = adapter
        }

        binding.btnGlobal.setOnClickListener {
            dbvm.getHighscoredata(personal = false)
        }

        binding.btnPersonal.setOnClickListener {
            dbvm.getHighscoredata(personal = true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}