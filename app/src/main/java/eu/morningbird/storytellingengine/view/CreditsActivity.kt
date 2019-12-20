package eu.morningbird.storytellingengine.view

import android.content.Intent
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.databinding.ActivityCreditsBinding
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.adapter.CreditsAdapter
import eu.morningbird.storytellingengine.viewmodel.CreditsViewModel
import kotlinx.android.synthetic.main.activity_credits.*


class CreditsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditsBinding
    private lateinit var viewModel: CreditsViewModel
    private lateinit var adapter: CreditsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()

        initObservers()
    }

    private fun initBinding() {
        viewModel = ViewModelProvider(this).get(CreditsViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credits)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViews() {
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar, theme)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initCreditsRecycler()
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this,
            Observer { event ->
                event.get()?.let { directions -> navigate(directions) }
            }
        )

        viewModel.credits.observe(this, Observer { credits ->
            if (!credits.isNullOrEmpty()) {
                adapter.data = credits
            }
        })

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val params = toolbar.layoutParams as ConstraintLayout.LayoutParams
            val statusBarHeight = insets.systemWindowInsetTop
            params.setMargins(
                0,
                statusBarHeight,
                0,
                0
            )
            toolbar.layoutParams = params
            insets
        }
    }

    private fun initCreditsRecycler() {
        adapter = CreditsAdapter(this)
        creditsRecyclerView.adapter = adapter
        creditsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun navigate(directions: NavigationDirections) {
        val intent = Intent(this, directions.destination)
        directions.flags?.let {
            for (flag in it) intent.addFlags(flag)
        }
        directions.bundle?.let {
            intent.putExtras(it)
        }
        startActivity(intent)
        if (directions.shouldFinishCurrentActivity) this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun startActivity(intent: Intent?, bundle: Bundle?) {
        super.startActivity(intent, bundle)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
