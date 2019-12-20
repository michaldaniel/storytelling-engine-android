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
import eu.morningbird.storytellingengine.adapter.AboutsAdapter
import eu.morningbird.storytellingengine.databinding.ActivityAboutBinding
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.viewmodel.AboutViewModel
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var viewModel: AboutViewModel

    private lateinit var gameInfoAboutsAdapter: AboutsAdapter
    private lateinit var developerInfoAboutsAdapter: AboutsAdapter
    private lateinit var supportInfoAboutsAdapter: AboutsAdapter
    private lateinit var legalInfoAboutsAdapter: AboutsAdapter
    private lateinit var engineInfoAboutsAdapter: AboutsAdapter
    private lateinit var attributionsInfoAboutsAdapter: AboutsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()
        initObservers()
    }

    private fun initBinding() {
        viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)
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
        initRecyclers()
    }

    private fun initRecyclers() {
        gameInfoAboutsAdapter = AboutsAdapter(this)
        gameInfoRecyclerView.adapter = gameInfoAboutsAdapter
        gameInfoRecyclerView.layoutManager = LinearLayoutManager(this)

        developerInfoAboutsAdapter = AboutsAdapter(this)
        developerInfoRecyclerView.adapter = developerInfoAboutsAdapter
        developerInfoRecyclerView.layoutManager = LinearLayoutManager(this)

        supportInfoAboutsAdapter = AboutsAdapter(this)
        supportInfoRecyclerView.adapter = supportInfoAboutsAdapter
        supportInfoRecyclerView.layoutManager = LinearLayoutManager(this)

        legalInfoAboutsAdapter = AboutsAdapter(this)
        legalInfoRecyclerView.adapter = legalInfoAboutsAdapter
        legalInfoRecyclerView.layoutManager = LinearLayoutManager(this)

        engineInfoAboutsAdapter = AboutsAdapter(this)
        engineInfoRecyclerView.adapter = engineInfoAboutsAdapter
        engineInfoRecyclerView.layoutManager = LinearLayoutManager(this)

        attributionsInfoAboutsAdapter = AboutsAdapter(this)
        attributionsInfoRecyclerView.adapter = attributionsInfoAboutsAdapter
        attributionsInfoRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this,
            Observer { event ->
                event.get()?.let { directions -> navigate(directions) }
            }
        )
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

        viewModel.gameInfoAboutItems.observe(this, Observer { items ->
            items?.let {
                gameInfoAboutsAdapter.data = it
            }
        })
        viewModel.developerInfoAboutItems.observe(this, Observer { items ->
            items?.let {
                developerInfoAboutsAdapter.data = it
            }
        })
        viewModel.supportInfoAboutItems.observe(this, Observer { items ->
            items?.let {
                supportInfoAboutsAdapter.data = it
            }
        })
        viewModel.legalInfoAboutItems.observe(this, Observer { items ->
            items?.let {
                legalInfoAboutsAdapter.data = it
            }
        })
        viewModel.engineInfoAboutItems.observe(this, Observer { items ->
            items?.let {
                engineInfoAboutsAdapter.data = it
            }
        })
        viewModel.attributionsInfoAboutItems.observe(this, Observer { items ->
            items?.let {
                attributionsInfoAboutsAdapter.data = it
            }
        })
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
