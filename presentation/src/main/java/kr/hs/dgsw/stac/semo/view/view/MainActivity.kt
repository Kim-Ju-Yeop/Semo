package kr.hs.dgsw.stac.semo.view.view

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kr.hs.dgsw.stac.semo.base.BaseActivity
import kr.hs.dgsw.stac.semo.databinding.ActivityMainBinding
import kr.hs.dgsw.stac.semo.viewmodel.view.MainViewModel
import kr.hs.dgsw.stac.semo.widget.extension.longToastMessage
import kr.hs.dgsw.stac.semo.widget.extension.shortToastMessage
import kr.hs.dgsw.stac.semo.widget.extension.startActivity
import kr.hs.dgsw.stac.semo.widget.extension.startActivityExtra
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel
        get() = getViewModel(MainViewModel::class)

    override fun init() { viewModel.getMyMethod() }
    override fun observerViewModel() {
        with(viewModel) {
            onEmptyEvent.observe(this@MainActivity, Observer {
                noRecently_textView.visibility = View.VISIBLE
                noMethod_textView.visibility = View.VISIBLE

                linearLayout3.visibility = View.GONE
                linearLayout5.visibility = View.GONE
            })
            onRecentEvent.observe(this@MainActivity, Observer {
                linearLayout3.visibility = View.VISIBLE
                linearLayout5.visibility = View.VISIBLE

                noRecently_textView.visibility = View.GONE
                noMethod_textView.visibility = View.GONE

                setRecentMethod()
            })
            onAddEvent.observe(this@MainActivity, Observer {
                startActivity(applicationContext, SelectActivity::class.java)
            })
            onFailureData.observe(this@MainActivity, Observer {
                longToastMessage(it.message.toString())
            })
            myLaundryAdapter.onItemClickEvent.observe(this@MainActivity, Observer {
                startActivityExtra(Intent(applicationContext, MyLaundryActivity::class.java).putExtra("myLaundryModel", it))
            })
            onFirstEvent.observe(this@MainActivity, Observer {
                startActivityExtra(Intent(applicationContext, MyLaundryActivity::class.java).putExtra("myLaundryModel", firstLaundry.value))
            })
            onSecondEvent.observe(this@MainActivity, Observer {
                startActivityExtra(Intent(applicationContext, MyLaundryActivity::class.java).putExtra("myLaundryModel", secondLaundry.value))
            })
            onThirdEvent.observe(this@MainActivity, Observer {
                startActivityExtra(Intent(applicationContext, MyLaundryActivity::class.java).putExtra("myLaundryModel", thirdLaundry.value))
            })
        }
    }

    private fun setRecentMethod() {
        with(viewModel) {
            when {
                myLaundryList.size >= 3 -> {
                    firstLaundry.value = myLaundryList[myLaundryList.size-1]
                    Glide.with(applicationContext).load(firstLaundry.value!!.imageUri).into(imageView1)

                    secondLaundry.value = myLaundryList[myLaundryList.size-2]
                    Glide.with(applicationContext).load(secondLaundry.value!!.imageUri).into(imageView2)

                    thirdLaundry.value = myLaundryList[myLaundryList.size-3]
                    Glide.with(applicationContext).load(thirdLaundry.value!!.imageUri).into(imageView3)
                }
                myLaundryList.size >= 2 -> {
                    firstLaundry.value = myLaundryList[myLaundryList.size-1]
                    Glide.with(applicationContext).load(firstLaundry.value!!.imageUri).into(imageView1)

                    secondLaundry.value = myLaundryList[myLaundryList.size-2]
                    Glide.with(applicationContext).load(secondLaundry.value!!.imageUri).into(imageView2)
                }
                myLaundryList.size >= 1 -> {
                    firstLaundry.value = myLaundryList[myLaundryList.size-1]
                    Glide.with(applicationContext).load(firstLaundry.value!!.imageUri).into(imageView1)
                }
                else -> {
                    return@with
                }
            }
        }
    }
}