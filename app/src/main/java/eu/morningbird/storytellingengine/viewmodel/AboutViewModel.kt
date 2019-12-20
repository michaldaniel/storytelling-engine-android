package eu.morningbird.storytellingengine.viewmodel

import android.content.ActivityNotFoundException
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.morningbird.storytellingengine.BuildConfig
import eu.morningbird.storytellingengine.R
import eu.morningbird.storytellingengine.model.AboutItem
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import eu.morningbird.storytellingengine.util.PlayStoreActions
import eu.morningbird.storytellingengine.view.CreditsActivity


class AboutViewModel : ViewModel() {
    val navigationEvent: MutableLiveData<Event<NavigationDirections>> = MutableLiveData()

    val gameInfoAboutItems: MutableLiveData<List<AboutItem>> = MutableLiveData()
    val developerInfoAboutItems: MutableLiveData<List<AboutItem>> = MutableLiveData()
    val supportInfoAboutItems: MutableLiveData<List<AboutItem>> = MutableLiveData()
    val legalInfoAboutItems: MutableLiveData<List<AboutItem>> = MutableLiveData()
    val engineInfoAboutItems: MutableLiveData<List<AboutItem>> = MutableLiveData()
    val attributionsInfoAboutItems: MutableLiveData<List<AboutItem>> = MutableLiveData()

    val version: MutableLiveData<String> = MutableLiveData(BuildConfig.VERSION_NAME)


    init {
        val gameInfoAboutItems: MutableList<AboutItem> = ArrayList()
        gameInfoAboutItems.add(AboutItem(R.string.about_website, icon = R.drawable.ic_url, url = "https://www.morningbird.eu"))
        gameInfoAboutItems.add(AboutItem(R.string.about_google_play_store, icon = R.drawable.ic_playstore, onClickListener = View.OnClickListener { view ->
            PlayStoreActions(view.context).openRate()
        }))
        gameInfoAboutItems.add(AboutItem(R.string.about_credits, icon = R.drawable.ic_attribution, onClickListener = View.OnClickListener { view ->
            val intent = Intent(view.context, CreditsActivity::class.java)
            navigationEvent.postValue(
                Event(
                    NavigationDirections(
                        intent,
                        CreditsActivity::class.java,
                        null,
                        null,
                        false
                    )
                )
            )
        }))
        this.gameInfoAboutItems.postValue(gameInfoAboutItems)

        val developerInfoAboutItems: MutableList<AboutItem> = ArrayList()
        developerInfoAboutItems.add(AboutItem(R.string.about_email, icon = R.drawable.ic_mail, onClickListener = View.OnClickListener { view ->
            val intentMail = Intent(Intent.ACTION_SEND)
            intentMail.type = "message/rfc822"
            intentMail.putExtra(
                Intent.EXTRA_EMAIL, arrayOf(
                    "contact@morningbird.eu"
                )
            )
            try {
                view.context.startActivity(
                    Intent.createChooser(
                        intentMail,
                        view.context.getString(R.string.about_select_mail_client)
                    )
                )
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    view.context,
                    view.context.getString(R.string.about_mail_intent_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }))
        developerInfoAboutItems.add(AboutItem(R.string.about_website, icon = R.drawable.ic_url, url = "https://morningbird.eu"))
        developerInfoAboutItems.add(AboutItem(R.string.about_google_play_store, icon = R.drawable.ic_playstore, onClickListener = View.OnClickListener { view ->
            PlayStoreActions(view.context).openMore()
        }))
        this.developerInfoAboutItems.postValue(developerInfoAboutItems)

        val supportInfoAboutItems: MutableList<AboutItem> = ArrayList()
        supportInfoAboutItems.add(AboutItem(R.string.about_bug_reports, icon = R.drawable.ic_bug, url = "https://github.com"))
        supportInfoAboutItems.add(AboutItem(R.string.about_email, icon = R.drawable.ic_mail, onClickListener = View.OnClickListener { view ->
            val intentMail = Intent(Intent.ACTION_SEND)
            intentMail.type = "message/rfc822"
            intentMail.putExtra(
                Intent.EXTRA_EMAIL, arrayOf(
                    "contact@morningbird.eu"
                )
            )
            intentMail.putExtra(Intent.EXTRA_SUBJECT, view.context.getString(R.string.about_support_mail_title));
            intentMail.putExtra(Intent.EXTRA_TEXT, view.context.getString(R.string.about_support_mail_message));
            try {
                view.context.startActivity(
                    Intent.createChooser(
                        intentMail,
                        view.context.getString(R.string.about_select_mail_client)
                    )
                )
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    view.context,
                    view.context.getString(R.string.about_mail_intent_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }))
        this.supportInfoAboutItems.postValue(supportInfoAboutItems)

        val legalInfoAboutItems: MutableList<AboutItem> = ArrayList()
        legalInfoAboutItems.add(AboutItem(R.string.about_privacy_policy, icon = R.drawable.ic_privacy, url = "https://morningbird.eu"))
        legalInfoAboutItems.add(AboutItem("Copyright 2020: Michał Daniel", icon = R.drawable.ic_copyright))
        this.legalInfoAboutItems.postValue(legalInfoAboutItems)

        val engineInfoAboutItems: MutableList<AboutItem> = ArrayList()
        engineInfoAboutItems.add(AboutItem(R.string.about_source_code, icon = R.drawable.ic_source, url = "https://github.com"))
        engineInfoAboutItems.add(AboutItem(R.string.about_bug_reports, icon = R.drawable.ic_bug, url = "https://github.com"))
        engineInfoAboutItems.add(AboutItem(R.string.about_license, icon = R.drawable.ic_document))
        this.engineInfoAboutItems.postValue(engineInfoAboutItems)

        val attributionsInfoAboutItems: MutableList<AboutItem> = ArrayList()
        attributionsInfoAboutItems.add(AboutItem("Character assets by puppetbomb", icon = R.drawable.ic_attribution, url = "https://puppetbomb.itch.io/college-students-sprite-pack"))
        //Engine related attributions, do not remove unless you replace drawables
        attributionsInfoAboutItems.add(AboutItem("Icons made by Google", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/google"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Vitaly Gorbachev", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/vitaly-gorbachev"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Freepik", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/freepik"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Pixel perfect", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/pixel-perfect"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by itim2101", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/itim2101"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Gregor Cresnar", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/gregor-cresnar"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Smashicons", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/smashicons"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Kiranshastry", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/kiranshastry"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Pixelmeetup", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/pixelmeetup"))
        attributionsInfoAboutItems.add(AboutItem("Icons made by Dave Gandy", icon = R.drawable.ic_attribution, url = "https://www.flaticon.com/authors/dave-gandy"))
        this.attributionsInfoAboutItems.postValue(attributionsInfoAboutItems)
    }


}