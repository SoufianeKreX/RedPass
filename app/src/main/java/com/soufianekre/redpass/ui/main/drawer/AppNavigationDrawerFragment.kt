package com.soufianekre.redpass.ui.main.drawer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.soufianekre.redpass.R
import com.soufianekre.redpass.R.string
import com.soufianekre.redpass.data.db.models.Label
import com.soufianekre.redpass.ui.base.BaseFragment
import com.soufianekre.redpass.ui.labels.LabelsActivity
import com.soufianekre.redpass.ui.main.MainActivity
import com.soufianekre.redpass.ui.main.drawer.adapters.DrawerLabelsAdapter
import com.soufianekre.redpass.ui.passwords.PasswordListFragment
import kotlinx.android.synthetic.main.nav_drawer_fragment.view.*


class AppNavigationDrawerFragment: BaseFragment(),DrawerMvp.View{


    private val NAV_VIEW_FRAGMENT_TAG: String? = "nav_view_fragment"
    private lateinit var drawer_view :View


    @BindView(R.id.drawer_item_all)
    lateinit var showAll : View
    @BindView(R.id.drawer_item_settings)
    lateinit var settings:View
    @BindView(R.id.drawer_item_edit_labels)
    lateinit var editLabels : View
    @BindView(R.id.left_drawer)
    lateinit var leftDrawer:View


    @BindView(R.id.drawer_labels_list_view)
    lateinit var drawerLabelsListView : RecyclerView

    lateinit var drawerLabelAdapter : DrawerLabelsAdapter


    var mDrawerToggle: ActionBarDrawerToggle? = null
    var mDrawerLayout: DrawerLayout? = null
    private var mActivity: MainActivity? = null
    private val alreadyInitialized: Boolean = false

    lateinit var mPresenter: DrawerPresenter<DrawerMvp.View>

    companion object{
        fun newInstance(): AppNavigationDrawerFragment{
            val fragment = AppNavigationDrawerFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        drawer_view= inflater.inflate(R.layout.nav_drawer_fragment,container,false)
        mPresenter = DrawerPresenter()
        mPresenter.onAttach(this)
        ButterKnife.bind(this,drawer_view)
        return drawer_view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity as MainActivity?
        init()
    }

    override fun onResume() {
        super.onResume()
        refreshMenus()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDetach()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawerLabelAdapter =
            DrawerLabelsAdapter(
                requireActivity(),
                ArrayList(),
                mPresenter
            )
        drawerLabelsListView.setHasFixedSize(true)

        drawerLabelsListView.layoutManager = LinearLayoutManager(requireActivity(),VERTICAL,false)
        drawerLabelsListView.adapter = drawerLabelAdapter


        setListeners()
    }

    override fun loadPasswordItemList(label: Label) {
        getMainActivity().loadFragment(PasswordListFragment.newInstance(label))
        getMainActivity().getDrawerLayout().closeDrawer(GravityCompat.START)

    }

    override fun buildLabelsMenu(labelList : List<Label>) {
        drawerLabelAdapter.addAll(labelList)
        mDrawerToggle!!.syncState()
    }

    override fun openLabelsActivity(){
        val intent = Intent(requireContext(),LabelsActivity::class.java)
        startActivity(intent)
        getMainActivity().getDrawerLayout().closeDrawer(GravityCompat.START)

    }

    override fun showPasswordListFragment() {
        drawerLabelAdapter.selectedItemPosition = -1
        drawerLabelAdapter.notifyDataSetChanged()
        getMainActivity().loadFragment(PasswordListFragment.newInstance(null))
        getMainActivity().getDrawerLayout().closeDrawer(GravityCompat.START)

    }



    private fun init(){
        mDrawerLayout = getMainActivity().getDrawerLayout()
        mDrawerLayout!!.isFocusableInTouchMode = false

        leftDrawer.setPadding(
            leftDrawer.paddingLeft, leftDrawer.paddingTop, leftDrawer.paddingRight,
            10
        )

        // ActionBarDrawerToggle± ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = object : ActionBarDrawerToggle(
            mActivity,
            mDrawerLayout,
            getMainActivity().getToolbar(),
            string.navigation_drawer_open,
            string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                getMainActivity().invalidateOptionsMenu()
            }

        }

//        if (isDoublePanelActive()) {
//            mDrawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
//        }

        // Styling options
        //mDrawerLayout!!.setDrawerShadow(drawable.drawer_shadow, GravityCompat.START)
        mDrawerLayout!!.addDrawerListener(mDrawerToggle as ActionBarDrawerToggle)
        (mDrawerToggle as ActionBarDrawerToggle).isDrawerIndicatorEnabled = true

    }


    private fun refreshMenus(){
        buildMainMenu()
        Log.v(NAV_VIEW_FRAGMENT_TAG,"Finished main menu initialization")
        mPresenter.getLabels()
        Log.v(NAV_VIEW_FRAGMENT_TAG,"Finished categories menu initialization")

    }


    private fun buildMainMenu() {
        drawer_view.drawer_item_settings.setOnClickListener{
            mPresenter.onDrawerItemClicked(it.id)
        }

    }



    private fun getMainActivity(): MainActivity {
        return (activity as MainActivity?)!!
    }




    private fun setListeners(){
        editLabels.setOnClickListener{
            mPresenter.onDrawerItemClicked(editLabels.id)
        }
        showAll.setOnClickListener{
            mPresenter.onDrawerItemClicked(showAll.id)
        }
    }



}