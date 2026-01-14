package com.fittrack.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.fittrack.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Bottom sheet dialog for selecting filter options.
 * Supports single selection from a list of options.
 */
class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var title: String = ""
    private var options: List<String> = emptyList()
    private var selectedOption: String? = null
    private var onOptionSelected: ((String?) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvFilterTitle)
        val tvClear = view.findViewById<TextView>(R.id.tvClear)
        val optionsContainer = view.findViewById<LinearLayout>(R.id.optionsContainer)

        tvTitle.text = title

        // Clear button
        tvClear.setOnClickListener {
            onOptionSelected?.invoke(null)
            dismiss()
        }

        // Add option items
        options.forEach { option ->
            val optionView = LayoutInflater.from(context)
                .inflate(R.layout.item_filter_option, optionsContainer, false)
            
            val tvOption = optionView.findViewById<TextView>(R.id.tvOption)
            val ivCheck = optionView.findViewById<View>(R.id.ivCheck)

            tvOption.text = option
            ivCheck.visibility = if (option == selectedOption) View.VISIBLE else View.GONE
            
            if (option == selectedOption) {
                tvOption.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }

            optionView.setOnClickListener {
                onOptionSelected?.invoke(option)
                dismiss()
            }

            optionsContainer.addView(optionView)
        }
    }

    companion object {
        fun newInstance(
            title: String,
            options: List<String>,
            selectedOption: String?,
            onOptionSelected: (String?) -> Unit
        ): FilterBottomSheetFragment {
            return FilterBottomSheetFragment().apply {
                this.title = title
                this.options = options
                this.selectedOption = selectedOption
                this.onOptionSelected = onOptionSelected
            }
        }
    }
}
