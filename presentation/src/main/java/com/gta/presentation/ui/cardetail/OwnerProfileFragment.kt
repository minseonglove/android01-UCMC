package com.gta.presentation.ui.cardetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gta.presentation.R
import com.gta.presentation.databinding.FragmentOwnerProfileBinding
import com.gta.presentation.model.ReportEventState
import com.gta.presentation.ui.base.BaseFragment
import com.gta.presentation.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OwnerProfileFragment : BaseFragment<FragmentOwnerProfileBinding>(
    R.layout.fragment_owner_profile
) {

    private val viewModel: OwnerProfileViewModel by viewModels()
    private val carListAdapter by lazy { CarListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.rvCars.adapter = carListAdapter.apply {
            setItemClickListener(
                object : CarListAdapter.OnItemClickListener {
                    override fun onClick(id: String) {
                        findNavController().navigate(
                            OwnerProfileFragmentDirections
                                .actionOwnerProfileFragmentToCarDetailFragment(id)
                        )
                    }
                }
            )
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.reportEvent.collectLatest { result ->
                when (result) {
                    is ReportEventState.Success -> {
                        sendSnackBar(message = getString(R.string.report_success))
                    }
                    is ReportEventState.Cooldown -> {
                        sendSnackBar(message = getString(R.string.report_cooldown, result.cooldown))
                    }
                    is ReportEventState.Error -> {
                        sendSnackBar(message = getString(R.string.report_fail))
                    }
                }
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.carList.collectLatest {
                carListAdapter.submitList(it)
            }
        }
    }
}
