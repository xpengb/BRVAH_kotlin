package com.allen.kotlinapp

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.allen.kotlinapp.adapter.PullToRefreshAdapter
import com.allen.kotlinapp.base.BaseActivity
import com.allen.kotlinapp.data.DataServer
import com.allen.kotlinapp.loadmore.CustomLoadMoreView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import org.jetbrains.anko.toast

/**
 * 文 件 名: PullToRefreshUseActivity
 * 创 建 人: Allen
 * 创建日期: 2017/6/20 11:24
 * 修改时间：
 * 修改备注：
 */
class PullToRefreshUseActivity : BaseActivity(), BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var pullToRefreshAdapter: PullToRefreshAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private val delayMillis = 1000

    private var mCurrentCounter = 0

    private var isErr: Boolean = false
    private var mLoadMoreEndGone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        mRecyclerView = findViewById(R.id.rv_list)
        mSwipeRefreshLayout = findViewById(R.id.swipeLayout)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189))
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        setTitle("Pull TO Refresh Use")
        setBackBtn()
        initAdapter()
        addHeadView()
    }

    private fun addHeadView() {
        val headView = layoutInflater.inflate(R.layout.head_view, mRecyclerView.parent as ViewGroup, false)
        (headView.findViewById(R.id.iv) as ImageView).visibility = View.GONE
        (headView.findViewById(R.id.tv) as TextView).text = "change load view"
        headView.setOnClickListener({
            mLoadMoreEndGone = true
            pullToRefreshAdapter.setLoadMoreView(CustomLoadMoreView())
            mRecyclerView.adapter = pullToRefreshAdapter
            toast( "change complete")
        })
        pullToRefreshAdapter.addHeaderView(headView)
    }

    override fun onLoadMoreRequested() {
        mSwipeRefreshLayout.isEnabled = false
        if (pullToRefreshAdapter.data.size < PAGE_SIZE) {
            pullToRefreshAdapter.loadMoreEnd(true)
        } else {
            if (mCurrentCounter >= TOTAL_COUNTER) {
                //                    pullToRefreshAdapter.loadMoreEnd();//default visible
                pullToRefreshAdapter.loadMoreEnd(mLoadMoreEndGone)//true is gone,false is visible
            } else {
                if (isErr) {
                    pullToRefreshAdapter.addData(DataServer.getSampleData(PAGE_SIZE))
                    mCurrentCounter = pullToRefreshAdapter.data.size
                    pullToRefreshAdapter.loadMoreComplete()
                } else {
                    isErr = true
                    toast( R.string.network_err)
                    pullToRefreshAdapter.loadMoreFail()

                }
            }
            mSwipeRefreshLayout.isEnabled = true
        }
    }

    override fun onRefresh() {
        pullToRefreshAdapter.setEnableLoadMore(false)
        Handler().postDelayed({
            pullToRefreshAdapter.setNewData(DataServer.getSampleData(PAGE_SIZE))
            isErr = false
            mCurrentCounter = PAGE_SIZE
            mSwipeRefreshLayout.isRefreshing = false
            pullToRefreshAdapter.setEnableLoadMore(true)
        }, delayMillis.toLong())
    }

    private fun initAdapter() {
        pullToRefreshAdapter = PullToRefreshAdapter()
        pullToRefreshAdapter.setOnLoadMoreListener(this, mRecyclerView)
        pullToRefreshAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        //        pullToRefreshAdapter.setPreLoadNumber(3);
        mRecyclerView.adapter = pullToRefreshAdapter
        mCurrentCounter = pullToRefreshAdapter.data.size

        mRecyclerView.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
               toast( Integer.toString(position))
            }
        })
    }

    companion object {

        private val TOTAL_COUNTER = 18

        private val PAGE_SIZE = 6
    }


}
