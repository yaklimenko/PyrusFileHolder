package com.example.pyrusfileholder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pyrusfileholder.databinding.FragmentFileListBinding
import com.example.pyrusfileholder.di.AppModule
import com.example.pyrusfileholder.di.DaggerFileHolderComponent
import com.example.pyrusfileholder.view.FileListViewImpl
import javax.inject.Inject


class FileListFragment : Fragment(), FileListRouter {

    private var _binding: FragmentFileListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject lateinit var presenter: FileListPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        val component = DaggerFileHolderComponent
            .builder()
            .appModule(AppModule(requireActivity().application as App))
            .appComponent((requireActivity().application as App).appComponent)
            .build()

        component.inject(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fileListView = FileListViewImpl(binding)
        presenter.onAttach(fileListView, this, savedInstanceState)

        (activity as? AppCompatActivity)?.let {
            it.setSupportActionBar(binding.toolbar)
            it.supportActionBar?.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroy()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICKFILE_REQUEST_CODE) {
            data?.data?.let {
                presenter.handleReceivedUri(it)
            }
        }
    }

    override fun openFileChooser() {
        activity?. let {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }

            startActivityForResult(intent, PICKFILE_REQUEST_CODE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.saveState(outState)
    }
}

private const val PICKFILE_REQUEST_CODE = 42