package com.nexus.xs.launcher

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.content.pm.ResolveInfo
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private lateinit var grid: GridLayout
    private lateinit var search: EditText

    private val apps: List<ResolveInfo>
        get() = packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            },
            0
        ).sortedBy {
            it.loadLabel(packageManager).toString().lowercase()
        }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        buildLauncher()
    }

    private fun buildLauncher() {

        val dark = LauncherSettings.isDarkMode(this)

        val background = if (dark) {
            Color.rgb(12, 12, 14)
        } else {
            Color.rgb(242, 242, 247)
        }

        val textColor = if (dark) Color.WHITE else Color.BLACK

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 28, 16, 12)
            setBackgroundColor(background)
        }

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val clock = TextView(this).apply {
            text = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())

            textSize = 22f
            setTextColor(textColor)
        }

        top.addView(
            clock,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        val settings = TextView(this).apply {
            text = "⚙"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(textColor)

            setOnClickListener {
                LauncherSettings.setDarkMode(
                    this@MainActivity,
                    !LauncherSettings.isDarkMode(
                        this@MainActivity
                    )
                )
                recreate()
            }
        }

        top.addView(
            settings,
            LinearLayout.LayoutParams(55, 55)
        )

        root.addView(top)

        search = EditText(this).apply {

            hint = "Search"
            textSize = 16f

            setSingleLine(true)
            setPadding(20, 0, 20, 0)

            setTextColor(textColor)

            setHintTextColor(
                if (dark) Color.LTGRAY else Color.GRAY
            )

            setBackground(
                GradientDrawable().apply {
                    setColor(
                        if (dark) {
                            Color.rgb(40, 40, 43)
                        } else {
                            Color.WHITE
                        }
                    )

                    cornerRadius = 50f
                }
            )
        }

        root.addView(
            search,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            ).apply {
                topMargin = 10
            }
        )

        val scroll = ScrollView(this)

        grid = GridLayout(this).apply {
            columnCount = 4
            setPadding(4, 16, 4, 16)
        }

        scroll.addView(
            grid,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        createDock(dark)

        search.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    refreshApps()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )

        setContentView(root)

        refreshApps()
    }

    private fun refreshApps() {

        grid.removeAllViews()

        val query = search.text.toString()

        apps.filter {
            it.loadLabel(packageManager)
                .toString()
                .contains(query, true)
        }.forEach {
            grid.addView(createApp(it))
        }
    }

    private fun createApp(
        info: ResolveInfo
    ): LinearLayout {

        val dark = LauncherSettings.isDarkMode(this)

        val item = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            setPadding(4, 8, 4, 10)

            setOnClickListener {

                packageManager
                    .getLaunchIntentForPackage(
                        info.activityInfo.packageName
                    )
                    ?.let {
                        startActivity(it)
                    }
            }
        }

        val icon = ImageView(this).apply {

            setImageDrawable(
                info.loadIcon(packageManager)
            )

            scaleType =
                ImageView.ScaleType.CENTER_INSIDE
        }

        item.addView(
            icon,
            LinearLayout.LayoutParams(
                64,
                64
            )
        )

        val name = TextView(this).apply {

            text = info.loadLabel(packageManager)

            textSize = 12f
            gravity = Gravity.CENTER

            setTextColor(
                if (dark) Color.WHITE else Color.BLACK
            )

            maxLines = 1

            ellipsize =
                android.text.TextUtils.TruncateAt.END
        }

        item.addView(
            name,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                28
            )
        )

        item.layoutParams =
            GridLayout.LayoutParams().apply {

                width = 0

                height =
                    GridLayout.LayoutParams.WRAP_CONTENT

                columnSpec =
                    GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                    )
            }

        return item
    }

    private fun createDock(dark: Boolean) {

        val dock = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER

            setPadding(8, 8, 8, 8)

            background =
                GradientDrawable().apply {

                    setColor(
                        if (dark) {
                            Color.argb(
                                220,
                                45,
                                45,
                                48
                            )
                        } else {
                            Color.argb(
                                220,
                                255,
                                255,
                                255
                            )
                        }
                    )

                    cornerRadius = 48f
                }
        }

        apps.take(4).forEach { info ->

            val button = ImageButton(this).apply {

                setImageDrawable(
                    info.loadIcon(packageManager)
                )

                background = null

                scaleType =
                    ImageView.ScaleType.CENTER_INSIDE

                setOnClickListener {

                    packageManager
                        .getLaunchIntentForPackage(
                            info.activityInfo.packageName
                        )
                        ?.let {
                            startActivity(it)
                        }
                }
            }

            dock.addView(
                button,
                LinearLayout.LayoutParams(
                    62,
                    62
                ).apply {
                    setMargins(5, 0, 5, 0)
                }
            )
        }

        root.addView(
            dock,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                78
            ).apply {
                topMargin = 8
            }
        )
    }
}
