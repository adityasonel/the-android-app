package prj.adityasnl.theandroidapp.di

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import prj.adityasnl.theandroidapp.adapter.ListAdapter
import prj.adityasnl.theandroidapp.ui.main.MainViewModel
import prj.adityasnl.theandroidapp.utils.MyArrayList

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }

    factory {
            (items: MyArrayList) -> ListAdapter(items)
    }
}